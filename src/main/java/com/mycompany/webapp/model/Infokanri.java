package com.mycompany.webapp.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 家計簿情報管理エンティティ Supabaseのtb_info_kanriテーブルにマッピング
 */
@Entity
@Table(name = "tb_info_kanri")
public class Infokanri {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id")
  private String userId;

  @Column(name = "syubetu", nullable = false, length = 50)
  private String syubetu;

  @Column(name = "kingaku", nullable = false)
  private Integer kingaku;

  @Column(name = "naisyo")
  private String naisyo;
  @Column(name = "hiduke", nullable = false)
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate hiduke;

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
  public Infokanri(String userId, String syubetu, Integer kingaku, String naisyo,
      LocalDate hiduke) {
    this.userId = userId;
    this.syubetu = syubetu;
    this.kingaku = kingaku;
    this.naisyo = naisyo;
    this.hiduke = hiduke;
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

  public String getSyubetu() {
    return syubetu;
  }

  public void setSyubetu(String syubetu) {
    this.syubetu = syubetu;
  }

  public Integer getKingaku() {
    return kingaku;
  }

  public void setKingaku(Integer kingaku) {
    this.kingaku = kingaku;
  }

  public String getNaisyo() {
    return naisyo;
  }

  public void setNaisyo(String naisyo) {
    this.naisyo = naisyo;
  }

  public LocalDate getHiduke() {
    return hiduke;
  }

  public void setHiduke(LocalDate hiduke) {
    this.hiduke = hiduke;
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
    return "Infokanri{" + "id=" + id + ", userId='" + userId + '\'' + ", syubetu='" + syubetu + '\''
        + ", kingaku=" + kingaku + ", naisyo='" + naisyo + '\'' + ", hiduke=" + hiduke
        + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + '}';
  }
}
