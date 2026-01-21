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
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            try {
                UserService userService = new UserService();

                if (userService.hasRegisteredUser()) {
                    new MainFrame().setVisible(true);
                } else {
                    new RegisterFrame().setVisible(true);
                }

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erro fatal ao iniciar sistema: " + e.getMessage());
            }
        });
    }
}