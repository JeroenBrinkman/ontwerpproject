package model;

import global.Globals;
import global.Logger;
import global.Misc;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.SQLException;

import model.intelligence.Intelligence.ClosedException;
import model.intelligence.WorkerIntelligence;

/**
 * Represents a worker in the query system
 * 
 * @author Jeroen
 *
 */
public class Worker extends Component {

	/** 
	 * creates a new worker AKA constructor
	 * @requires addr != null
	 * @requires con != null
	 * @requires mod != null
	 * @throws ClosedException
	 */
	public Worker(InetSocketAddress addr, Connection con, Model mod)
			throws ClosedException {
		super(addr, con);
		intel = new WorkerIntelligence(this, mod, con);
		collumnList = Misc.concat(Globals.WORKER_CALLS, Globals.WORKER_STATS);

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

		long[] result = new long[Globals.WORKER_STATS.length]; //List<String> result = new ArrayList<String>();
		int resultIndex = 0;
		String currentLine;

		for(int i = 0; i < lines.length; i++){
			currentLine = lines[i];
			if (!currentLine.contains("last")) {
				parts = currentLine.split(":");
				currentLine = parts[1];
				currentLine = currentLine.replaceAll("\\s+", "");

				result[resultIndex++] = Long.parseLong(currentLine);

			}
		}
		
		return result;
	}

	@Override
	public String getTableName() {
		return "w" + super.getTableName();
	}

	@Override
	public String[] getCalls() {
		return Globals.WORKER_CALLS;
	}

	@Override
	public int getType() {
		return Globals.ID_WORKER;
	}

}
