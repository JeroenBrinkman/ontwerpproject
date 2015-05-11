package model;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Represents a database in the query system
 * 
 * @author Jeroen
 *
 */
public class Database extends Component {

	public Database(String ip, Connection con) {
		super(ip, con);
		//TODO tepm currentlyplaceholder
		String[] temp = {"cpu", "hdd", "mem"};
		collumnList = temp;
		String sql = "INSERT INTO " + getTableName()
				+ " VALUES( ?,  ?";
		for (int i = 0; i < collumnList.length; ++i) {
			sql += ",  ?";
		}
		sql += ")";
		try {
			insert = conn.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Database(InetSocketAddress addr, Connection con) {
		super(addr, con);
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
		String out = "d" + adr.getHostString();
		out = out.replace(".", "");
		out = out.replace("/", "");
		out = out.replace(":", "p");
		return out;
	}

}
