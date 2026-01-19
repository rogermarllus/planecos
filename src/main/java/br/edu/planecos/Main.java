package br.edu.planecos;

import br.edu.planecos.service.UserService;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

public class Main {

    public static void main(String[] args) {
        // Usamos invokeLater para garantir que a GUI rode na Thread correta do Swing
        // (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                UserService userService = new UserService();

                if (userService.hasRegisteredUser()) {
                    System.out.println("Usuário encontrado! Abrindo Tela Principal...");
                    // TODO: Aqui chamaremos: new MainFrame().setVisible(true);
                } else {
                    System.out.println("Primeiro acesso! Abrindo Tela de Cadastro...");
                    // TODO: Aqui chamaremos: new RegisterFrame().setVisible(true);
                }

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erro fatal ao iniciar sistema: " + e.getMessage());
            }
        });
    }
}