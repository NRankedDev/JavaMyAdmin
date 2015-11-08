package javaMyAdmin.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class Functions {
	public static Table executeFinal(String cmd, Connection connect, String dbname) throws SQLException{
		String[] parts = cmd.split(";");
		Table t = new Table(null, new ArrayList<String>(), connect, null);
		for(int i = 0; i < parts.length; i++){
			t.isAbstract(true);
			if(dbname != null) connect.createStatement().executeQuery("USE `"+dbname+"`");
			if(connect.createStatement().execute(parts[i])){
				t.loadLines(connect.createStatement().executeQuery(parts[i]));
			}else{
				connect.createStatement().executeUpdate(parts[i]);
				t = null;
			}
		}
		return t;
	}
}
