package model;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.SQLException;

import model.intelligence.DatabaseIntelligence;

/**
 * Represents a database in the query system
 * 
 * @author Jeroen
 *
 */
public class Database extends Component {

	public Database(String ip, Connection con, Model mod) {
		super(ip, con);
		intel = new DatabaseIntelligence(this, mod);
		//TODO tepm currentlyplaceholder
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

	public Database(InetSocketAddress addr, Connection con, Model mod) {
		super(addr, con);
		intel = new DatabaseIntelligence(this, mod);
		//TODO temp currentlyplaceholder
		String[] temp = {"cpu", "hdd", "mem"};
		collumnList = temp;
	}

	@Override
	protected String[] parseInput(String message) {
		// TODO make parser
		return null;
	}

	@Override
	public String getTableName() {
		return "d" + super.getTableName();
	}

}
