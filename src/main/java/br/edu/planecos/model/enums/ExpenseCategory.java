package br.edu.planecos.model.enums;

public enum ExpenseCategory {
  FOOD("Alimentação"),
  TRANSPORT("Transporte"),
  HOUSING("Moradia"),
  HEALTH("Saúde"),
  EDUCATION("Educação"),
  LEISURE("Lazer"),
  OTHER("Outros");

  private final String label;

  ExpenseCategory(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public static ExpenseCategory fromString(String text) {
    if (text == null || text.trim().isEmpty()) {
      return OTHER;
    }
    try {
      return ExpenseCategory.valueOf(text.toUpperCase());
    } catch (IllegalArgumentException e) {
      return OTHER;
    }
  }
}