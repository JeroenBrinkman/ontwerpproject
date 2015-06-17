package model;

import global.Globals;

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
		collumnList = Globals.concat(Globals.WORKER_CALLS, Globals.WORKER_COLS);

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
		Globals.log("Constructor for " + getTableName() + " completed");
	}

	@Override
	public long[] parseInput(String message) {
		String[] parts;
		String[] lines = message.split("\n");
		long[] result = new long[lines.length]; // List<String> result = new
												// ArrayList<String>();
		String currentLine;
		for (int i = 0; i < lines.length; i++) {
			currentLine = lines[i];
			if (!currentLine.contains("last")) {
				parts = currentLine.split(":");
				currentLine = parts[1];
				currentLine = currentLine.replaceAll("\\s+", "");
				result[i] = Integer.parseInt(currentLine);
				// result.add(currentLine);
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
}
