package model;

import global.Globals;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Represents a worker in the query system
 * @author Jeroen
 *
 */
public class Worker extends Component {

	public Worker(String ip, Connection con) {
		super(ip, con);
		//TODO temp currently placeholder
		String[] temp = {"cpu", "hdd", "mem"};
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

	public Worker(InetSocketAddress addr, Connection con) {
		super(addr, con);
		//TODO temp currently placeholder
		String[] temp = {"cpu", "hdd", "mem"};
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

	@Override
	protected String[] parseInput(String message) {
		// TODO makeparser
		return null;
	}
}
