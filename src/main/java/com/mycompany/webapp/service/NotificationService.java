package com.mycompany.webapp.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

/** 通知サービス デプロイ結果やシステム状態をLINEで通知 */
@Service
public class NotificationService {

  @Autowired
  private MCPLineBotService mcpLineBotService;

  /** デプロイ成功通知をLINEで送信 */
  public void sendDeploySuccessNotification(String serviceName, String commitId,
      String deployTime) {
    String message = createSuccessFlexMessage(serviceName, commitId, deployTime);
    sendLineMessage(message);
  }

  /** デプロイ失敗通知をLINEで送信 */
  public void sendDeployFailureNotification(String serviceName, String commitId,
      String errorMessage, String deployTime) {
    String message = createFailureFlexMessage(serviceName, commitId, errorMessage, deployTime);
    sendLineMessage(message);
  }

  /** デプロイ失敗通知をLINEで送信（詳細ログ付き） */
  public void sendDeployFailureNotificationWithLog(String serviceName, String commitId,
      String errorLog, String deployTime) {
    String analyzedError = analyzeDeploymentError(errorLog);
    String message = createFailureFlexMessageWithAnalysis(serviceName, commitId, analyzedError,
        errorLog, deployTime);
    sendLineMessage(message);
  }

  /** デプロイ開始通知をLINEで送信 */
  public void sendDeployStartNotification(String serviceName, String commitId, String deployTime) {
    String message = createStartFlexMessage(serviceName, commitId, deployTime);
    sendLineMessage(message);
  }

  /** アプリケーション起動完了通知をLINEで送信 */
  public void sendApplicationStartedNotification(String timestamp, String environment,
      String port) {
    String message = createApplicationStartedFlexMessage(timestamp, environment, port);
    sendLineMessage(message);
  }

