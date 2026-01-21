package br.edu.planecos.dao;

import br.edu.planecos.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

class UserDAOTest {

  private UserDAO userDAO;

  @BeforeEach
  void setUp() {
    this.userDAO = new UserDAO();
    cleanDatabase();
  }

  @Test
  @DisplayName("Deve salvar um usuário no banco e gerar um ID")
  void shouldSaveUser() {
    User user = new User();
    user.setFullName("Usuário Teste JUnit");
    user.setCurrentBalance(new BigDecimal("0.00"));

    userDAO.save(user);

    Assertions.assertNotNull(user.getId(), "O ID não deveria ser nulo após salvar");
    System.out.println("Usuário salvo com ID: " + user.getId());
  }

  @Test
  @DisplayName("Deve encontrar o primeiro usuário cadastrado")
  void shouldFindUser() {
    User novo = new User();
    novo.setFullName("Busca Teste");
    novo.setCurrentBalance(BigDecimal.TEN);
    userDAO.save(novo);

    User foundUser = userDAO.findFirstUser();

    Assertions.assertNotNull(foundUser);
    Assertions.assertNotNull(foundUser.getId());
  }

  private void cleanDatabase() {
    try (Connection conn = ConnectionFactory.getConnection();
        Statement stmt = conn.createStatement()) {

      stmt.execute("SET FOREIGN_KEY_CHECKS = 0");

      stmt.execute("TRUNCATE TABLE expenses");
      stmt.execute("TRUNCATE TABLE users");

      stmt.execute("SET FOREIGN_KEY_CHECKS = 1");

    } catch (SQLException e) {
      throw new RuntimeException("Erro ao limpar banco de dados de teste", e);
    }
  }
}