package javaMyAdmin.db;

import java.sql.SQLException;
import java.util.ArrayList;

public class Debug {
	public static String check(String url) throws SQLException{
		if(url.equalsIgnoreCase("debug")){
			Debug.run();
			url = "localhost";
		}
		return url;
	}
	public static void run() throws SQLException{
		System.out.println("run Debug!");
		DBManager DBM = new DBManager();
		DBM.connect("127.0.0.1", "root", "");
		DBM.getDB("cdcol").getTable("t1").renameColumn("test99", "test");
		DBM.getDB("cdcol").getTable("t1").removeColumn("test2");
		System.out.println("end Debug!");
	}
}
