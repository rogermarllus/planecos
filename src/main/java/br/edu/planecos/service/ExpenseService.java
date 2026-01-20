package br.edu.planecos.service;

import br.edu.planecos.dao.ExpenseDAO;
import br.edu.planecos.dao.UserDAO;
import br.edu.planecos.model.Expense;
import br.edu.planecos.model.User;
import br.edu.planecos.model.enums.ExpenseStatus;

import java.math.BigDecimal;
import java.util.List;

public class ExpenseService {

  private final ExpenseDAO expenseDAO;
  private final UserDAO userDAO;

  public ExpenseService() {
    this.expenseDAO = new ExpenseDAO();
    this.userDAO = new UserDAO();
  }

  // 1. CADASTRO: Se nascer PAGA, desconta do saldo.
  public void registerExpense(Expense expense) {
    if (expense.getAmount() == null || expense.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("O valor da despesa deve ser positivo.");
    }

    // Salva a despesa primeiro
    expenseDAO.save(expense);

    // Regra: Se já nasce PAGA, debita do usuário
    if (expense.getStatus() == ExpenseStatus.PAID) {
      User user = userDAO.findFirstUser();
      if (user != null) {
        BigDecimal novoSaldo = user.getCurrentBalance().subtract(expense.getAmount());
        user.setCurrentBalance(novoSaldo);
        userDAO.updateBalance(user);
      }
    }
  }

  // 2. ALTERAR STATUS: Inverte e ajusta o saldo (Estorno ou Pagamento)
  public void toggleExpenseStatus(Long expenseId) {
    Expense expense = expenseDAO.findById(expenseId);
    if (expense == null) {
      throw new IllegalArgumentException("Despesa não encontrada para o ID: " + expenseId);
    }

    User user = userDAO.findFirstUser();
    if (user == null) {
      throw new IllegalStateException("Usuário não encontrado.");
    }

    // Lógica da troca
    if (expense.getStatus() == ExpenseStatus.PENDING) {
      // ESTAVA PENDENTE -> VIROU PAGA
      // Ação: Pagar (Subtrair do saldo)
      expense.setStatus(ExpenseStatus.PAID);
      user.setCurrentBalance(user.getCurrentBalance().subtract(expense.getAmount()));
    } else {
      // ESTAVA PAGA -> VIROU PENDENTE
      // Ação: Estornar (Devolver valor ao saldo)
      expense.setStatus(ExpenseStatus.PENDING);
      user.setCurrentBalance(user.getCurrentBalance().add(expense.getAmount()));
    }

    // Persiste as alterações no banco
    expenseDAO.update(expense); // Atualiza o status da despesa
    userDAO.updateBalance(user); // Atualiza o saldo do usuário
  }

  public List<Expense> listAllExpenses() {
    User user = userDAO.findFirstUser();
    if (user != null) {
      return expenseDAO.findAll(user.getId());
    }
    return List.of();
  }

  public void deleteExpense(Long id) {
    expenseDAO.delete(id);
  }
}