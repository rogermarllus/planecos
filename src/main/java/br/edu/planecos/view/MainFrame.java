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
import java.util.stream.Collectors;

public class MainFrame extends JFrame {

  private final UserService userService;
  private final ExpenseService expenseService;

  // Componentes de UI que precisam ser atualizados
  private JLabel lblUserName;
  private JLabel lblBalance;
  private JTable expenseTable;
  private DefaultTableModel tableModel;

  // Filtros
  private JComboBox<Object> filterCategory;
  private JComboBox<Object> filterStatus;

  public MainFrame() {
    this.userService = new UserService();
    this.expenseService = new ExpenseService();
    initUI();
    refreshData();
  }

  private void initUI() {
    setTitle("Planecos - Controle de Despesas");
    setSize(800, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout());

    // --- PAINEL TOPO (Info do Usuário) ---
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.setBackground(new Color(240, 248, 255));
    topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

    lblUserName = new JLabel("Usuário: Carregando...");
    lblUserName.setFont(new Font("Arial", Font.BOLD, 16));

    lblBalance = new JLabel("Saldo: R$ 0,00");
    lblBalance.setFont(new Font("Arial", Font.BOLD, 16));
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

    // --- PAINEL CENTRO (Tabela e Filtros) ---
    JPanel centerPanel = new JPanel(new BorderLayout());

    // Barra de Filtros
    JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    filterPanel.add(new JLabel("Filtrar por: "));

    filterCategory = new JComboBox<>();

    // 1. Adiciona a opção "coringa" (String)
    filterCategory.addItem("Todas as Categorias");

    // 2. Adiciona os Enums REAIS (não o .name())
    for (ExpenseCategory c : ExpenseCategory.values()) {
      filterCategory.addItem(c);
    }

    // 3. O Renderer Inteligente
    filterCategory.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
          boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof ExpenseCategory) {
          // Se for Categoria, mostra o Label traduzido ("Alimentação")
          setText(((ExpenseCategory) value).getLabel());
        } else if (value instanceof String) {
          // Se for a String "Todas as Categorias", mostra ela mesma
          setText((String) value);
        }

