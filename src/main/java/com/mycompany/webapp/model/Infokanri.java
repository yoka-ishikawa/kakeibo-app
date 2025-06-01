package com.mycompany.webapp.model;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.sql.Timestamp;

@Entity
@Table(name = "tb_info_kanri")
public class Infokanri {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "user_token")
  private String userToken;
  @Column(name = "registered_at")
  private LocalDate registeredAt;
  @Column(name = "type")
  private String type;
  @Column(name = "category")
  private String category;
  @Column(name = "amount")
  private Integer amount;
  @Column(name = "update_date_time")
  private Timestamp updateDateTime;

  // getter/setter
  public void setUserToken(String userToken) {
    this.userToken = userToken;
  }
  public void setregisteredAt(LocalDate registeredAt) {
    this.registeredAt = registeredAt;
  }
  public void setType(String type) {
    this.type = type;
  }
  public void setCategory(String category) {
    this.category = category;
  }
  public void setAmount(Integer amount) {
    this.amount = amount;
  }
  public void setUpdateDateTime(Timestamp updateDateTime) {
    this.updateDateTime = updateDateTime;
  }
}