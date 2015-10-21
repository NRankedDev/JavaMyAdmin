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
		DBManager DBM = new DBManager("127.0.0.1", "root", "");
		ArrayList<String> input = new ArrayList<String>();
		input.add("Till ist toll2");
		input.add("Norman");
		input.add("2015");
		input.add("97");
		DBM.getDB(0).getTable(1).addTupel(input);
		
		
		
		
		System.out.println("end Debug!");
	}
}
