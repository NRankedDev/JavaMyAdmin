package javaMyAdmin.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DBManager extends Connector {
	// <<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>>

	private Connection connect;
	private static String url;
	private static String password;
	private static String user;
	private ArrayList<Database> db = new ArrayList<Database>();

	// <<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>>

	// <<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>>
	public DBManager(String url, String user, String password) throws SQLException {
		this.user = user;
		this.password = password;
		this.url = url;
		connect = doConnection("");
	}

	// <<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>>

	// <<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>>
	/**/public static Connection doConnection(String dbname) throws SQLException {
		Connector c = new Connector();

		return c.getConnection((url + "/" + dbname), user, password);
	}

	// <<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>>

	// <<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>>
	// //Methoden
	/**/public ArrayList<Database> loadDB() throws SQLException {
		ResultSet rs = connect.getMetaData().getCatalogs();
		while (rs.next()) {
			db.add(new Database(rs.getString("TABLE_CAT")));
		}
		return db;
	}

	/**/public ArrayList<Database> getDB() throws SQLException {
		if (db.isEmpty())
			loadDB();
		return db;
	}

	/**/public Database getDB(int DatabaseNumber) throws SQLException {
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

	// <<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>><<>>

	/* still gelegt */public Database selectDB(int c) throws SQLException {
		Database d;
		try {
			db.get(c);
		} catch (Exception e) {
			loadDB();
			System.out.println("Error Code:\n" + e + "\nDatenbanken wurden nicht vor geladen!!!\n\n");
		}
		d = db.get(c);
		return d;
	}
}
