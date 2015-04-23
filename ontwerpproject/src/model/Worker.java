package model;

import java.net.InetAddress;

/**
 * Represents a worker in the query system
 * @author Jeroen
 *
 */
public class Worker extends Component {

	public Worker(String ip, Model mod) {
		super(ip, mod);
		String[] temp = {"cpu", "disk", "mem"};
		collumnList = temp;
	}

	public Worker(InetAddress ip, Model mod) {
		super(ip, mod);
	}

	@Override
	protected String[] parseInput(String message) {
		// TODO Auto-generated method stub
		return null;
	}
}
