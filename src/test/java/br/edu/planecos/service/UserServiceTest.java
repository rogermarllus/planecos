package br.edu.planecos.service;

import br.edu.planecos.dao.ConnectionFactory;
import br.edu.planecos.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

class UserServiceTest {

  private UserService userService;

  @BeforeEach
  void setUp() {
    cleanDatabase(); // Garante banco limpo para cada teste
    this.userService = new UserService();
  }

  @Test
  @DisplayName("Deve registrar um usuário válido")
  void shouldRegisterValidUser() {
    User user = userService.registerNewUser("Maria Silva", new BigDecimal("100.00"));

    Assertions.assertNotNull(user.getId());
    Assertions.assertEquals("Maria Silva", user.getFullName());
    // Verifica se realmente salvou e se conseguimos buscar de volta
    Assertions.assertTrue(userService.hasRegisteredUser());
  }

  @Test
  @DisplayName("Não deve permitir usuário com nome vazio")
  void shouldBlockEmptyName() {
    // Preparação FORA do assertThrows
    BigDecimal zeroBalance = BigDecimal.ZERO;

    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      userService.registerNewUser("", zeroBalance);
    });

    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      userService.registerNewUser(null, zeroBalance);
    });
  }

  @Test
  @DisplayName("Não deve permitir saldo inicial negativo")
  void shouldBlockNegativeBalance() {
    // Preparação FORA do assertThrows
    // Se este new BigDecimal falhar, o teste quebra (ERRO) em vez de passar
    // falsamente.
    BigDecimal negativeBalance = new BigDecimal("-10.00");
    String nomeValido = "João";

    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      userService.registerNewUser(nomeValido, negativeBalance);
    });
  }

  // --- Helper de Limpeza (Mesmo padrão dos DAOs) ---
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