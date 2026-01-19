package br.edu.planecos.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class UserTest {

  @Test
  @DisplayName("Deve criar objeto User corretamente com todos os atributos")
  void shouldCreateUserCorrectly() {
    // 1. Cenário (Arrange)
    Long id = 1L;
    String name = "Teste Silva";
    BigDecimal balance = new BigDecimal("100.50");

    // 2. Ação (Act)
    User user = new User(id, name, balance);

    // 3. Verificação (Assert)
    // Verificamos se o que colocamos no objeto é o que sai dele
    Assertions.assertEquals(id, user.getId());
    Assertions.assertEquals(name, user.getFullName());
    Assertions.assertEquals(balance, user.getCurrentBalance());
  }

  @Test
  @DisplayName("Deve atualizar o saldo corretamente")
  void shouldUpdateBalance() {
    User user = new User();
    user.setCurrentBalance(BigDecimal.ZERO);

    BigDecimal newBalance = new BigDecimal("500.00");
    user.setCurrentBalance(newBalance);

    Assertions.assertEquals(newBalance, user.getCurrentBalance());
  }
}