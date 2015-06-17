package model;

import global.Globals;
import global.Logger;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.SQLException;

import model.intelligence.DatabaseIntelligence;
import model.intelligence.Intelligence.ClosedException;

/**
 * Represents a database in the query system
 * empty class for now
 * @author Jeroen
 *
 */
public class Database extends Component {

	public Database(InetSocketAddress addr, Connection con, Model mod) throws ClosedException {
		super(addr, con);
		intel = new DatabaseIntelligence(this, mod, con);
		collumnList = Globals.DATABASE_CALLS;
		
		String sql = "INSERT INTO " + getTableName() + " VALUES( ?,  ?";
		for (int i = 0; i < collumnList.length; ++i) {
			sql += ",  ?";
		}
		sql += ")";
		try {
			insert = conn.prepareStatement(sql);
		} catch (SQLException e) {
			intel.databaseError(e);
		}
		
		Logger.log("Constructor for " + getTableName() + " completed");
	}

	@Override
	public long[] parseInput(String message) {
		return null;
	}

	@Override
	public String getTableName() {
		return "d" + super.getTableName();
	}

	@Override
	public String[] getCalls() {
		return Globals.DATABASE_CALLS;
	}

}