  /** LINE Bot MCPを使用してブロードキャストメッセージを送信 */
  private void sendLineMessage(String flexMessage) {
    try {
      String channelAccessToken = System.getenv("LINE_CHANNEL_ACCESS_TOKEN");

      if (channelAccessToken == null || channelAccessToken.isEmpty()) {
        System.out.println("=== LINE通知設定未完了 ===");
        System.out.println("環境変数 LINE_CHANNEL_ACCESS_TOKEN が未設定です");
        System.out.println("メッセージ内容（ログのみ）: " + flexMessage);
        System.out.println("========================");
        return;
      }

      // MCP LINE Bot機能を使用してブロードキャストメッセージを送信
      System.out.println("=== LINE ブロードキャスト送信開始 ===");
      System.out.println("Channel Access Token: " + channelAccessToken.substring(0, 10) + "...");

      // MCPLineBotServiceを使用して実際にLINE送信
      boolean result = mcpLineBotService.sendBroadcastFlexMessage("家計簿アプリ通知", flexMessage);

      if (result) {
        System.out.println("LINE送信成功");
      } else {
        System.out.println("LINE送信失敗");
      }
      System.out.println("===================================");

    } catch (Exception e) {
      System.err.println("LINE通知送信エラー: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /** デプロイ失敗の詳細ログを解析してエラー要因を特定 */
  private String analyzeDeploymentError(String errorLog) {
    if (errorLog == null || errorLog.isEmpty()) {
      return "不明なエラー";
    }

    // PostgreSQL接続エラーの検出
    if (errorLog.contains("java.io.EOFException") && errorLog.contains("postgresql")) {
      return "PostgreSQL SSL接続エラー (EOFException)";
    }

    if (errorLog.contains("Connection refused")) {
      return "データベース接続拒否";
    }

    if (errorLog.contains("UnknownHostException")) {
      return "データベースホスト名解決エラー";
    }

    if (errorLog.contains("SocketTimeoutException")) {
      return "データベース接続タイムアウト";
    }

    if (errorLog.contains("BeanCreationException")) {
      return "Spring Bean作成エラー";
    }

    if (errorLog.contains("DataSourceBeanCreationException")) {
      return "データソース設定エラー";
    }

    if (errorLog.contains("sslmode")) {
      return "SSL接続設定問題";
    }

    return "アプリケーション起動エラー";
  }

  /** デプロイ成功用のFlexメッセージを作成 */
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

  /** デプロイ失敗用のFlexメッセージを作成 */
  private String createFailureFlexMessage(String serviceName, String commitId, String errorMessage,
      String deployTime) {
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

  /** デプロイ開始用のFlexメッセージを作成 */
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

  /** アプリケーション起動完了用のFlexメッセージを作成 */
  private String createApplicationStartedFlexMessage(String timestamp, String environment,
      String port) {
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
        }                """, timestamp, environment, port);
  }

  /** デプロイ失敗用のFlexメッセージを作成（詳細解析付き） */
  private String createFailureFlexMessageWithAnalysis(String serviceName, String commitId,
      String analyzedError, String errorLog, String deployTime) {
    // エラーログを最初の5行に制限
    String truncatedLog = truncateErrorLog(errorLog, 5);

    return String.format("""
        {
          "altText": "❌ デプロイ失敗 - %s",
          "contents": {
            "type": "bubble",
            "size": "mega",
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
              "spacing": "md",
              "paddingAll": "16px",
              "contents": [
                {
                  "type": "box",
                  "layout": "vertical",
                  "spacing": "xs",
                  "contents": [
                    {
                      "type": "text",
                      "text": "🔍 エラー解析",
                      "size": "sm",
                      "weight": "bold",
                      "color": "#e74c3c"
                    },
                    {
                      "type": "text",
                      "text": "%s",
                      "size": "xs",
                      "wrap": true,
                      "margin": "sm",
                      "color": "#c0392b"
                    }
                  ]
                },
                {
                  "type": "separator",
                  "margin": "md"
                },
                {
                  "type": "box",
                  "layout": "vertical",
                  "spacing": "xs",
                  "contents": [
                    {
                      "type": "text",
                      "text": "📋 デプロイ情報",
                      "size": "sm",
                      "weight": "bold",
                      "color": "#2c3e50"
                    },
                    {
                      "type": "text",
                      "text": "• サービス: %s\\n• コミット: %s\\n• 失敗時刻: %s",
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
                  "type": "box",
                  "layout": "vertical",
                  "spacing": "xs",
                  "contents": [
                    {
                      "type": "text",
                      "text": "🔧 エラーログ (抜粋)",
                      "size": "sm",
                      "weight": "bold",
                      "color": "#7f8c8d"
                    },
                    {
                      "type": "text",
                      "text": "%s",
                      "size": "xxs",
                      "wrap": true,
                      "margin": "sm",
                      "color": "#95a5a6"
                    }
                  ]
                },
                {
                  "type": "separator",
                  "margin": "md"
                },
                {
                  "type": "text",
                  "text": "🚨 すぐに環境変数とデータベース接続設定を確認し、再デプロイを実行してください。",
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
              "spacing": "xs",
              "paddingAll": "12px",
              "contents": [
                {
                  "type": "button",
                  "action": {
                    "type": "uri",
                    "label": "Renderログを確認",
                    "uri": "https://dashboard.render.com"
                  },
                  "style": "primary",
                  "color": "#e74c3c"
                }
              ]
            }
          }
        }
        """, serviceName, serviceName, analyzedError, serviceName, commitId, deployTime,
        truncatedLog);
  }

  /** エラーログを指定された行数に制限 */
  private String truncateErrorLog(String errorLog, int maxLines) {
    if (errorLog == null || errorLog.isEmpty()) {
      return "ログが取得できませんでした";
    }

    String[] lines = errorLog.split("\n");
    if (lines.length <= maxLines) {
      return errorLog;
    }

    StringBuilder truncated = new StringBuilder();
    for (int i = 0; i < maxLines; i++) {
      truncated.append(lines[i]).append("\n");
    }
    truncated.append("... (以下 ").append(lines.length - maxLines).append(" 行省略)");

    return truncated.toString();
  }

  /** 詳細な接続エラー情報をLINEに送信 */
  public void sendConnectionErrorDetails(String errorType, String errorMessage, String diagnostic) {
    try {
      String message = "🚨 DB接続エラー詳細\\n" + "時刻: "
          + java.time.LocalDateTime.now()
              .format(java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))
          + "\\n" + "エラータイプ: " + (errorType != null ? errorType : "不明") + "\\n" + "メッセージ: "
          + (errorMessage != null ? errorMessage.substring(0, Math.min(errorMessage.length(), 100))
              : "詳細なし")
          + "\\n" + "診断情報: "
          + (diagnostic != null ? diagnostic.substring(0, Math.min(diagnostic.length(), 100))
              : "診断情報なし")
          + "\\n" + "💡対処: Renderダッシュボードで環境変数・DBサービス状態を確認";

      sendLineMessage(message);
      System.out.println("📱 詳細接続エラー情報をLINEに送信しました");

    } catch (Exception e) {
      System.err.println("❌ LINE通知送信エラー: " + e.getMessage());
    }
  }
}
