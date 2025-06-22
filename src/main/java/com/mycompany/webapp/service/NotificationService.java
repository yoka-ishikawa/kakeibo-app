package com.mycompany.webapp.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Map;

/**
 * 通知サービス
 * デプロイ結果やシステム状態をLINEで通知
 */
@Service
public class NotificationService {

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * デプロイ成功通知をLINEで送信
     */
    public void sendDeploySuccessNotification(String serviceName, String commitId, String deployTime) {
        String message = createSuccessFlexMessage(serviceName, commitId, deployTime);
        sendLineMessage(message);
    }

    /**
     * デプロイ失敗通知をLINEで送信
     */
    public void sendDeployFailureNotification(String serviceName, String commitId, String errorMessage, String deployTime) {
        String message = createFailureFlexMessage(serviceName, commitId, errorMessage, deployTime);
        sendLineMessage(message);
    }

    /**
     * デプロイ開始通知をLINEで送信
     */
    public void sendDeployStartNotification(String serviceName, String commitId, String deployTime) {
        String message = createStartFlexMessage(serviceName, commitId, deployTime);
        sendLineMessage(message);
    }

    /**
     * アプリケーション起動完了通知をLINEで送信
     */
    public void sendApplicationStartedNotification(String timestamp, String environment, String port) {
        String message = createApplicationStartedFlexMessage(timestamp, environment, port);
        sendLineMessage(message);
    }

    /**
     * LINE Bot APIに直接メッセージを送信
     */
    private void sendLineMessage(String flexMessage) {
        try {
            String url = "http://localhost:3000/broadcast-flex"; // MCP LINE Bot サーバーのURL
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, Object> payload = Map.of("message", flexMessage);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            
            restTemplate.postForObject(url, request, String.class);
            System.out.println("LINE通知送信完了");
        } catch (Exception e) {
            System.err.println("LINE通知送信エラー: " + e.getMessage());
        }
    }

    /**
     * デプロイ成功用のFlexメッセージを作成
     */
    private String createSuccessFlexMessage(String serviceName, String commitId, String deployTime) {
        return String.format("""
            {
              "altText": "✅ デプロイ成功 - %s",
              "contents": {
                "type": "bubble",
                "size": "kilo",
                "header": {
                  "type": "box",
                  "layout": "vertical",
                  "backgroundColor": "#27ae60",
                  "paddingTop": "16px",
                  "paddingBottom": "16px",
                  "contents": [
                    {
                      "type": "text",
                      "text": "✅ デプロイ成功",
                      "color": "#ffffff",
                      "size": "lg",
                      "weight": "bold"
                    },
                    {
                      "type": "text",
                      "text": "%s",
                      "color": "#ffffff",
                      "size": "sm"
                    }
                  ]
                },
                "body": {
                  "type": "box",
                  "layout": "vertical",
                  "spacing": "sm",
                  "paddingAll": "16px",
                  "contents": [
                    {
                      "type": "text",
                      "text": "本番環境への新しいバージョンのデプロイが正常に完了しました",
                      "size": "md",
                      "weight": "bold",
                      "color": "#2c3e50",
                      "wrap": true,
                      "margin": "sm"
                    },
                    {
                      "type": "separator",
                      "margin": "lg"
                    },
                    {
                      "type": "box",
                      "layout": "vertical",
                      "contents": [
                        {
                          "type": "text",
                          "text": "📋 デプロイ詳細",
                          "size": "sm",
                          "weight": "bold",
                          "color": "#27ae60",
                          "margin": "lg"
                        },
                        {
                          "type": "text",
                          "text": "• サービス: %s\\n• コミット: %s\\n• 完了時刻: %s\\n• ステータス: 正常稼働中",
                          "size": "xs",
                          "wrap": true,
                          "margin": "sm"
                        }
                      ]
                    },
                    {
                      "type": "separator",
                      "margin": "md"
                    },
                    {
                      "type": "text",
                      "text": "🎯 アプリケーションが正常に更新されました。新機能やバグ修正をご利用いただけます。",
                      "size": "xs",
                      "color": "#34495e",
                      "wrap": true,
                      "margin": "md"
                    }
                  ]
                },
                "footer": {
                  "type": "box",
                  "layout": "vertical",
                  "spacing": "sm",
                  "paddingAll": "16px",
                  "contents": [
                    {
                      "type": "button",
                      "style": "primary",
                      "height": "sm",
                      "color": "#27ae60",
                      "action": {
                        "type": "uri",
                        "label": "アプリにアクセス",
                        "uri": "https://your-app.onrender.com"
                      }
                    }
                  ]
                }
              }
            }
            """, serviceName, serviceName, serviceName, commitId, deployTime);
    }

