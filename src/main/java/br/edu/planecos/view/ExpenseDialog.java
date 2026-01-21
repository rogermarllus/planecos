package br.edu.planecos.view;

import br.edu.planecos.model.Expense;
import br.edu.planecos.model.User;
import br.edu.planecos.model.enums.ExpenseCategory;
import br.edu.planecos.model.enums.ExpenseStatus;
import br.edu.planecos.service.ExpenseService;
import br.edu.planecos.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ExpenseDialog extends JDialog {

  private final transient ExpenseService expenseService;
  private final transient UserService userService;
  private final transient Runnable onSuccessCallback;

  public ExpenseDialog(Frame parent, Runnable onSuccessCallback) {
    super(parent, "Nova Despesa", true);
    this.expenseService = new ExpenseService();
    this.userService = new UserService();
    this.onSuccessCallback = onSuccessCallback;
    initUI();
  }

  private void initUI() {
    setSize(400, 500);
    setLocationRelativeTo(getParent());
    setLayout(new GridLayout(6, 1, 10, 10));

    JTextField txtTitle = new JTextField();
    txtTitle.setBorder(BorderFactory.createTitledBorder("Título (Ex: Mercado)"));

    JTextField txtAmount = new JTextField();
    txtAmount.setBorder(BorderFactory.createTitledBorder("Valor (R$)"));

    JComboBox<ExpenseCategory> cbCategory = new JComboBox<>(ExpenseCategory.values());

    cbCategory.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
          boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof ExpenseCategory category) {
          setText(category.getLabel());
        }

        return this;
      }
    });

    cbCategory.setBorder(BorderFactory.createTitledBorder("Categoria"));

    JRadioButton rbtnPending = new JRadioButton("Pendente", true);
    JRadioButton rbtnPaid = new JRadioButton("Pago");
    ButtonGroup statusGroup = new ButtonGroup();
    statusGroup.add(rbtnPending);
    statusGroup.add(rbtnPaid);

    JPanel panelStatus = new JPanel();
    panelStatus.setBorder(BorderFactory.createTitledBorder("Situação"));
    panelStatus.add(rbtnPending);
    panelStatus.add(rbtnPaid);

    JFormattedTextField txtDate = new JFormattedTextField();
    txtDate.setValue(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    txtDate.setBorder(BorderFactory.createTitledBorder("Data (dd/MM/yyyy)"));

    JButton btnSave = new JButton("Salvar Despesa");

    add(txtTitle);
    add(txtAmount);
    add(cbCategory);
    add(panelStatus);
    add(txtDate);
    add(btnSave);

    btnSave.addActionListener(e -> {
      try {
        User user = userService.getCurrentUser();
        String title = txtTitle.getText();
        BigDecimal amount = new BigDecimal(txtAmount.getText().replace(",", "."));
        ExpenseCategory category = (ExpenseCategory) cbCategory.getSelectedItem();
        ExpenseStatus status = rbtnPaid.isSelected() ? ExpenseStatus.PAID : ExpenseStatus.PENDING;

        LocalDate date = LocalDate.parse(txtDate.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        Expense expense = new Expense(user.getId(), title, amount, status, category, date);

        expenseService.registerExpense(expense);

        JOptionPane.showMessageDialog(this, "Despesa salva!");
        onSuccessCallback.run();
        dispose();

      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Erro ao salvar: " + ex.getMessage());
      }
    });
  }
}