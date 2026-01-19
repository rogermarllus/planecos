package br.edu.planecos.model;

import br.edu.planecos.model.enums.ExpenseCategory;
import br.edu.planecos.model.enums.ExpenseStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Expense {
  private Long id;
  private Long userId;
  private String title;
  private BigDecimal amount;
  private ExpenseStatus status;
  private ExpenseCategory category;
  private LocalDate expenseDate;

  public Expense() {
  }

  public Expense(Long userId, String title, BigDecimal amount, ExpenseStatus status, ExpenseCategory category,
      LocalDate expenseDate) {
    this.userId = userId;
    this.title = title;
    this.amount = amount;
    this.status = status;
    this.category = category;
    this.expenseDate = expenseDate;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public ExpenseStatus getStatus() {
    return status;
  }

  public void setStatus(ExpenseStatus status) {
    this.status = status;
  }

  public ExpenseCategory getCategory() {
    return category;
  }

  public void setCategory(ExpenseCategory category) {
    this.category = category;
  }

  public LocalDate getExpenseDate() {
    return expenseDate;
  }

  public void setExpenseDate(LocalDate expenseDate) {
    this.expenseDate = expenseDate;
  }
}