    /**
     * デプロイ失敗用のFlexメッセージを作成
     */
    private String createFailureFlexMessage(String serviceName, String commitId, String errorMessage, String deployTime) {
        return String.format("""
            {
              "altText": "❌ デプロイ失敗 - %s",
              "contents": {
                "type": "bubble",
                "size": "kilo",
                "header": {
                  "type": "box",
                  "layout": "vertical",
                  "backgroundColor": "#e74c3c",
                  "paddingTop": "16px",
                  "paddingBottom": "16px",
                  "contents": [
                    {
                      "type": "text",
                      "text": "❌ デプロイ失敗",
                      "color": "#ffffff",
                      "size": "lg",
                      "weight": "bold"
                    },
                    {
                      "type": "text",
                      "text": "%s",
                      "color": "#ffffff",
                      "size": "sm"
                    }
                  ]
                },
                "body": {
                  "type": "box",
                  "layout": "vertical",
                  "spacing": "sm",
                  "paddingAll": "16px",
                  "contents": [
                    {
                      "type": "text",
                      "text": "デプロイ中にエラーが発生しました。至急確認が必要です。",
                      "size": "md",
                      "weight": "bold",
                      "color": "#2c3e50",
                      "wrap": true,
                      "margin": "sm"
                    },
                    {
                      "type": "separator",
                      "margin": "lg"
                    },
                    {
                      "type": "box",
                      "layout": "vertical",
                      "contents": [
                        {
                          "type": "text",
                          "text": "🔍 エラー詳細",
                          "size": "sm",
                          "weight": "bold",
                          "color": "#e74c3c",
                          "margin": "lg"
                        },
                        {
                          "type": "text",
                          "text": "• サービス: %s\\n• コミット: %s\\n• 失敗時刻: %s\\n• エラー: %s",
                          "size": "xs",
                          "wrap": true,
                          "margin": "sm"
                        }
                      ]
                    },
                    {
                      "type": "separator",
                      "margin": "md"
                    },
                    {
                      "type": "text",
                      "text": "⚠️ 前回のバージョンが引き続き稼働中です。ログを確認してエラーを修正してください。",
                      "size": "xs",
                      "color": "#e67e22",
                      "wrap": true,
                      "margin": "md"
                    }
                  ]
                },
                "footer": {
                  "type": "box",
                  "layout": "vertical",
                  "spacing": "sm",
                  "paddingAll": "16px",
                  "contents": [
                    {
                      "type": "button",
                      "style": "primary",
                      "height": "sm",
                      "color": "#e74c3c",
                      "action": {
                        "type": "uri",
                        "label": "Renderログ確認",
                        "uri": "https://dashboard.render.com"
                      }
                    }
                  ]
                }
              }
            }
            """, serviceName, serviceName, serviceName, commitId, deployTime, errorMessage);
    }

