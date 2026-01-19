package br.edu.planecos.model;

import br.edu.planecos.model.enums.ExpenseCategory;
import br.edu.planecos.model.enums.ExpenseStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

class ExpenseTest {

  @Test
  @DisplayName("Deve criar despesa via construtor completo e getters")
  void shouldCreateExpenseCorrectly() {
    Long userId = 1L;
    String title = "Pizza";
    BigDecimal amount = new BigDecimal("50.00");
    ExpenseStatus status = ExpenseStatus.PENDING;
    ExpenseCategory category = ExpenseCategory.FOOD;
    LocalDate date = LocalDate.now();

    Expense expense = new Expense(userId, title, amount, status, category, date);

    Assertions.assertEquals(userId, expense.getUserId());
    Assertions.assertEquals(title, expense.getTitle());
    Assertions.assertEquals(amount, expense.getAmount());
    Assertions.assertEquals(status, expense.getStatus());
    Assertions.assertEquals(category, expense.getCategory());
    Assertions.assertEquals(date, expense.getExpenseDate());
  }

  @Test
  @DisplayName("Deve testar setters e construtor vazio")
  void shouldSetValuesCorrectly() {
    Expense expense = new Expense();
    expense.setId(10L);
    expense.setTitle("Uber");

    Assertions.assertEquals(10L, expense.getId());
    Assertions.assertEquals("Uber", expense.getTitle());
  }
}