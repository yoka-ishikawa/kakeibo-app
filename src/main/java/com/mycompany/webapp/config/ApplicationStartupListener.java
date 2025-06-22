package com.mycompany.webapp.config;

import com.mycompany.webapp.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * アプリケーション起動イベントリスナー デプロイ完了後の自動通知を担当
 */
@Component
public class ApplicationStartupListener {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private Environment environment;

    /**
     * アプリケーション完全起動後に実行される データベース接続も含めてすべての初期化が完了している状態
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        try {
            // プロダクション環境でのみ通知を送信
            String activeProfile = environment.getProperty("spring.profiles.active");
            if ("production".equals(activeProfile)) {
                System.out.println("=== アプリケーション起動完了通知 ===");

                // 環境変数から情報を取得
                String serviceName = System.getenv("RENDER_SERVICE_NAME");
                if (serviceName == null) {
                    serviceName = "家計簿アプリ";
                }

                String commitId = System.getenv("RENDER_GIT_COMMIT");
                if (commitId == null) {
                    commitId = "最新バージョン";
                } else {
                    // コミットIDを短縮表示
                    commitId = commitId.length() > 7 ? commitId.substring(0, 7) : commitId;
                }

                String deployTime = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

                // デプロイ成功通知を送信
                notificationService.sendDeploySuccessNotification(serviceName, commitId,
                        deployTime);

                System.out.println("デプロイ成功通知を送信しました");
            } else {
                System.out.println("開発環境のため、デプロイ通知はスキップします");
            }
        } catch (Exception e) {
            System.err.println("起動通知送信エラー: " + e.getMessage());
            // エラーが発生してもアプリケーション起動は継続
        }
    }
}
