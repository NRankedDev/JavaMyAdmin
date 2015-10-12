package javaMyAdmin.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connector {
	public Connection getConnection(String url, String user, String password) throws SQLException {
		url = "jdbc:mysql://" + url;
		// System.out.println(url);
		Connection connect = DriverManager.getConnection(url, user, password);
		return connect;
	}

	public void closeConnection(Connection connect) throws SQLException {
		if (connect != null) {
			connect.close();
		}
	}

}

// package main;
//
// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.SQLException;
//
// public class Connector {
//
// private static String url;
// private static String user;
// // private static String password;
//
// public Connector(String url, String user, String password){
// this.password = password;
// this.url = url;
// this.user = user;
// }
// public static Connection getConnection() throws SQLException{
// Connection connect = DriverManager.getConnection(url ,user,password);
// return connect;
// }
//
// public static void closeConnection(Connection connect) throws SQLException{
// if(connect != null){
// connect.close();
// }
// }
// public static String getUrl() {
// return url;
// }
// public static void setUrl(String url) {
// Connector.url = url;
// }
// public static String getUser() {
// return user;
// }
// public static void setUser(String user) {
// Connector.user = user;
// }
// public static String getPassword() {
// return password;
// }
// public static void setPassword(String password) {
// Connector.password = password;
// }
//
//
// }
