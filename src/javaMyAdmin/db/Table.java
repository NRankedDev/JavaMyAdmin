package javaMyAdmin.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

public class Table {
	private ArrayList<String> columnNames = new ArrayList<String>();;
	private ArrayList<Line> lines = new ArrayList<Line>();
	private String name;
	private Connection connect;

	public Table(String name, ArrayList<String> columnNames, Connection connect) throws SQLException {
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

	// test--------
	// /*test*/public void setLine(int s, ArrayList<String> values) throws
	// SQLException{
	// ResultSet rs = connect.createStatement().executeQuery("UPDATE `"+
	// getName() + "` SET `zeny` = '999999' WHERE `char`.`char_id` = 150000;);
	// Line line = new Line();
	// for(int i = 0; i < columnNames.size(); i++){
	// line.AddValue(values.get(0));
	// }
	// lines.set(s, line);
	/* test */public void setValue(int line, int column, int value) throws SQLException {
		if (lines.isEmpty()) {
			loadLines(null);
		}
		connect.createStatement().executeUpdate("UPDATE `" + getName() + "` SET `" + getColumnNames(column) + "` = '" + value + "' WHERE `" + getColumnNames(0) + "` = " + getLines(line).getValues(0));
		// ResultSet rs = connect.createStatement().executeQuery("UPDATE `"+
		// getName() +
		// "` SET `zeny` = '999999' WHERE `char`.`char_id` = 150000;");
	}

	public ArrayList<Line> search(String suche, int column) throws SQLException {
		ResultSet rs = connect.createStatement().executeQuery("SELECT * FROM `" + getName() + "` WHERE `" + getColumnNames(column) + "` =" + suche);
		return getLines(rs);
	}

}
