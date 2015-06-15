package model;


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

	public Manager(InetSocketAddress addr, Connection con, Model mod) throws ClosedException {
		super(addr, con);
		intel = new ManagerIntelligence(this, mod, con);
		//TODO temp currentlyplaceholder
		String[] temp = {"time", "cpu", "hdd", "mem"};
		collumnList = temp;
	}

	@Override
	protected String[] parseInput(String message) {
		// TODO make parser
		return null;
	}

	@Override
	public String getTableName() {
		return "m" + super.getTableName();
	}

}
