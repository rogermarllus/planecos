package br.edu.planecos.service;

import br.edu.planecos.dao.ConnectionFactory;
import br.edu.planecos.model.Expense;
import br.edu.planecos.model.User;
import br.edu.planecos.model.enums.ExpenseCategory;
import br.edu.planecos.model.enums.ExpenseStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

class ExpenseServiceTest {

  private ExpenseService expenseService;
  private UserService userService;
  private User userPadrao;

  @BeforeEach
  void setUp() {
    cleanDatabase();
    this.expenseService = new ExpenseService();
    this.userService = new UserService();

    this.userPadrao = userService.registerNewUser("Tester", new BigDecimal("1000.00"));
  }

  @Test
  @DisplayName("Deve descontar do saldo se a despesa for PAGA")
  void shouldDeductBalanceWhenPaid() {
    Expense exp = createExpense(new BigDecimal("200.00"), ExpenseStatus.PAID);

    expenseService.registerExpense(exp);

    User userAtualizado = userService.getCurrentUser();
    BigDecimal esperado = new BigDecimal("800.00");

    Assertions.assertEquals(0, esperado.compareTo(userAtualizado.getCurrentBalance()),
        "O saldo deveria ter sido descontado");
  }

  @Test
  @DisplayName("NÃO deve descontar do saldo se a despesa for PENDENTE")
  void shouldNotDeductBalanceWhenPending() {
    Expense exp = createExpense(new BigDecimal("200.00"), ExpenseStatus.PENDING);

    expenseService.registerExpense(exp);

    User userAtualizado = userService.getCurrentUser();
    BigDecimal esperado = new BigDecimal("1000.00");

    Assertions.assertEquals(0, esperado.compareTo(userAtualizado.getCurrentBalance()),
        "O saldo não deveria mudar para despesas pendentes");
  }

  @Test
  @DisplayName("Não deve aceitar despesa com valor negativo ou zero")
  void shouldBlockInvalidAmount() {
    Expense exp = createExpense(new BigDecimal("-50.00"), ExpenseStatus.PENDING);

    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      expenseService.registerExpense(exp);
    });
  }

  private Expense createExpense(BigDecimal amount, ExpenseStatus status) {
    return new Expense(
        userPadrao.getId(),
        "Teste",
        amount,
        status,
        ExpenseCategory.FOOD,
        LocalDate.now());
  }

  private void cleanDatabase() {
    try (Connection conn = ConnectionFactory.getConnection();
        Statement stmt = conn.createStatement()) {
      stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
      stmt.execute("TRUNCATE TABLE expenses");
      stmt.execute("TRUNCATE TABLE users");
      stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}