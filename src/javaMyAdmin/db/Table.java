package javaMyAdmin.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;


public class Table {
	private ArrayList<String> columnNames = new ArrayList<String>();;
	private ArrayList<Line> lines = new ArrayList<Line>();
	private String name;
	private Connection connect;
	private String dbname;

	public Table(String name, ArrayList<String> columnNames, Connection connect, String dbname) throws SQLException {
		this.dbname = dbname;
		this.name = name;
		this.connect = connect;
		this.columnNames = columnNames;
	}

	public String getName() {
		return name;
	}

	public void AddColumn(String name) {
		columnNames.add(name);
	}

	public void AddLine(Line line) {
		lines.add(line);
	}

	public void clear() {
		columnNames.clear();
		lines.clear();
	}

	public ArrayList<String> getColumnNames() throws SQLException {
		return columnNames;
	}

	public String getColumnNames(int i) throws SQLException {
		return columnNames.get(i);
	}

	public ArrayList<Line> getLines() throws SQLException {
		loadLines(null);
		return lines;
	}

	public Line getLines(int i) throws SQLException {
		if (lines.isEmpty()) {
			loadLines(null);
		}
		return lines.get(i);
	}
	
	public ArrayList<Line> getLines(ResultSet rs) throws SQLException {
		loadLines(rs);
		return lines;
	}
	
	public String getDatentyp(String column) throws SQLException{
		return getColumnInfo(column, 1);
	}
	
	public String getLength(String column) throws SQLException{
		return getColumnInfo(column, 2);
	}
	
	public boolean getNull(String column) throws SQLException{
		return getColumnInfo(column, 3).equals(true) ? true : false;
	}
	
	public String getIndex(String column) throws SQLException{
		return getColumnInfo(column, 4);
		
	}

	public void setValue(int line, int column, String value) throws SQLException {
		if (lines.isEmpty()) {
			loadLines(null);
		}
		connect.createStatement().executeUpdate("UPDATE `"+dbname+"`.`" + getName() + "` SET `" + getColumnNames(column) + "` = '" + value + "' WHERE `" + getColumnNames(0) + "` = " + getLines(line).getValues(0));
	}

	public void loadLines(ResultSet rs) throws SQLException {
		clear();
		if (rs == null) {
			rs = connect.createStatement().executeQuery("SELECT * FROM `" + getName() + "`");
		}
		ResultSetMetaData metaData = rs.getMetaData();
		int i = 1;
		int count = metaData.getColumnCount();
		while (i < count + 1) {
			AddColumn(metaData.getColumnName(i)); // fehler hier irwo
			i++;
		}
		while (rs.next()) {
			Line line = new Line();
			for (int a = 1; a < count + 1; a++) {
				try {
					line.AddValue(rs.getString(a));
				} catch (Exception e) {
					line.AddValue(null);
				}
			}
			AddLine(line);
		}
	}
	
	public void addTupel(ArrayList<String> input) throws SQLException{
		String cmd = "INSERT INTO `"+ getName() +"` (";
		String value = " VALUES (";
		for(int i = 0; i < columnNames.size(); i++){
			cmd += "`" + columnNames.get(i) + "`";
			value += "'" + input.get(i) + "'";
			if(columnNames.size() != i+1){
				cmd += ", ";
				value += ", ";
			}
		}
		cmd += ")" + value + ");";
		connect.createStatement().executeUpdate(cmd);
	}
	
	public void rmTupel(ArrayList<String> values) throws SQLException{
		String cmd = "DELETE FROM `"+ getName() +"` WHERE";
		for(int i = 0; i < values.size(); i++){
			cmd += " `"+getColumnNames(i)+"`='"+values.get(i)+"'";
			if(values.size() != i+1){
				cmd += " AND ";
			}
		}
		connect.createStatement().executeUpdate(cmd);
	}
	
	public ArrayList<Line> search(String suche, int column) throws SQLException {
		return getLines(connect.createStatement().executeQuery("SELECT * FROM `" + getName() + "` WHERE `" + getColumnNames(column) + "` =" + suche));
	}
	
	public String getColumnInfo(String column, int i) throws SQLException{
		String value;
		ResultSet rs;
		
		switch(i){
		case 1:
			value = "DATA_TYP";
			break;
		case 2:
			value = "CHARAKTER_MAXIMUM_LENGTH";
			break;
		case 3:
			value = "IS_NULLABLE";
			break;
		case 4:
			value = "COLUMN_KEY";
			break;
		default:
			value = null;
			break;
		}
	
		return (rs=connect.createStatement().executeQuery("select `"+value+"` from information_schema.columns where table_name='"+name+"' and column_name like '"+column+"'")).next() ? rs.getString(1) : null;
	}

}
