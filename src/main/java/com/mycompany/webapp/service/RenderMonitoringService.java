package com.mycompany.webapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/** Render環境の監視・デプロイ状況確認・自動通知サービス Render REST APIを直接呼び出してデプロイ状況やログを監視 */
@Service
public class RenderMonitoringService {

  private static final Logger logger = LoggerFactory.getLogger(RenderMonitoringService.class);

  // 実際のRenderサービスID（MCPから取得した情報）
  private static final String RENDER_SERVICE_ID = "srv-d12hh8mmcj7s73fc6170";
  private static final String RENDER_API_BASE_URL = "https://api.render.com/v1";

  @Value("${app.render.monitoring.enabled:true}")
  private boolean monitoringEnabled;

  @Value("${app.render.monitoring.check-interval:300}")
  private int checkIntervalSeconds;

  @Autowired private NotificationService notificationService;

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final RestTemplate restTemplate = new RestTemplate();

  /** デプロイ状況の監視を開始 */
  @Async
  public CompletableFuture<Void> startDeploymentMonitoring() {
    if (!monitoringEnabled) {
      logger.warn("Render monitoring is disabled");
      return CompletableFuture.completedFuture(null);
    }

    String apiKey = System.getenv("RENDER_API_KEY");
    if (apiKey == null || apiKey.isEmpty()) {
      logger.warn("RENDER_API_KEY not configured - monitoring disabled");
      return CompletableFuture.completedFuture(null);
    }

    logger.info("Starting Render deployment monitoring for service: {}", RENDER_SERVICE_ID);

    try {
      // 最新のデプロイ状況を確認
      String latestDeployStatus = checkLatestDeploymentStatus(apiKey);

      if ("live".equals(latestDeployStatus)) {
        sendDeploymentSuccessNotification();
      } else if ("build_failed".equals(latestDeployStatus)
          || "deploy_failed".equals(latestDeployStatus)
          || "update_failed".equals(latestDeployStatus)) {
        String errorLogs = getRecentErrorLogs(apiKey);
        sendDeploymentFailureNotification(latestDeployStatus, errorLogs);
      } else if ("building".equals(latestDeployStatus) || "deploying".equals(latestDeployStatus)) {
        logger.info("Deployment in progress: {}", latestDeployStatus);
        sendDeploymentInProgressNotification(latestDeployStatus);
      }
    } catch (Exception e) {
      logger.error("Error during deployment monitoring", e);
      sendMonitoringErrorNotification(e.getMessage());
    }

    return CompletableFuture.completedFuture(null);
  }

  /** Render REST APIを直接呼び出して最新のデプロイ状況を確認 */
  private String checkLatestDeploymentStatus(String apiKey) {
    try {
      logger.info("Checking latest deployment status for service: {}", RENDER_SERVICE_ID);

      // Render API: List Deployments
      String url = RENDER_API_BASE_URL + "/services/" + RENDER_SERVICE_ID + "/deploys?limit=1";

      HttpHeaders headers = new HttpHeaders();
      headers.set("Authorization", "Bearer " + apiKey);
      headers.set("Accept", "application/json");

      HttpEntity<?> entity = new HttpEntity<>(headers);

      ResponseEntity<String> response =
          restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

      if (response.getStatusCode().is2xxSuccessful()) {
        JsonNode deploysJson = objectMapper.readTree(response.getBody());
        if (deploysJson.isArray() && deploysJson.size() > 0) {
          JsonNode latestDeploy = deploysJson.get(0);
          String status = latestDeploy.get("status").asText();
          logger.info("Latest deployment status: {}", status);
          return status;
        }
      }

      logger.warn("No deployments found or API call failed");
      return "unknown";

    } catch (Exception e) {
      logger.error("Failed to check deployment status via Render API", e);
      return "unknown";
    }
  }

  /** Render REST APIでエラーログを取得 */
  private String getRecentErrorLogs(String apiKey) {
    try {
      logger.info("Fetching recent error logs for service: {}", RENDER_SERVICE_ID);

      // Render API: List Logs with error filter
      String url =
          RENDER_API_BASE_URL
              + "/logs"
              + "?resource="
              + RENDER_SERVICE_ID
              + "&level=error"
              + "&limit=5";

      HttpHeaders headers = new HttpHeaders();
      headers.set("Authorization", "Bearer " + apiKey);
      headers.set("Accept", "application/json");

      HttpEntity<?> entity = new HttpEntity<>(headers);

      ResponseEntity<String> response =
          restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

      if (response.getStatusCode().is2xxSuccessful()) {
        JsonNode logsJson = objectMapper.readTree(response.getBody());
        JsonNode logs = logsJson.get("logs");

        if (logs != null && logs.isArray() && logs.size() > 0) {
          StringBuilder errorMessages = new StringBuilder();
          for (JsonNode log : logs) {
            String message = log.get("message").asText();
            errorMessages.append(message).append("\n");
          }
          return errorMessages.toString();
        }
      }

      return "No recent error logs found";

    } catch (Exception e) {
      logger.error("Failed to fetch error logs", e);
      return "Error logs could not be retrieved: " + e.getMessage();
    }
  }

