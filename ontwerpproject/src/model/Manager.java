package model;

import java.net.InetAddress;

public class Manager extends Component {

	public Manager(String ip, int id, Model mod) {
		super(ip, id, mod);
	}
	
	public Manager(InetAddress ip, int id, Model mod) {
		super(ip, id, mod);
	}

	@Override
	public void update(String message) {
		// TODO Auto-generated method stub

	}

	@Override
	protected DatabaseEntry[] parseInput(String message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createTableSQL() {
		//TODO add missing collumns
		String sql ="CREATE TABLE " + this.adr.toString() +
                " (date DATE not NULL, " +
                " cpu INTEGER, " + 
                " mem INTEGER, " + 
                " disk INTEGER, " + 
                " PRIMARY KEY ( date ))";
		return sql;
	}

}
