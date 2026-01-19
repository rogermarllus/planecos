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

  // O @BeforeEach roda ANTES de cada teste. Útil para preparar o terreno.
  @BeforeEach
  void setUp() {
    this.userDAO = new UserDAO();
    // Opcional: Aqui você poderia limpar a tabela users antes de cada teste
    cleanDatabase();
  }

  @Test
  @DisplayName("Deve salvar um usuário no banco e gerar um ID")
  void shouldSaveUser() {
    // 1. Cenário
    User user = new User();
    user.setFullName("Usuário Teste JUnit");
    user.setCurrentBalance(new BigDecimal("0.00"));

    // 2. Ação
    userDAO.save(user);

    // 3. Verificação
    // Se salvou, o ID não pode ser nulo (pois o banco gera o auto_increment)
    Assertions.assertNotNull(user.getId(), "O ID não deveria ser nulo após salvar");
    System.out.println("Usuário salvo com ID: " + user.getId());
  }

  @Test
  @DisplayName("Deve encontrar o primeiro usuário cadastrado")
  void shouldFindUser() {
    // Garante que existe algo para buscar
    User novo = new User();
    novo.setFullName("Busca Teste");
    novo.setCurrentBalance(BigDecimal.TEN);
    userDAO.save(novo);

    // Ação
    User foundUser = userDAO.findFirstUser();

    // Verificação
    Assertions.assertNotNull(foundUser);
    Assertions.assertNotNull(foundUser.getId());
  }

  private void cleanDatabase() {
    try (Connection conn = ConnectionFactory.getConnection();
        Statement stmt = conn.createStatement()) {

      // 1. Desabilita a proteção de chaves estrangeiras
      stmt.execute("SET FOREIGN_KEY_CHECKS = 0");

      // 2. Limpa as tabelas (aproveite para limpar expenses também, para evitar lixo)
      stmt.execute("TRUNCATE TABLE expenses");
      stmt.execute("TRUNCATE TABLE users");

      // 3. Reabilita a proteção
      stmt.execute("SET FOREIGN_KEY_CHECKS = 1");

    } catch (SQLException e) {
      throw new RuntimeException("Erro ao limpar banco de dados de teste", e);
    }
  }
}