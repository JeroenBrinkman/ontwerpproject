package model;

import global.Globals;
import global.Logger;
import global.Misc;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.SQLException;

import model.intelligence.Intelligence.ClosedException;
import model.intelligence.ManagerIntelligence;

/**
 * Represents a clustermanager in the query system
 * 
 * @author Jeroen
 *
 */
public class Manager extends Component {

	/**
	 * Constructor
	 * @requires addr != null
	 * @requires con != null
	 * @requires mod != null
	 * @throws ClosedException if the database fails during the construction
	 */
	public Manager(InetSocketAddress addr, Connection con, Model mod)
			throws ClosedException {
		super(addr, con);
		intel = new ManagerIntelligence(this, mod, con);
		collumnList = Misc.concat(Globals.MANAGER_CALLS,
				Globals.MANAGER_STATS);
		
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
		String[] parts;
		String[] lines = message.split("\n");
		long[] result = new long[Globals.MANAGER_STATS.length];
		String currentLine;
		for (int i = 0; i < Globals.MANAGER_STATS.length; i++) {
			currentLine = lines[i];
			// regels met w[X] erin komen als het goed is alleen voor na alle
			// relevante informatie.
			// if(!currentLine.contains("[w")){
			parts = currentLine.split(":");
			currentLine = parts[1];
			currentLine = currentLine.replaceAll("\\s+", "");
			//niet toepasbaar als w[X] voorkomt voor relevante informatie
			result[i] = Long.parseLong(currentLine);
		}
		return result;
	}

	@Override
	public String getTableName() {
		return "m" + super.getTableName();
	}

	@Override
	public String[] getCalls() {
		return Globals.MANAGER_CALLS;
	}

	@Override
	public int getType() {
		return Globals.ID_MANAGER;
	}

}
