package br.edu.planecos.service;

import br.edu.planecos.dao.UserDAO;
import br.edu.planecos.model.User;
import java.math.BigDecimal;

public class UserService {

  private final UserDAO userDAO;

  public UserService() {
    this.userDAO = new UserDAO();
  }

  public User registerNewUser(String fullName, BigDecimal initialBalance) {
    if (fullName == null || fullName.trim().isEmpty()) {
      throw new IllegalArgumentException("O nome do usuário não pode ser vazio.");
    }
    if (initialBalance == null || initialBalance.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("O saldo inicial não pode ser negativo.");
    }

    User newUser = new User();
    newUser.setFullName(fullName);
    newUser.setCurrentBalance(initialBalance);

    userDAO.save(newUser);
    return newUser;
  }

  public void updateUserProfile(User user) {
    if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
      throw new IllegalArgumentException("Nome não pode ser vazio.");
    }
    userDAO.update(user);
  }

  public User getCurrentUser() {
    return userDAO.findFirstUser();
  }

  public boolean hasRegisteredUser() {
    return userDAO.findFirstUser() != null;
  }
}