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

	public Worker(InetSocketAddress addr, Connection con, Model mod) {
		super(addr, con);
		intel = new WorkerIntelligence(this, mod);
		//TODO temp currently placeholder
		String[] temp = {"cpu", "hdd", "mem", "time"};
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
