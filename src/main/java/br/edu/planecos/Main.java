package br.edu.planecos;

import br.edu.planecos.service.UserService;
import br.edu.planecos.view.MainFrame;
import br.edu.planecos.view.RegisterFrame;

import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

public class Main {

    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) { // ou "Windows"
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // Usamos invokeLater para garantir que a GUI rode na Thread correta do Swing
        // (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                UserService userService = new UserService();

                if (userService.hasRegisteredUser()) {
                    System.out.println("Usuário encontrado! Abrindo Tela Principal...");
                    new MainFrame().setVisible(true);
                } else {
                    System.out.println("Primeiro acesso! Abrindo Tela de Cadastro...");
                    new RegisterFrame().setVisible(true);
                }

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erro fatal ao iniciar sistema: " + e.getMessage());
            }
        });
    }
}