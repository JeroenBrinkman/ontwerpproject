package model;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.SQLException;

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
