package br.edu.planecos.dao;

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
import java.util.List;

class ExpenseDAOTest {

  private ExpenseDAO expenseDAO;
  private UserDAO userDAO;
  private User userPadrao;

  @BeforeEach
  void setUp() {
    cleanDatabase();

    this.expenseDAO = new ExpenseDAO();
    this.userDAO = new UserDAO();

    this.userPadrao = new User();
    userPadrao.setFullName("Dono das Despesas");
    userPadrao.setCurrentBalance(new BigDecimal("1000.00"));
    userDAO.save(userPadrao);
  }

  @Test
  @DisplayName("Deve salvar despesa corretamente")
  void shouldSaveExpense() {
    Expense expense = criarDespesaExemplo();

    expenseDAO.save(expense);

    Assertions.assertNotNull(expense.getId(), "ID deve ser gerado pelo banco");
  }

  @Test
  @DisplayName("Deve listar despesas de um usuário específico")
  void shouldFindAllExpensesForUser() {
    Expense exp1 = criarDespesaExemplo();
    expenseDAO.save(exp1);

    Expense exp2 = criarDespesaExemplo();
    exp2.setTitle("Outra despesa");
    expenseDAO.save(exp2);

    List<Expense> lista = expenseDAO.findAll(userPadrao.getId());

    Assertions.assertEquals(2, lista.size());
    Assertions.assertTrue(lista.stream().anyMatch(e -> e.getTitle().equals("Conta de Luz")));
  }

  @Test
  @DisplayName("Deve deletar uma despesa")
  void shouldDeleteExpense() {
    Expense exp = criarDespesaExemplo();
    expenseDAO.save(exp);
    Long id = exp.getId();

    expenseDAO.delete(id);

    List<Expense> lista = expenseDAO.findAll(userPadrao.getId());
    Assertions.assertTrue(lista.isEmpty(), "A lista deveria estar vazia após deletar");
  }

  private Expense criarDespesaExemplo() {
    Expense exp = new Expense();
    exp.setUserId(userPadrao.getId());
    exp.setTitle("Conta de Luz");
    exp.setAmount(new BigDecimal("150.00"));
    exp.setCategory(ExpenseCategory.HOUSING);
    exp.setStatus(ExpenseStatus.PENDING);
    exp.setExpenseDate(LocalDate.now());
    return exp;
  }

  private void cleanDatabase() {
    try (Connection conn = ConnectionFactory.getConnection();
        Statement stmt = conn.createStatement()) {
      stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
      stmt.execute("TRUNCATE TABLE expenses");
      stmt.execute("TRUNCATE TABLE users");
      stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
    } catch (SQLException e) {
      throw new RuntimeException("Erro ao limpar banco", e);
    }
  }
}