        return this;
      }
    });

    filterStatus = new JComboBox<>();
    filterStatus.addItem("Todos os Status");

    // Adiciona o OBJETO Enum real, não a String .name()
    for (ExpenseStatus s : ExpenseStatus.values()) {
      filterStatus.addItem(s);
    }

    // Configura o Renderer para mostrar o Label ("Pago"/"Pendente")
    filterStatus.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
          boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof ExpenseStatus) {
          // Pega o texto bonito do Enum ("Pendente", "Pago")
          setText(((ExpenseStatus) value).getLabel());
        } else if (value instanceof String) {
          // Pega o texto "Todos os Status"
          setText((String) value);
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

    // Tabela
    String[] columns = { "ID", "Título", "Valor (R$)", "Categoria", "Data", "Situação" };
    tableModel = new DefaultTableModel(columns, 0) {
      @Override // Impede edição direta na célula (regra de negócio)
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    expenseTable = new JTable(tableModel);
    centerPanel.add(new JScrollPane(expenseTable), BorderLayout.CENTER);

    add(centerPanel, BorderLayout.CENTER);

    // --- PAINEL INFERIOR (Ações) ---
    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    JButton btnDelete = new JButton("Excluir Selecionada");
    btnDelete.setBackground(new Color(255, 99, 71));
    btnDelete.setForeground(Color.WHITE);
    btnDelete.addActionListener(e -> deleteSelectedExpense());

    JButton btnToggleStatus = new JButton("Alterar Pago/Pendente");
    btnToggleStatus.addActionListener(e -> toggleExpenseStatus());

    JButton btnAdd = new JButton("Nova Despesa");
    btnAdd.setFont(new Font("Arial", Font.BOLD, 14));
    btnAdd.setBackground(new Color(65, 105, 225));
    btnAdd.setForeground(Color.WHITE);
    btnAdd.addActionListener(e -> openExpenseDialog());

    bottomPanel.add(btnDelete);
    bottomPanel.add(btnToggleStatus);
    bottomPanel.add(Box.createHorizontalStrut(20)); // Espaçamento
    bottomPanel.add(btnAdd);

    add(bottomPanel, BorderLayout.SOUTH);
  }

  // --- AÇÕES E LÓGICA ---

  private void refreshData() {
    refreshHeader();
    refreshTable();
  }

  private void refreshHeader() {
    User user = userService.getCurrentUser();
    if (user != null) {
      lblUserName.setText("Usuário: " + user.getFullName());
      // Formata o saldo com 2 casas decimais
      lblBalance.setText(String.format("Saldo Atual: R$ %.2f", user.getCurrentBalance()));

      // Dica de UX: Mudar cor se saldo for negativo
      if (user.getCurrentBalance().signum() < 0) {
        lblBalance.setForeground(Color.RED);
      } else {
        lblBalance.setForeground(new Color(0, 100, 0)); // Verde escuro
      }
    }
  }

  private void refreshTable() {
    // 1. Limpa as linhas atuais da tabela
    tableModel.setRowCount(0);

    // 2. Busca os dados atualizados do banco via Service
    List<Expense> expenses = expenseService.listAllExpenses();

    // 3. Captura os objetos selecionados nos ComboBoxes
    // Nota: Podem ser String ("Todas...") ou o próprio Enum
    // (ExpenseCategory/ExpenseStatus)
    Object selectedCat = filterCategory.getSelectedItem();
    Object selectedStat = filterStatus.getSelectedItem();

    // 4. Filtra a lista usando Stream e verificando os tipos
    List<Expense> filtered = expenses.stream()
        .filter(e -> {
          // --- Filtro de Categoria ---
          if (selectedCat instanceof String) {
            return true; // Usuário selecionou "Todas as Categorias"
          }
          if (selectedCat instanceof ExpenseCategory) {
            // Compara o Enum da despesa com o Enum do filtro
            return e.getCategory() == selectedCat;
          }
          return true;
        })
        .filter(e -> {
          // --- Filtro de Status ---
          if (selectedStat instanceof String) {
            return true; // Usuário selecionou "Todos os Status"
          }
          if (selectedStat instanceof ExpenseStatus) {
            // Compara o Enum da despesa com o Enum do filtro
            return e.getStatus() == selectedStat;
          }
          return true;
        })
        .collect(Collectors.toList());

    // 5. Preenche a tabela com os dados formatados
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    for (Expense e : filtered) {
      Object[] row = {
          e.getId(),
          e.getTitle(),
          e.getAmount(), // Valor (BigDecimal)
          e.getCategory().getLabel(), // Mostra "Alimentação" em vez de FOOD
          e.getExpenseDate().format(dtf), // Data formatada BR
          e.getStatus().getLabel() // Mostra "Pago" ou "Pendente"
      };
      tableModel.addRow(row);
    }

    // 6. Aproveita para atualizar o saldo no topo da tela (caso tenha mudado algo)
    refreshHeader();
  }

  private void openExpenseDialog() {
    // Passa uma função lambda para atualizar a tela após salvar
    new ExpenseDialog(this, this::refreshData).setVisible(true);
  }

  private void deleteSelectedExpense() {
    int row = expenseTable.getSelectedRow();
    if (row == -1) {
      JOptionPane.showMessageDialog(this, "Selecione uma despesa para excluir.");
      return;
    }

    Long id = (Long) tableModel.getValueAt(row, 0); // Pega o ID da coluna 0
    int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza?", "Excluir", JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
      expenseService.deleteExpense(id);
      refreshData();
    }
  }

  private void editProfile() {
    User user = userService.getCurrentUser();

    // Painel simples para edição rápida
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