  /** デプロイ成功通知 */
  private void sendDeploymentSuccessNotification() {
    try {
      String timestamp =
          LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

      // 既存のメソッドを使用
      notificationService.sendDeploySuccessNotification("kakeibo-app", "latest", timestamp);

      logger.info("Deployment success notification sent");

    } catch (Exception e) {
      logger.error("Failed to send deployment success notification", e);
    }
  }

  /** デプロイ失敗通知 */
  private void sendDeploymentFailureNotification(String status, String errorLogs) {
    try {
      String timestamp =
          LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

      // 既存のメソッドを使用
      notificationService.sendDeployFailureNotificationWithLog(
          "kakeibo-app", "latest", errorLogs, timestamp);

      logger.error("Deployment failure notification sent for status: {}", status);

    } catch (Exception e) {
      logger.error("Failed to send deployment failure notification", e);
    }
  }

  /** デプロイ進行中通知 */
  private void sendDeploymentInProgressNotification(String status) {
    try {
      String timestamp =
          LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

      // 既存のメソッドを使用
      notificationService.sendDeployStartNotification("kakeibo-app", "latest", timestamp);

      logger.info("Deployment in progress notification sent for status: {}", status);

    } catch (Exception e) {
      logger.error("Failed to send deployment in progress notification", e);
    }
  }

  /** 監視エラー通知 */
  private void sendMonitoringErrorNotification(String errorMessage) {
    try {
      String timestamp =
          LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

      // 既存のメソッドを使用
      notificationService.sendConnectionErrorDetails(
          "Monitoring Error", errorMessage, "Render API monitoring failed at " + timestamp);

      logger.error("Monitoring error notification sent: {}", errorMessage);

    } catch (Exception e) {
      logger.error("Failed to send monitoring error notification", e);
    }
  }

  /** デプロイ成功用Flexメッセージ作成 */
  private String createDeploymentSuccessFlexMessage(String timestamp) {
    try {
      return String.format(
          """
                    {
                      "type": "bubble",
                      "hero": {
                        "type": "box",
                        "layout": "vertical",
                        "contents": [
                          {
                            "type": "text",
                            "text": "🎉",
                            "size": "xxl",
                            "align": "center"
                          },
                          {
                            "type": "text",
                            "text": "デプロイ成功！",
                            "weight": "bold",
                            "size": "xl",
                            "align": "center",
                            "color": "#27AE60"
                          }
                        ],
                        "backgroundColor": "#E8F8F5",
                        "paddingAll": "20px"
                      },
                      "body": {
                        "type": "box",
                        "layout": "vertical",
                        "contents": [
                          {
                            "type": "text",
                            "text": "家計簿アプリが正常にデプロイされました",
                            "weight": "bold",
                            "size": "md",
                            "wrap": true
                          },
                          {
                            "type": "separator",
                            "margin": "md"
                          },
                          {
                            "type": "box",
                            "layout": "vertical",
                            "contents": [
                              {
                                "type": "box",
                                "layout": "baseline",
                                "contents": [
                                  {
                                    "type": "text",
                                    "text": "⏰ 時刻",
                                    "size": "sm",
                                    "color": "#666666",
                                    "flex": 2
                                  },
                                  {
                                    "type": "text",
                                    "text": "%s",
                                    "size": "sm",
                                    "wrap": true,
                                    "flex": 3
                                  }
                                ]
                              },
                              {
                                "type": "box",
                                "layout": "baseline",
                                "contents": [
                                  {
                                    "type": "text",
                                    "text": "🚀 サービス",
                                    "size": "sm",
                                    "color": "#666666",
                                    "flex": 2
                                  },
                                  {
                                    "type": "text",
                                    "text": "%s",
                                    "size": "sm",
                                    "wrap": true,
                                    "flex": 3
                                  }
                                ]
                              },
                              {
                                "type": "box",
                                "layout": "baseline",
                                "contents": [
                                  {
                                    "type": "text",
                                    "text": "📊 ステータス",
                                    "size": "sm",
                                    "color": "#666666",
                                    "flex": 2
                                  },
                                  {
                                    "type": "text",
                                    "text": "LIVE ✅",
                                    "size": "sm",
                                    "color": "#27AE60",
                                    "weight": "bold",
                                    "flex": 3
                                  }
                                ]
                              }
                            ],
                            "spacing": "sm",
                            "margin": "md"
                          }
                        ]
                      },
                      "footer": {
                        "type": "box",
                        "layout": "vertical",
                        "contents": [
                          {
                            "type": "text",
                            "text": "アプリが正常に稼働中です",
                            "size": "sm",
                            "color": "#27AE60",
                            "align": "center"
                          }
                        ],
                        "backgroundColor": "#E8F8F5",
                        "paddingAll": "10px"
                      }
                    }
                    """,
          timestamp, RENDER_SERVICE_ID);
    } catch (Exception e) {
      logger.error("Failed to create deployment success flex message", e);
      return "{}";
    }
  }

