package model;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.SQLException;

import model.intelligence.WorkerIntelligence;

/**
 * Represents a worker in the query system
 * @author Jeroen
 *
 */
public class Worker extends Component {

	public Worker(String ip, Connection con) {
		super(ip, con);
		intel = new WorkerIntelligence(this);
		//TODO temp currently placeholder
		String[] temp = {"cpu", "hdd", "mem"};
		collumnList = temp;
		String sql = "INSERT INTO " + getTableName()
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
		intel = new WorkerIntelligence(this);
		//TODO temp currently placeholder
		String[] temp = {"cpu", "hdd", "mem"};
		collumnList = temp;
		
		String sql = "INSERT INTO " + getTableName()
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

	@Override
	public String getTableName() {
		return "w" + super.getTableName();
	}
}
