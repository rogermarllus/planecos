package br.edu.planecos.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class User {
  private Long id;
  private String fullName;
  private BigDecimal currentBalance;
  private LocalDateTime createdAt;

  public User() {
  }

  public User(Long id, String fullName, BigDecimal currentBalance) {
    this.id = id;
    this.fullName = fullName;
    this.currentBalance = currentBalance;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public BigDecimal getCurrentBalance() {
    return currentBalance;
  }

  public void setCurrentBalance(BigDecimal currentBalance) {
    this.currentBalance = currentBalance;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}