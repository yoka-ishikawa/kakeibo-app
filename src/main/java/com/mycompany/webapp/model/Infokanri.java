package com.mycompany.webapp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** 家計簿情報管理エンティティ Supabaseのtb_info_kanriテーブルにマッピング */
@Entity
@Table(name = "tb_info_kanri")
public class Infokanri {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id")
  private String userId;

  @Column(name = "type", nullable = false, length = 50)
  private String type;

  @Column(name = "amount", nullable = false)
  private Integer amount;

  @Column(name = "category")
  private String category;

  @Column(name = "registed_at", nullable = false)
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate registedAt;

  @Column(name = "created_at")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;

  // ライフサイクルメソッド
  @PrePersist
  protected void onCreate() {
    LocalDateTime now = LocalDateTime.now();
    this.createdAt = now;
    this.updatedAt = now;
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }

  // デフォルトコンストラクタ
  public Infokanri() {}

  // 引数付きコンストラクタ
  public Infokanri(String userId, String type, Integer amount, String category,
      LocalDate registedAt) {
    this.userId = userId;
    this.type = type;
    this.amount = amount;
    this.category = category;
    this.registedAt = registedAt;
  }

  // Getter/Setter
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Integer getAmount() {
    return amount;
  }

  public void setAmount(Integer amount) {
    this.amount = amount;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public LocalDate getRegistedAt() {
    return registedAt;
  }

  public void setRegistedAt(LocalDate registedAt) {
    this.registedAt = registedAt;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Override
  public String toString() {
    return "Infokanri{" + "id=" + id + ", userId='" + userId + '\'' + ", type='" + type + '\''
        + ", amount=" + amount + ", category='" + category + '\'' + ", registedAt=" + registedAt
        + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + '}';
  }
}
