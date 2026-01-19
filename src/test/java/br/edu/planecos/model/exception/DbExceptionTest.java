package br.edu.planecos.model.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.edu.planecos.exception.DbException;

class DbExceptionTest {

  @Test
  @DisplayName("Deve criar exceção com mensagem")
  void shouldCreateExceptionWithMessage() {
    DbException ex = new DbException("Erro de teste");
    Assertions.assertEquals("Erro de teste", ex.getMessage());
  }

  @Test
  @DisplayName("Deve criar exceção com mensagem e causa")
  void shouldCreateExceptionWithMessageAndCause() {
    RuntimeException causa = new RuntimeException("Causa original");
    DbException ex = new DbException("Erro encapsulado", causa);

    Assertions.assertEquals("Erro encapsulado", ex.getMessage());
    Assertions.assertEquals(causa, ex.getCause());
  }
}