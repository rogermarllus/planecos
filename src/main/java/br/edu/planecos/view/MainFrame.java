package br.edu.planecos.view;

import br.edu.planecos.model.Expense;
import br.edu.planecos.model.User;
import br.edu.planecos.model.enums.ExpenseCategory;
import br.edu.planecos.model.enums.ExpenseStatus;
import br.edu.planecos.service.ExpenseService;
import br.edu.planecos.service.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainFrame extends JFrame {

  private final transient UserService userService;
  private final transient ExpenseService expenseService;

  private JLabel lblUserName;
  private JLabel lblBalance;
  private JTable expenseTable;
  private DefaultTableModel tableModel;

  private JComboBox<Object> filterCategory;
  private JComboBox<Object> filterStatus;

  private static final String FONT_FAMILY = "Arial";

  public MainFrame() {
    this.userService = new UserService();
    this.expenseService = new ExpenseService();
    initUI();
    refreshData();
  }

  private void initUI() {
    setTitle("Planecos - Controle de Despesas");
    setSize(800, 600);
    setDefaultCloseOperation(3);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout());

    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.setBackground(new Color(240, 248, 255));
    topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

    lblUserName = new JLabel("Usuário: Carregando...");
    lblUserName.setFont(new Font(FONT_FAMILY, Font.BOLD, 16));

    lblBalance = new JLabel("Saldo: R$ 0,00");
    lblBalance.setFont(new Font(FONT_FAMILY, Font.BOLD, 16));
    lblBalance.setForeground(new Color(0, 100, 0));

    JButton btnEditProfile = new JButton("Editar Perfil");
    btnEditProfile.addActionListener(e -> editProfile());

    JPanel userInfoPanel = new JPanel(new GridLayout(2, 1));
    userInfoPanel.setOpaque(false);
    userInfoPanel.add(lblUserName);
    userInfoPanel.add(lblBalance);

    topPanel.add(userInfoPanel, BorderLayout.CENTER);
    topPanel.add(btnEditProfile, BorderLayout.EAST);

    add(topPanel, BorderLayout.NORTH);

    JPanel centerPanel = new JPanel(new BorderLayout());

    JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    filterPanel.add(new JLabel("Filtrar por: "));

    filterCategory = new JComboBox<>();

    filterCategory.addItem("Todas as Categorias");

    for (ExpenseCategory c : ExpenseCategory.values()) {
      filterCategory.addItem(c);
    }

    filterCategory.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
          boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof ExpenseCategory category) {
          setText(category.getLabel());
        } else if (value instanceof String text) {
          setText(text);
        }

        return this;
      }
    });

    filterStatus = new JComboBox<>();
    filterStatus.addItem("Todos os Status");

    for (ExpenseStatus s : ExpenseStatus.values()) {
      filterStatus.addItem(s);
    }

    filterStatus.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
          boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof ExpenseStatus status) {
          setText(status.getLabel());
        } else if (value instanceof String text) {
          setText(text);
        }
        return this;
      }
    });

    JButton btnApplyFilter = new JButton("Filtrar");
    btnApplyFilter.addActionListener(e -> refreshTable());

    filterPanel.add(filterCategory);
    filterPanel.add(filterStatus);
    filterPanel.add(btnApplyFilter);

    centerPanel.add(filterPanel, BorderLayout.NORTH);

    String[] columns = { "ID", "Título", "Valor (R$)", "Categoria", "Data", "Situação" };
    tableModel = new DefaultTableModel(columns, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    expenseTable = new JTable(tableModel);
    centerPanel.add(new JScrollPane(expenseTable), BorderLayout.CENTER);

    add(centerPanel, BorderLayout.CENTER);

    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    JButton btnDelete = new JButton("Excluir Selecionada");
    btnDelete.setBackground(new Color(255, 99, 71));
    btnDelete.setForeground(Color.WHITE);
    btnDelete.addActionListener(e -> deleteSelectedExpense());

    JButton btnToggleStatus = new JButton("Alterar Pago/Pendente");
    btnToggleStatus.addActionListener(e -> toggleExpenseStatus());

    JButton btnAdd = new JButton("Nova Despesa");
    btnAdd.setFont(new Font(FONT_FAMILY, Font.BOLD, 14));
    btnAdd.setBackground(new Color(65, 105, 225));
    btnAdd.setForeground(Color.WHITE);
    btnAdd.addActionListener(e -> openExpenseDialog());

    bottomPanel.add(btnDelete);
    bottomPanel.add(btnToggleStatus);
    bottomPanel.add(Box.createHorizontalStrut(20));
    bottomPanel.add(btnAdd);

    add(bottomPanel, BorderLayout.SOUTH);
  }

  private void refreshData() {
    refreshHeader();
    refreshTable();
  }

  private void refreshHeader() {
    User user = userService.getCurrentUser();
    if (user != null) {
      lblUserName.setText("Usuário: " + user.getFullName());
      lblBalance.setText(String.format("Saldo Atual: R$ %.2f", user.getCurrentBalance()));

      if (user.getCurrentBalance().signum() < 0) {
        lblBalance.setForeground(Color.RED);
      } else {
        lblBalance.setForeground(new Color(0, 100, 0));
      }
    }
  }

  private void refreshTable() {
    tableModel.setRowCount(0);

    List<Expense> expenses = expenseService.listAllExpenses();
    Object selectedCat = filterCategory.getSelectedItem();
    Object selectedStat = filterStatus.getSelectedItem();

    List<Expense> filtered = expenses.stream()
        .filter(e -> {
          if (selectedCat instanceof ExpenseCategory category) {
            return e.getCategory() == category;
          }
          return true;
        })
        .filter(e -> {
          if (selectedStat instanceof ExpenseStatus status) {
            return e.getStatus() == status;
          }
          return true;
        })
        .toList();

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    for (Expense e : filtered) {
      Object[] row = {
          e.getId(),
          e.getTitle(),
          e.getAmount(),
          e.getCategory().getLabel(),
          e.getExpenseDate().format(dtf),
          e.getStatus().getLabel()
      };
      tableModel.addRow(row);
    }

    refreshHeader();
  }

  private void openExpenseDialog() {
    new ExpenseDialog(this, this::refreshData).setVisible(true);
  }

  private void deleteSelectedExpense() {
    int row = expenseTable.getSelectedRow();
    if (row == -1) {
      JOptionPane.showMessageDialog(this, "Selecione uma despesa para excluir.");
      return;
    }

    Long id = (Long) tableModel.getValueAt(row, 0);
    int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza?", "Excluir", JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
      expenseService.deleteExpense(id);
      refreshData();
    }
  }

  private void editProfile() {
    User user = userService.getCurrentUser();

    JTextField txtName = new JTextField(user.getFullName());
    JTextField txtBal = new JTextField(user.getCurrentBalance().toString());

    Object[] message = {
        "Nome:", txtName,
        "Saldo Atual:", txtBal
    };

    int option = JOptionPane.showConfirmDialog(this, message, "Editar Perfil", JOptionPane.OK_CANCEL_OPTION);
    if (option == JOptionPane.OK_OPTION) {
      try {
        user.setFullName(txtName.getText());
        user.setCurrentBalance(new BigDecimal(txtBal.getText().replace(",", ".")));
        userService.updateUserProfile(user);
        lblUserName.setText("Usuário: " + user.getFullName());
        lblBalance.setText("Saldo: R$ " + user.getCurrentBalance());
        JOptionPane.showMessageDialog(this, "Dados atualizados!");
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
      }
    }
  }

  private void toggleExpenseStatus() {
    int row = expenseTable.getSelectedRow();

    if (row == -1) {
      JOptionPane.showMessageDialog(this, "Selecione uma despesa para alterar o status.", "Aviso",
          JOptionPane.WARNING_MESSAGE);
      return;
    }

    Long id = (Long) tableModel.getValueAt(row, 0);

    try {
      expenseService.toggleExpenseStatus(id);
      refreshData();
      JOptionPane.showMessageDialog(this, "Status alterado com sucesso!");
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Erro ao alterar status: " + ex.getMessage(), "Erro",
          JOptionPane.ERROR_MESSAGE);
    }
  }
}