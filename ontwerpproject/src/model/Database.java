package model;

import java.net.InetAddress;

/**
 * Represents a database in the query system
 * 
 * @author Jeroen
 *
 */
public class Database extends Component {

	public Database(String ip, Model mod) {
		super(ip, mod);
		String[] temp = {"cpu", "disk", "mem"};
		collumnList = temp;
	}

	public Database(InetAddress ip, Model mod) {
		super(ip, mod);
	}

	@Override
	protected String[] parseInput(String message) {
		// TODO Auto-generated method stub
		return null;
	}

}
