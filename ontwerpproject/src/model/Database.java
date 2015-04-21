package model;

import java.net.InetAddress;

/**
 * Represents a database in the query system
 * @author Jeroen
 *
 */
public class Database extends Component {

	public Database(String ip, Model mod) {
		super(ip, mod);
	}

	public Database(InetAddress ip, Model mod) {
		super(ip, mod);
	}

	@Override
	protected String[] parseInput(String message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createTableSQL() {
		// TODO add missing collumns
		String sql = "CREATE TABLE " + this.adr.toString()
				+ " (date DATE not NULL, " + " cpu INTEGER, "
				+ " mem INTEGER, " + " disk INTEGER, "
				+ " PRIMARY KEY ( date ))";
		return sql;
	}

}
