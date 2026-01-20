package br.edu.planecos.dao;

import br.edu.planecos.exception.DbException;
import br.edu.planecos.model.User;
import java.sql.*;

public class UserDAO {

  public void save(User user) {
    String sql = "INSERT INTO users (full_name, current_balance) VALUES (?, ?)";

    try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

      stmt.setString(1, user.getFullName());
      stmt.setBigDecimal(2, user.getCurrentBalance());

      stmt.execute();

      try (ResultSet rs = stmt.getGeneratedKeys()) {
        if (rs.next()) {
          user.setId(rs.getLong(1));
        }
      }
    } catch (SQLException e) {
      throw new DbException("Erro ao salvar usuário: " + e.getMessage(), e);
    }
  }

  public void update(User user) {
    String sql = "UPDATE users SET full_name = ?, current_balance = ? WHERE id = ?";
    try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, user.getFullName());
      stmt.setBigDecimal(2, user.getCurrentBalance());
      stmt.setLong(3, user.getId());
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new DbException("Erro ao atualizar usuário", e);
    }
  }

  public User findFirstUser() {
    String sql = "SELECT id, full_name, current_balance, created_at FROM users LIMIT 1";
    try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {

      if (rs.next()) {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setFullName(rs.getString("full_name"));
        user.setCurrentBalance(rs.getBigDecimal("current_balance"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
      }
      return null;
    } catch (SQLException e) {
      throw new DbException("Erro ao buscar usuário: " + e.getMessage(), e);
    }
  }

  public void updateBalance(User user) {
    String sql = "UPDATE users SET current_balance = ? WHERE id = ?";

    try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setBigDecimal(1, user.getCurrentBalance());
      stmt.setLong(2, user.getId());
      stmt.executeUpdate();

    } catch (SQLException e) {
      throw new DbException("Erro ao atualizar saldo do usuário: " + e.getMessage(), e);
    }
  }
}