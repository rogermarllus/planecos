package br.edu.planecos.model.enums;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExpenseCategoryTest {

  @Test
  @DisplayName("Deve converter string válida para Enum")
  void shouldConvertValidString() {
    Assertions.assertEquals(ExpenseCategory.FOOD, ExpenseCategory.fromString("FOOD"));
    Assertions.assertEquals(ExpenseCategory.TRANSPORT, ExpenseCategory.fromString("transport")); // Teste
                                                                                                 // case-insensitive
  }

  @Test
  @DisplayName("Deve retornar OTHER para string desconhecida ou nula")
  void shouldReturnOtherForInvalidString() {
    Assertions.assertEquals(ExpenseCategory.OTHER, ExpenseCategory.fromString("COISA_QUE_NAO_EXISTE"));
    Assertions.assertEquals(ExpenseCategory.OTHER, ExpenseCategory.fromString(null));
    Assertions.assertEquals(ExpenseCategory.OTHER, ExpenseCategory.fromString(""));
  }

  @Test
  @DisplayName("Deve retornar o label correto para exibição")
  void shouldReturnCorrectLabel() {
    Assertions.assertEquals("Alimentação", ExpenseCategory.FOOD.getLabel());
  }
}