  /** デプロイ失敗用Flexメッセージ作成 */
  private String createDeploymentFailureFlexMessage(
      String status, String errorLogs, String timestamp) {
    try {
      // エラーログを短縮（LINE制限対応）
      String shortErrorLogs =
          errorLogs.length() > 100 ? errorLogs.substring(0, 100) + "..." : errorLogs;

      return String.format(
          """
                    {
                      "type": "bubble",
                      "hero": {
                        "type": "box",
                        "layout": "vertical",
                        "contents": [
                          {
                            "type": "text",
                            "text": "❌",
                            "size": "xxl",
                            "align": "center"
                          },
                          {
                            "type": "text",
                            "text": "デプロイ失敗",
                            "weight": "bold",
                            "size": "xl",
                            "align": "center",
                            "color": "#E74C3C"
                          }
                        ],
                        "backgroundColor": "#FADBD8",
                        "paddingAll": "20px"
                      },
                      "body": {
                        "type": "box",
                        "layout": "vertical",
                        "contents": [
                          {
                            "type": "text",
                            "text": "家計簿アプリのデプロイでエラーが発生しました",
                            "weight": "bold",
                            "size": "md",
                            "wrap": true,
                            "color": "#E74C3C"
                          },
                          {
                            "type": "separator",
                            "margin": "md"
                          },
                          {
                            "type": "box",
                            "layout": "vertical",
                            "contents": [
                              {
                                "type": "box",
                                "layout": "baseline",
                                "contents": [
                                  {
                                    "type": "text",
                                    "text": "⏰ 時刻",
                                    "size": "sm",
                                    "color": "#666666",
                                    "flex": 2
                                  },
                                  {
                                    "type": "text",
                                    "text": "%s",
                                    "size": "sm",
                                    "wrap": true,
                                    "flex": 3
                                  }
                                ]
                              },
                              {
                                "type": "box",
                                "layout": "baseline",
                                "contents": [
                                  {
                                    "type": "text",
                                    "text": "🚀 サービス",
                                    "size": "sm",
                                    "color": "#666666",
                                    "flex": 2
                                  },
                                  {
                                    "type": "text",
                                    "text": "%s",
                                    "size": "sm",
                                    "wrap": true,
                                    "flex": 3
                                  }
                                ]
                              },
                              {
                                "type": "box",
                                "layout": "baseline",
                                "contents": [
                                  {
                                    "type": "text",
                                    "text": "📊 ステータス",
                                    "size": "sm",
                                    "color": "#666666",
                                    "flex": 2
                                  },
                                  {
                                    "type": "text",
                                    "text": "%s ❌",
                                    "size": "sm",
                                    "color": "#E74C3C",
                                    "weight": "bold",
                                    "flex": 3
                                  }
                                ]
                              },
                              {
                                "type": "box",
                                "layout": "baseline",
                                "contents": [
                                  {
                                    "type": "text",
                                    "text": "🔍 エラー",
                                    "size": "sm",
                                    "color": "#666666",
                                    "flex": 2
                                  },
                                  {
                                    "type": "text",
                                    "text": "%s",
                                    "size": "xs",
                                    "wrap": true,
                                    "color": "#E74C3C",
                                    "flex": 3
                                  }
                                ]
                              }
                            ],
                            "spacing": "sm",
                            "margin": "md"
                          }
                        ]
                      },
                      "footer": {
                        "type": "box",
                        "layout": "vertical",
                        "contents": [
                          {
                            "type": "text",
                            "text": "⚠️ 対処をお願いします",
                            "size": "sm",
                            "color": "#E74C3C",
                            "align": "center",
                            "weight": "bold"
                          }
                        ],
                        "backgroundColor": "#FADBD8",
                        "paddingAll": "10px"
                      }
                    }
                    """,
          timestamp, RENDER_SERVICE_ID, status, shortErrorLogs);
    } catch (Exception e) {
      logger.error("Failed to create deployment failure flex message", e);
      return "{}";
    }
  }

  /** Render APIキーが設定されているかチェック */
  public boolean isRenderApiConfigured() {
    String apiKey = System.getenv("RENDER_API_KEY");
    return apiKey != null && !apiKey.isEmpty();
  }

  /** 監視設定の確認 */
  public String getMonitoringStatus() {
    StringBuilder status = new StringBuilder();
    status.append("Render監視設定:\n");
    status.append("- 監視有効: ").append(monitoringEnabled).append("\n");
    status.append("- サービスID: ").append(RENDER_SERVICE_ID).append("\n");
    status.append("- チェック間隔: ").append(checkIntervalSeconds).append("秒\n");
    status.append("- API認証: ").append(isRenderApiConfigured() ? "設定済み" : "未設定");

    return status.toString();
  }

  /** 手動でデプロイ監視を実行（テスト用） */
  public void triggerMonitoring() {
    logger.info("Manual deployment monitoring triggered");
    startDeploymentMonitoring();
  }
}
