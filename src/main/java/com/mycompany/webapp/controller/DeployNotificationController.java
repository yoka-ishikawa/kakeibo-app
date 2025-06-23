package com.mycompany.webapp.controller;

import com.mycompany.webapp.service.NotificationService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Renderデプロイ通知用コントローラー Renderのデプロイフック機能と連携してLINE通知を送信 */
@RestController
@RequestMapping("/api/deploy")
public class DeployNotificationController {

    @Autowired
    private NotificationService notificationService;

    /** Renderデプロイ成功通知 */
    @PostMapping("/success")
    public ResponseEntity<Map<String, String>> deploySuccess(
            @RequestBody Map<String, Object> payload) {
        try {
            System.out.println("=== デプロイ成功通知受信 ===");
            System.out.println("Payload received: " + payload.keySet());

            String serviceName = payload.getOrDefault("service", "家計簿アプリ").toString();
            String commitId = payload.getOrDefault("commit", "不明").toString();
            String deployTime =
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

            // LINE通知送信
            notificationService.sendDeploySuccessNotification(serviceName, commitId, deployTime);

            return ResponseEntity.ok(Map.of("status", "success", "message", "デプロイ成功通知を送信しました"));
        } catch (Exception e) {
            System.err.println("デプロイ成功通知エラー: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("status", "error", "message", "通知送信に失敗しました: " + e.getMessage()));
        }
    }

    /** Renderデプロイ失敗通知 */
    @PostMapping("/failure")
    public ResponseEntity<Map<String, String>> deployFailure(
            @RequestBody Map<String, Object> payload) {
        try {
            System.out.println("=== デプロイ失敗通知受信 ===");
            System.out.println("Payload received: " + payload.keySet());

            String serviceName = payload.getOrDefault("service", "家計簿アプリ").toString();
            String commitId = payload.getOrDefault("commit", "不明").toString();
            String errorMessage = payload.getOrDefault("error", "詳細不明").toString();
            String deployTime =
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

            // LINE通知送信
            notificationService.sendDeployFailureNotification(serviceName, commitId, errorMessage,
                    deployTime);

            return ResponseEntity.ok(Map.of("status", "success", "message", "デプロイ失敗通知を送信しました"));
        } catch (Exception e) {
            System.err.println("デプロイ失敗通知エラー: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("status", "error", "message", "通知送信に失敗しました: " + e.getMessage()));
        }
    }

    /** デプロイ開始通知 */
    @PostMapping("/started")
    public ResponseEntity<Map<String, String>> deployStarted(
            @RequestBody Map<String, Object> payload) {
        try {
            System.out.println("=== デプロイ開始通知受信 ===");
            System.out.println("Payload received: " + payload.keySet());

            String serviceName = payload.getOrDefault("service", "家計簿アプリ").toString();
            String commitId = payload.getOrDefault("commit", "不明").toString();
            String deployTime =
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

            // LINE通知送信
            notificationService.sendDeployStartNotification(serviceName, commitId, deployTime);

            return ResponseEntity.ok(Map.of("status", "success", "message", "デプロイ開始通知を送信しました"));
        } catch (Exception e) {
            System.err.println("デプロイ開始通知エラー: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("status", "error", "message", "通知送信に失敗しました: " + e.getMessage()));
        }
    }

    /** 通知テスト用エンドポイント */
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> testNotification() {
        try {
            String deployTime =
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
            notificationService.sendDeploySuccessNotification("家計簿アプリ（テスト）", "test-commit",
                    deployTime);

            return ResponseEntity.ok(Map.of("status", "success", "message", "テスト通知を送信しました"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    Map.of("status", "error", "message", "テスト通知送信に失敗しました: " + e.getMessage()));
        }
    }

    /** デプロイ失敗通知（詳細ログ付き） */
    @PostMapping("/failure-with-log")
    public ResponseEntity<Map<String, String>> deployFailureWithLog(
            @RequestBody Map<String, Object> payload) {
        try {
            System.out.println("=== デプロイ失敗通知（詳細ログ付き）受信 ===");
            System.out.println("Payload received: " + payload.keySet());

            String serviceName = payload.getOrDefault("service", "家計簿アプリ").toString();
            String commitId = payload.getOrDefault("commit", "不明").toString();
            String errorLog = payload.getOrDefault("errorLog", "ログが取得できませんでした").toString();
            String deployTime =
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

            // 詳細ログ付きLINE通知送信
            notificationService.sendDeployFailureNotificationWithLog(serviceName, commitId,
                    errorLog, deployTime);

            return ResponseEntity
                    .ok(Map.of("status", "success", "message", "詳細ログ付きデプロイ失敗通知を送信しました"));
        } catch (Exception e) {
            System.err.println("詳細ログ付きデプロイ失敗通知エラー: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("status", "error", "message", "通知送信に失敗しました: " + e.getMessage()));
        }
    }
}
