package br.edu.planecos.view;

import br.edu.planecos.service.UserService;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class RegisterFrame extends JFrame {

  private final transient UserService userService;

  public RegisterFrame() {
    this.userService = new UserService();
    initUI();
  }

  private void initUI() {
    setTitle("Bem-vindo ao Planecos");
    setSize(300, 350);
    setDefaultCloseOperation(3);
    setLocationRelativeTo(null);
    setLayout(new GridLayout(4, 1, 10, 10));

    JTextField txtName = new JTextField();
    txtName.setBorder(BorderFactory.createTitledBorder("Seu Nome Completo"));

    JTextField txtBalance = new JTextField();
    txtBalance.setBorder(BorderFactory.createTitledBorder("Saldo Inicial (R$)"));

    JButton btnSave = new JButton("Começar");
    btnSave.setBackground(new Color(60, 179, 113));
    btnSave.setForeground(Color.WHITE);

    add(new JLabel(" Faça Seu Cadastro!", SwingConstants.CENTER));
    add(txtName);
    add(txtBalance);
    add(btnSave);

    btnSave.addActionListener(e -> {
      try {
        String nome = txtName.getText();
        String saldoStr = txtBalance.getText().replace(",", ".");
        BigDecimal saldo = new BigDecimal(saldoStr);

        userService.registerNewUser(nome, saldo);

        JOptionPane.showMessageDialog(this, "Bem-vindo, " + nome + "!");

        new MainFrame().setVisible(true);
        dispose();

      } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Valor do saldo inválido.");
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
      }
    });
  }
}