package javaMyAdmin.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class DBManager extends Connector {
	
	private Connection connect;
	private static String url;
	private static String password;
	private static String user;
	private static DBManager instance = new DBManager();
	private ArrayList<Database> db = new ArrayList<Database>();

	public DBManager(){
		instance = this;
	}
	
	public void connect(String url, String user, String password) throws SQLException{
		this.url = Debug.check(url);
		this.user = user;
		this.password = password;
		connect = doConnection("");
	}
	
    public static DBManager getInstance() {
        return instance;
    }
    
    public void close() throws SQLException {
    	 closeConnection(connect);
    }
    
	public static Connection doConnection(String dbname) throws SQLException {
		Connector c = new Connector();

		return c.getConnection((url + "/" + dbname), user, password);
	}


	//Methoden
	public ArrayList<Database> loadDB() throws SQLException {
		db.clear();
		ResultSet rs = connect.getMetaData().getCatalogs();
		while (rs.next()) {
			db.add(new Database(rs.getString("TABLE_CAT")));
		}
		return db;
	}

	public ArrayList<Database> getDB() throws SQLException {
		if (db.isEmpty())
			loadDB();
		return db;
	}

	public Database getDB(int DatabaseNumber) throws SQLException {
		if (db.isEmpty())
			loadDB();
		return db.get(DatabaseNumber);
	}

	public Database getDB(String dbname) throws SQLException {
		Database d = null;
		if (db.isEmpty())
			loadDB();
		int i = 0;
		for (Database lst : db) {
			if (lst.getDbname().equalsIgnoreCase(dbname)) {
				break;
			}
			i++;
		}
		try {
			d = db.get(i);
		} catch (Exception e) {
			System.out.println(e);
		}
		return d;
	}
	
	public String getUrl(){
		return url;
	}
	public void addDB(String dbname) throws SQLException{
		connect.createStatement().executeUpdate("CREATE DATABASE " + dbname);
		loadDB();
	}
	
	public void rmDB(String dbname) throws SQLException {
		connect.createStatement().executeUpdate("DROP DATABASE " + dbname);
		loadDB();
	}
	
	public Table executeSQL(String cmd) throws SQLException{
		return Functions.executeFinal(cmd, connect, null);
	}
}