    /**
     * デプロイ開始用のFlexメッセージを作成
     */
    private String createStartFlexMessage(String serviceName, String commitId, String deployTime) {
        return String.format("""
            {
              "altText": "🚀 デプロイ開始 - %s",
              "contents": {
                "type": "bubble",
                "size": "kilo",
                "header": {
                  "type": "box",
                  "layout": "vertical",
                  "backgroundColor": "#3498db",
                  "paddingTop": "16px",
                  "paddingBottom": "16px",
                  "contents": [
                    {
                      "type": "text",
                      "text": "🚀 デプロイ開始",
                      "color": "#ffffff",
                      "size": "lg",
                      "weight": "bold"
                    },
                    {
                      "type": "text",
                      "text": "%s",
                      "color": "#ffffff",
                      "size": "sm"
                    }
                  ]
                },
                "body": {
                  "type": "box",
                  "layout": "vertical",
                  "spacing": "sm",
                  "paddingAll": "16px",
                  "contents": [
                    {
                      "type": "text",
                      "text": "新しいバージョンのデプロイを開始しました",
                      "size": "md",
                      "weight": "bold",
                      "color": "#2c3e50",
                      "wrap": true,
                      "margin": "sm"
                    },
                    {
                      "type": "separator",
                      "margin": "lg"
                    },
                    {
                      "type": "box",
                      "layout": "vertical",
                      "contents": [
                        {
                          "type": "text",
                          "text": "📋 デプロイ情報",
                          "size": "sm",
                          "weight": "bold",
                          "color": "#3498db",
                          "margin": "lg"
                        },
                        {
                          "type": "text",
                          "text": "• サービス: %s\\n• コミット: %s\\n• 開始時刻: %s\\n• ステータス: 進行中",
                          "size": "xs",
                          "wrap": true,
                          "margin": "sm"
                        }
                      ]
                    },
                    {
                      "type": "separator",
                      "margin": "md"
                    },
                    {
                      "type": "text",
                      "text": "⏳ デプロイ完了まで数分かかる場合があります。完了次第、結果をお知らせします。",
                      "size": "xs",
                      "color": "#34495e",
                      "wrap": true,
                      "margin": "md"
                    }
                  ]
                }
              }
            }
            """, serviceName, serviceName, serviceName, commitId, deployTime);
    }

    /**
     * アプリケーション起動完了用のFlexメッセージを作成
     */
    private String createApplicationStartedFlexMessage(String timestamp, String environment, String port) {
        return String.format("""
            {
              "altText": "🚀 アプリケーション起動完了",
              "contents": {
                "type": "bubble",
                "size": "kilo",
                "header": {
                  "type": "box",
                  "layout": "vertical",
                  "backgroundColor": "#2ecc71",
                  "paddingTop": "16px",
                  "paddingBottom": "16px",
                  "contents": [
                    {
                      "type": "text",
                      "text": "🚀 アプリ起動完了",
                      "color": "#ffffff",
                      "size": "lg",
                      "weight": "bold"
                    },
                    {
                      "type": "text",
                      "text": "家計簿アプリ",
                      "color": "#ffffff",
                      "size": "sm"
                    }
                  ]
                },
                "body": {
                  "type": "box",
                  "layout": "vertical",
                  "spacing": "sm",
                  "paddingAll": "16px",
                  "contents": [
                    {
                      "type": "box",
                      "layout": "vertical",
                      "spacing": "xs",
                      "contents": [
                        {
                          "type": "text",
                          "text": "システム情報",
                          "size": "sm",
                          "weight": "bold",
                          "color": "#2ecc71"
                        },
                        {
                          "type": "text",
                          "text": "• 起動時刻: %s\\n• 環境: %s\\n• ポート: %s\\n• ステータス: 稼働中",
                          "size": "xs",
                          "wrap": true,
                          "margin": "sm"
                        }
                      ]
                    },
                    {
                      "type": "separator",
                      "margin": "md"
                    },
                    {
                      "type": "text",
                      "text": "✅ データベース接続も正常に完了し、アプリケーションが利用可能な状態です。",
                      "size": "xs",
                      "color": "#27ae60",
                      "wrap": true,
                      "margin": "md"
                    }
                  ]
                },
                "footer": {
                  "type": "box",
                  "layout": "vertical",
                  "spacing": "xs",
                  "paddingAll": "12px",
                  "contents": [
                    {
                      "type": "button",
                      "action": {
                        "type": "uri",
                        "label": "アプリを開く",
                        "uri": "https://kakeibo-app.onrender.com"
                      },
                      "style": "primary",
                      "color": "#2ecc71"
                    }
                  ]
                }
              }
            }
            """, timestamp, environment, port);
    }
}
