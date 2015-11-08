package javaMyAdmin.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class Functions {
	public static Table executeFinal(String cmd, Connection connect, String dbname) throws SQLException{
		String[] parts = cmd.split(";");
		Table t = null;
		for(int i = 0; i < parts.length; i++){
			if(dbname != null) connect.createStatement().executeQuery("USE `"+dbname+"`");
			try{ //unsauber
				t = new Table(null, new ArrayList<String>(), connect, null);
				t.isAbstract(true);
				t.loadLines(connect.createStatement().executeQuery(parts[i]));
			}catch(Exception e){
				connect.createStatement().executeUpdate(parts[i]);
			}
		}
		return t;
	}
}
