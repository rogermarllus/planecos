package br.edu.planecos.dao;

import br.edu.planecos.exception.DbException;
import br.edu.planecos.model.Expense;
import br.edu.planecos.model.enums.ExpenseCategory;
import br.edu.planecos.model.enums.ExpenseStatus;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDAO {

  public void save(Expense expense) {
    String sql = "INSERT INTO expenses (user_id, title, amount, status, category, expense_date) VALUES (?, ?, ?, ?, ?, ?)";

    try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

      stmt.setLong(1, expense.getUserId());
      stmt.setString(2, expense.getTitle());
      stmt.setBigDecimal(3, expense.getAmount());

      stmt.setString(4, expense.getStatus().name());
      stmt.setString(5, expense.getCategory().name());

      stmt.setDate(6, Date.valueOf(expense.getExpenseDate()));

      stmt.execute();

      try (ResultSet rs = stmt.getGeneratedKeys()) {
        if (rs.next()) {
          expense.setId(rs.getLong(1));
        }
      }
    } catch (SQLException e) {
      throw new DbException("Erro ao salvar despesa: " + e.getMessage(), e);
    }
  }

  public List<Expense> findAll(Long userId) {
    String sql = "SELECT id, user_id, title, amount, status, category, expense_date FROM expenses WHERE user_id = ? ORDER BY expense_date DESC";
    List<Expense> expenses = new ArrayList<>();

    try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setLong(1, userId);

      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          Expense exp = new Expense();
          exp.setId(rs.getLong("id"));
          exp.setUserId(rs.getLong("user_id"));
          exp.setTitle(rs.getString("title"));
          exp.setAmount(rs.getBigDecimal("amount"));

          exp.setStatus(ExpenseStatus.fromString(rs.getString("status")));
          exp.setCategory(ExpenseCategory.fromString(rs.getString("category")));

          exp.setExpenseDate(rs.getDate("expense_date").toLocalDate());

          expenses.add(exp);
        }
      }
    } catch (SQLException e) {
      throw new DbException("Erro ao listar despesas: " + e.getMessage(), e);
    }
    return expenses;
  }

  public void delete(Long id) {
    String sql = "DELETE FROM expenses WHERE id = ?";
    try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setLong(1, id);
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new DbException("Erro ao deletar despesa: " + e.getMessage(), e);
    }
  }

  public Expense findById(Long id) {
    String sql = "SELECT id, user_id, title, amount, status, category, expense_date FROM expenses WHERE id = ?";
    try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setLong(1, id);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          Expense exp = new Expense();
          exp.setId(rs.getLong("id"));
          exp.setUserId(rs.getLong("user_id"));
          exp.setTitle(rs.getString("title"));
          exp.setAmount(rs.getBigDecimal("amount"));
          exp.setStatus(ExpenseStatus.fromString(rs.getString("status")));
          exp.setCategory(ExpenseCategory.fromString(rs.getString("category")));
          exp.setExpenseDate(rs.getDate("expense_date").toLocalDate());
          return exp;
        }
      }
    } catch (SQLException e) {
      throw new DbException("Erro ao buscar despesa por ID", e);
    }
    return null;
  }

  public void update(Expense expense) {
    String sql = "UPDATE expenses SET title = ?, amount = ?, status = ?, category = ?, expense_date = ? WHERE id = ?";

    try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setString(1, expense.getTitle());
      stmt.setBigDecimal(2, expense.getAmount());
      stmt.setString(3, expense.getStatus().name());
      stmt.setString(4, expense.getCategory().name());
      stmt.setDate(5, java.sql.Date.valueOf(expense.getExpenseDate()));
      stmt.setLong(6, expense.getId());

      stmt.executeUpdate();

    } catch (SQLException e) {
      throw new DbException("Erro ao atualizar despesa", e);
    }
  }
}