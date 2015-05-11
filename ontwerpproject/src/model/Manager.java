package model;

import global.Globals;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Represents a clustermanager in the query system
 * 
 * @author Jeroen
 *
 */
public class Manager extends Component {

	public Manager(String ip, Connection con) {
		super(ip, con);
		//TODO temp currently placeholder
		String[] temp = {"cpu", "disk", "mem"};
		collumnList = temp;
		String sql = "INSERT INTO " + Globals.getTableName(getTableName())
				+ " VALUES( ?,  ?";
		for (int i = 0; i < collumnList.length; ++i) {
			sql += ",  ?";
		}
		sql += ")";
		try {
			insert = conn.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Manager(InetSocketAddress addr, Connection con) {
		super(addr, con);
		//TODO temp currentlyplaceholder
		String[] temp = {"cpu", "disk", "mem"};
		collumnList = temp;
	}

	@Override
	protected String[] parseInput(String message) {
		// TODO make parser
		return null;
	}

}
