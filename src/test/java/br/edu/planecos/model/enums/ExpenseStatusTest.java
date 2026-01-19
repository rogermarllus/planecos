package br.edu.planecos.model.enums;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExpenseStatusTest {

  @Test
  @DisplayName("Deve converter string válida para Enum")
  void shouldConvertValidString() {
    Assertions.assertEquals(ExpenseStatus.PAID, ExpenseStatus.fromString("PAID"));
    Assertions.assertEquals(ExpenseStatus.PENDING, ExpenseStatus.fromString("pending"));
  }

  @Test
  @DisplayName("Deve lançar exceção para status inválido")
  void shouldThrowExceptionForInvalidString() {
    // Diferente da Categoria, aqui queremos que quebre se vier algo errado do banco
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      ExpenseStatus.fromString("INVALIDO");
    });
  }

  @Test
  @DisplayName("Deve retornar o label correto")
  void shouldReturnCorrectLabel() {
    Assertions.assertEquals("Pago", ExpenseStatus.PAID.getLabel());
  }
}