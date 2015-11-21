package javaMyAdmin.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Database {

	private ArrayList<Table> table = new ArrayList<Table>();
	private String dbname;
	private Connection connect;

	public Database(String dbname) throws SQLException {
		this.dbname = dbname;
		connect = DBManager.doConnection(dbname);
	}
	
	//Methoden
	public void loadTables() throws SQLException {
		table.clear();
		DatabaseMetaData md = connect.getMetaData();
		ResultSet rs = md.getTables(null, null, "%", null);
		while (rs.next()) {
			String name = rs.getString(3);
			table.add(new Table(name, new ArrayList<String>(), connect, dbname));
		}
	}

	public ArrayList<Table> getTable() throws SQLException {
		loadTables();
		return table;
	}

	public Table getTable(int tableNumber) throws SQLException {
		if (table.isEmpty())
			loadTables();
		return table.get(tableNumber);
	}

	public Table getTable(String name) throws SQLException {
		Table t = null;
		if (table.isEmpty())
			loadTables();
		int i = 0;
		for (Table lst : table) {
			if (lst.getName().equalsIgnoreCase(name)) {
				break;
			}
			i++;
		}
		try {
			t = table.get(i);
		} catch (Exception e) {
			System.out.println(e);
		}
		return t;
	}

	public String getDbname() {
		return dbname;
	}

	public void renameDatabase(String newName) throws SQLException {
		loadTables();
		connect.createStatement().executeUpdate("CREATE DATABASE " + newName);
		for(Table lst : getTable()){
			connect.createStatement().executeUpdate("RENAME TABLE " + dbname + "." + lst.getName()+ " TO " + newName + "." + lst.getName());
		}
		connect.createStatement().executeUpdate("DROP DATABASE " + dbname);
		dbname = newName;
		DBManager.getInstance().loadDB();
	}

	public void rmTable(String tablename) throws SQLException {
		connect.createStatement().executeUpdate("DROP TABLE " + tablename);
		loadTables();
	}

	public void addTable(String tablename, ArrayList<String> titles, ArrayList<String> datatypes, ArrayList<String> length, ArrayList<Boolean> check, ArrayList<String> index)
			throws SQLException {
		String cmd = "";
		String checknull = "";
		String komma = ",";
		for (int i = 0; i < titles.size(); i++) {
			if (i == titles.size() - 1) {
				komma = "";
			}
			if (check.get(i) == true) {
				checknull = "DEFAULT NULL";
			} else {
				checknull = "NOT NULL";
			}

			if (length.get(i).equals("")) {
				length.set(i, "10");
			}
			cmd += "`" + titles.get(i) + "` " + datatypes.get(i) + "(" + length.get(i) + ") " + checknull + komma + "\n";
		}
		cmd = "CREATE TABLE " + /* IF NOT EXISTS + */"`" + tablename + "` ( " + cmd + ") ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=13 ;\n";
		try {
			connect.createStatement().executeUpdate(cmd);
		} catch (Exception e) {
			System.out.println(e);
		}
		for (int i = 0; i < index.size(); i++) {
			if (index.get(i).equals("PRIMARY")) {
				connect.createStatement().executeUpdate("ALTER TABLE `" + tablename + "`" + " ADD PRIMARY KEY(`" + titles.get(i) + "`);");
			}
		}
		loadTables();
	}
	
	public Table executeSQL(String cmd) throws SQLException{
//		Table t = new Table(null, new ArrayList<String>(), connect, null);
//		t.isAbstract(true);
//		if(connect.createStatement().execute(cmd)){
//			t.loadLines(connect.createStatement().executeQuery(cmd));
//		}else{
//			connect.createStatement().executeUpdate(cmd);
//			loadTables();
//			t = null;
//		}
		Table t = Functions.executeFinal(cmd, connect, dbname);
		loadTables();
		return t;
	}
}
