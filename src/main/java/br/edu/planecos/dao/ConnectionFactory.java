package br.edu.planecos.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import br.edu.planecos.exception.DbException;

public class ConnectionFactory {

  private ConnectionFactory() {
    throw new IllegalStateException("Utility class");
  }

  public static Connection getConnection() {
    try {
      Properties props = loadProperties();
      String url = props.getProperty("db.url");
      String user = props.getProperty("db.user");
      String pass = props.getProperty("db.password");

      return DriverManager.getConnection(url, user, pass);
    } catch (SQLException | IOException e) {
      throw new DbException("Erro ao conectar com o banco de dados: " + e.getMessage(), e);
    }
  }

  private static Properties loadProperties() throws IOException {
    Properties props = new Properties();
    try (InputStream input = ConnectionFactory.class.getResourceAsStream("/db.properties")) {
      if (input == null) {
        throw new IOException("Arquivo db.properties não encontrado na raiz do resources.");
      }
      props.load(input);
    }
    return props;
  }
}