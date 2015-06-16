package model;

import global.Globals;

import java.net.InetSocketAddress;
import java.sql.Connection;

import model.intelligence.DatabaseIntelligence;
import model.intelligence.Intelligence.ClosedException;

/**
 * Represents a database in the query system
 * 
 * @author Jeroen
 *
 */
public class Database extends Component {

	public Database(InetSocketAddress addr, Connection con, Model mod) throws ClosedException {
		super(addr, con);
		intel = new DatabaseIntelligence(this, mod, con);
		//TODO temp currentlyplaceholder
		String[] temp = {"cpu", "hdd", "mem"};
		collumnList = temp;
	}

	@Override
	public String[] parseInput(String message) {
		// TODO make parser
		return null;
	}

	@Override
	public String getTableName() {
		return "d" + super.getTableName();
	}
	
	@Override
	public String[] getCalls() {
		return Globals.DatabaseCalls;
	}

}
