package javaMyAdmin.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connector {
	public Connection getConnection(String url, String user, String password) throws SQLException {
		url = "jdbc:mysql://" + url;
		Connection connect = DriverManager.getConnection(url, user, password);
		return connect;
	}

	public void closeConnection(Connection connect) throws SQLException {
		if (connect != null) {
			connect.close();
		}
	}

}
