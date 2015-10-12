package javaMyAdmin.db;

import java.sql.SQLException;

public class Main {
	public static void main(String args[]) throws SQLException {
		DBManager DBM = new DBManager("127.0.0.1", "root", "");

		// System.out.println(DBM.getDB(8).getTable(5).getName());
		// System.out.println(DBM.getDB(1).getTable(0).getName());
		// System.out.println(DBM.getDB(8).getTable(5).getName());

		// System.out.println(DBM.getDB(8).getTable(5).getName());
		// for(Line lst : DBM.getDB(8).getTable(5).getLines())
		// System.out.println(lst.getValues());
		// JOptionPane.showConfirmDialog(null, "Änder was an der Table");
		// System.out.println("---------------------------");
		// for(Line lst : DBM.getDB(8).getTable(5).getLines())
		// System.out.println(lst.getValues());
		System.out.println(DBM.getDB("rathena").getTable(0).getName());
		// System.out.println(DBM.getDB(8).searchInTable(5, "150001", 0));
		System.out.println();
		System.out.println();
		System.out.println();
		// for(Line lst : DBM.getDB(8).getTable(5).search("150001", 0)){
		// System.out.println(lst.getValues());
		// }

	}
}
