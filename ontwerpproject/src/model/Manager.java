package model;

import java.net.InetAddress;

/**
 * Represents a clustermanager in the query system
 * 
 * @author Jeroen
 *
 */
public class Manager extends Component {

	public Manager(String ip, Model mod) {
		super(ip, mod);
	}

	public Manager(InetAddress ip, Model mod) {
		super(ip, mod);
	}

	@Override
	protected String[] parseInput(String message) {
		// TODO Auto-generated method stub
		return null;
	}

}
