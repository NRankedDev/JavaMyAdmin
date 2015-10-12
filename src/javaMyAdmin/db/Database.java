package javaMyAdmin.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

public class Database {

	// <<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>>
	// //Variablen
	/**/private ArrayList<Table> table = new ArrayList<Table>();
	/**/private String dbname;
	/**/private Connection connect;

	// <<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>>

	// <<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>>
	// //Constructor
	/**/public Database(String dbname) throws SQLException {
		/**/this.dbname = dbname;
		/**/connect = DBManager.doConnection(dbname);
		/**/}

	// <<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>>

	// <<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>>
	// //Methoden
	/**/public void loadTables() throws SQLException {
		table.clear();
		DatabaseMetaData md = connect.getMetaData();
		ResultSet rs = md.getTables(null, null, "%", null);
		while (rs.next()) {
			String name = rs.getString(3);
			ResultSet rs2 = connect.createStatement().executeQuery("SELECT * FROM `" + name + "`");
			ArrayList<String> columns = new ArrayList<String>();
			ResultSetMetaData metaData = rs2.getMetaData(); // fehler hier irwo
			int i = 1;
			int count = metaData.getColumnCount();
			while (i < count + 1) {
				columns.add(metaData.getColumnName(i));
				i++;
			}
			table.add(new Table(name, columns, connect));
		}
	}

	/**/public ArrayList<Table> getTable() throws SQLException {
		loadTables();
		return table;
	}

	/**/public Table getTable(int tableNumber) throws SQLException {
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

	/**/public String getDbname() {
		return dbname;
	}

	/**/public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	// <<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>>

	/* still gelegt */public Table selectTable(int c) throws SQLException {
		Table t = null; // vllt ist es richtig Table t;
		// try{
		// table.get(c);
		// }catch(Exception e){
		// loadTables();
		// System.out.println("Error Code:\n" + e +
		// "\nTabellen wurden nicht vor geladen!!!\n\n");
		// }
		// t = table.get(c);
		// ResultSet rs =
		// connect.createStatement().executeQuery("SELECT * FROM `" +
		// t.getName() + "`");
		// ResultSetMetaData metaData = rs.getMetaData();
		// int i = 1;
		// int count = metaData.getColumnCount();
		// while(i < count+1){
		// t.AddColumn(metaData.getColumnName(i));
		// i++;
		// }
		// while(rs.next()){
		// Line line = new Line();
		// for(int a = 1; a < count+1; a++){
		// try{
		// line.AddValue(rs.getString(a));
		// }catch(Exception e){
		// line.AddValue(null);
		// }
		// }
		// t.AddLine(line);
		// }
		return t;
	}

	/* test */public Table search() {
		// sucht durch alle tabllen in der datenbank
		// sql command
		return null;
	}
}
