package model;

import global.Globals;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Represents a database in the query system
 * 
 * @author Jeroen
 *
 */
public class Database extends Component {

	public Database(String ip, Connection con) {
		super(ip, con);
		String[] temp = {"cpu", "disk", "mem"};
		collumnList = temp;
		// TODO time from worker or system?
		String sql = "INSERT INTO " + Globals.getTableName(adr.toString())
				+ " VALUES( ?,  ?";
		for (int i = 0; i < collumnList.length; ++i) {
			sql += ",  ?";
		}
		sql += ")";
		try {
			insert = conn.prepareStatement(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Database(InetAddress ip, Connection con) {
		super(ip, con);
		String[] temp = {"cpu", "disk", "mem"};
		collumnList = temp;
	}

	@Override
	protected String[] parseInput(String message) {
		// TODO Auto-generated method stub
		return null;
	}

}
