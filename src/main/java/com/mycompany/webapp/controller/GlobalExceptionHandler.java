package com.mycompany.webapp.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/** グローバル例外ハンドラー アプリケーション全体で発生する例外を統一的に処理し、 適切なJSONレスポンスを返す */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
    System.err.println("=== グローバル例外キャッチ ===");
    System.err.println("例外タイプ: " + ex.getClass().getSimpleName());
    System.err.println("例外メッセージ: " + ex.getMessage());
    ex.printStackTrace();

    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("error", "内部サーバーエラーが発生しました");
    errorResponse.put("message", ex.getMessage());
    errorResponse.put("timestamp", LocalDateTime.now().toString());
    errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .header("Content-Type", "application/json")
        .body(errorResponse);
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
    System.err.println("=== ランタイム例外キャッチ ===");
    System.err.println("例外タイプ: " + ex.getClass().getSimpleName());
    System.err.println("例外メッセージ: " + ex.getMessage());
    ex.printStackTrace();

    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("error", "処理中にエラーが発生しました");
    errorResponse.put("message", ex.getMessage());
    errorResponse.put("timestamp", LocalDateTime.now().toString());
    errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .header("Content-Type", "application/json")
        .body(errorResponse);
  }
}
