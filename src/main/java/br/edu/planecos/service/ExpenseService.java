package br.edu.planecos.service;

import br.edu.planecos.dao.ExpenseDAO;
import br.edu.planecos.dao.UserDAO;
import br.edu.planecos.model.Expense;
import br.edu.planecos.model.User;
import br.edu.planecos.model.enums.ExpenseStatus;
import java.util.List;

public class ExpenseService {

  private final ExpenseDAO expenseDAO;
  private final UserDAO userDAO;

  public ExpenseService() {
    this.expenseDAO = new ExpenseDAO();
    this.userDAO = new UserDAO();
  }

  public void registerExpense(Expense expense) {
    // 1. Validações básicas
    if (expense.getAmount().signum() <= 0) {
      throw new IllegalArgumentException("O valor da despesa deve ser positivo.");
    }

    // 2. Salva a despesa
    expenseDAO.save(expense);

    // 3. Regra de Negócio: Se já foi paga, abate do saldo do usuário
    if (expense.getStatus() == ExpenseStatus.PAID) {
      User user = userDAO.findFirstUser(); // Busca o usuário atual
      if (user != null) {
        // Subtrai do saldo: saldoAtual - valorDespesa
        user.setCurrentBalance(user.getCurrentBalance().subtract(expense.getAmount()));
        userDAO.updateBalance(user);
      }
    }
  }

  public List<Expense> listAllExpenses() {
    User user = userDAO.findFirstUser();
    if (user == null) {
      throw new IllegalStateException("Nenhum usuário cadastrado para listar despesas.");
    }
    return expenseDAO.findAll(user.getId());
  }

  public void deleteExpense(Long expenseId) {
    // Em um cenário real, se deletar uma despesa PAGA, deveríamos estornar o valor
    // ao saldo?
    // Por simplicidade, vamos apenas deletar por enquanto.
    expenseDAO.delete(expenseId);
  }
}