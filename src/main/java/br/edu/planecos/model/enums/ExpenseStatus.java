package br.edu.planecos.model.enums;

public enum ExpenseStatus {
    PENDING("Pendente"),
    PAID("Pago");

    private final String label;

    ExpenseStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static ExpenseStatus fromString(String text) {
        for (ExpenseStatus b : ExpenseStatus.values()) {
            if (b.name().equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Status não encontrado: " + text);
    }
}