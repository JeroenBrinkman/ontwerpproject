package model;

import global.Globals;
import java.net.InetSocketAddress;
import java.sql.Connection;

import model.intelligence.Intelligence.ClosedException;
import model.intelligence.ManagerIntelligence;

/**
 * Represents a clustermanager in the query system
 * 
 * @author Jeroen
 *
 */
public class Manager extends Component {

	public Manager(InetSocketAddress addr, Connection con, Model mod)
			throws ClosedException {
		super(addr, con);
		intel = new ManagerIntelligence(this, mod, con);
		collumnList = Globals.concat(Globals.MANAGER_CALLS,
				Globals.MANAGER_COLS);
	}

	@Override
	public long[] parseInput(String message) {
		String[] parts;
		String[] lines = message.split("\n");
		long[] result = new long[Globals.MANAGER_COLS.length];
		//String[] result = new String[collumnList.length];
		String currentLine;
		for (int i = 0; i < Globals.MANAGER_COLS.length; i++) {
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

}
