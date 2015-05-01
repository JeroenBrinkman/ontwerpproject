package model;

import global.Globals;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.lang.String;

/**
 * The abstract superclass for all components. represents a component in the
 * life query system. Components are uniquely identified by their inetaddress.
 * 
 * Every component has a pointer to the model for databaseconnections. And a
 * list of collumns.
 * 
 * @author Jeroen
 *
 */
public abstract class Component {
	/**
	 * The inetaddress of this Component, and at the same time its unique
	 * identifier
	 * 
	 * @invariant adr != null
	 */
	protected InetAddress adr;
	/**
	 * A list with all the collumns a database of this type has
	 * 
	 * @invariant collumnList != null
	 */
	protected String[] collumnList = { "notinitialisedakacomponentincorrect" };
	/**
	 * Pointer to the model for database connections
	 * 
	 * @invariant model != null
	 */
	protected Connection conn; // TODO make connection shared

	/**
	 * Creates a new component, tries to parse an inetadress from the given
	 * string
	 * 
	 * @requires ip is valid && ip != null
	 * @requires mod != null
	 * @ensures model = mod
	 * @ensures adr = ip (parsed)
	 */
	protected PreparedStatement check;
	protected PreparedStatement delete;
	protected PreparedStatement insert;
	protected PreparedStatement getlimit;

	public Component(String ip, Connection con) {
		conn = con;
		try {
			adr = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			System.out.println("invalid ip on component");
		}

		try {
			String sql = "SELECT COUNT(*) FROM "
					+ Globals.getTableName(adr.toString()) + " WHERE tag =  ? ";
			check = conn.prepareStatement(sql);

			sql = "SELECT * FROM " + Globals.getTableName(adr.toString())
					+ " WHERE tag = ? ORDER BY date ASC LIMIT ?";
			getlimit = conn.prepareStatement(sql);

			sql = "DELETE FROM " + Globals.getTableName(adr.toString())
					+ " WHERE tag =  ?  AND date = ?";
			delete = conn.prepareStatement(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Creates a new component using the given ip and model
	 * 
	 * @requires ip != null
	 * @param mod
	 *            != null
	 * @ensures model = mod
	 * @ensures adr = ip
	 */
	public Component(InetAddress ip, Connection con) {
		adr = ip;
		conn = con;
	}

	/**
	 * Getter for the ip
	 * 
	 * @ensure \result = adr
	 * @pure
	 */
	public InetAddress getInet() {
		return adr;
	}

	/**
	 * Abstract method, generates the sql for creating a new table for this
	 * component in String form
	 * 
	 * @ensure result != null && \result is valid
	 * @pure
	 */
	public String createTableSQL() {
		String sql = "CREATE TABLE "
				+ Globals.getTableName(this.adr.toString())
				+ " (date BIGINT(64) not NULL, " + " tag CHAR(1) not NULL , ";
		for (String a : collumnList) {
			sql += a + " INTEGER, ";
		}
		sql += "PRIMARY KEY ( date))";
		return sql;

	}

	/**
	 * Updates the database with a new entry, parsed from the String[]
	 * 
	 * @requires message != null && message.length == collumnList.length +1
	 */
	public void update(String[] message) {
		// TODO make this
		// fix de roundrobin
		try {
			// tags are Seconds -> Minutes -> Hours -> Days -> Weeks -> Other
			// aka S->M->H->D->W->O
			check.setString(1, "S");
			ResultSet v = check.executeQuery();
			if (v.next() && v.getInt(1) == Globals.SQLMAXsec) {
				compressSEntries();
			}

			check.setString(1, "M");
			v = check.executeQuery();
			if (v.getInt(1) == Globals.SQLMAXmin) {
				compressMEntries();
			}

			check.setString(1, "H");
			v = check.executeQuery();
			if (v.getInt(1) == Globals.SQLMAXhour) {
				compressHEntries();
			}

			check.setString(1, "D");
			v = check.executeQuery();
			if (v.getInt(1) == Globals.SQLMAXday) {
				compressDEntries();
			}

			check.setString(1, "W");
			v = check.executeQuery();
			if (v.getInt(1) == Globals.SQLMAXweek) {
				compressWEntries();
			}

			// actual insert
			insert.setString(1, Long.toString(System.currentTimeMillis()));
			insert.setString(2, "S");
			for (int i = 0; i < message.length; ++i) {
				insert.setString(i + 3, message[i]);
			}
			insert.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void compressWEntries() throws SQLException {
		int[] b = new int[collumnList.length];
		delete.setString(1, "W");
		getlimit.setString(1, "W");
		getlimit.setInt(2, 28); //1 month = 4 weeks
		ResultSet r = getlimit.executeQuery();
		long newdate = 0;
		while (r.next()) {
			for (int i = 0; i < b.length; ++i) {
				// start at 3,because date and tag do not have to be
				// averaged and are not relevant
				b[i] += r.getInt(i + 3);
			}
			delete.setString(2, r.getString(1));
			delete.executeUpdate();
			newdate = Long.parseLong(r.getString(1)) - ((int)(14*7 * 24 * 60 * 6000));
		}
		insert.setString(1, Long.toString(newdate));
		insert.setString(2, "O");
		for (int i = 0; i < b.length; ++i) {
			insert.setString(i + 3, Integer.toString(b[i] / 28));
		}
		insert.executeUpdate();
	}

	private void compressDEntries() throws SQLException {
		int[] b = new int[collumnList.length];
		delete.setString(1, "D");
		getlimit.setString(1, "D");
		getlimit.setInt(2, 7);
		ResultSet r = getlimit.executeQuery();
		long newdate = 0;
		while (r.next()) {
			for (int i = 0; i < b.length; ++i) {
				// start at 3,because date and tag do not have to be
				// averaged and are not relevant
				b[i] += r.getInt(i + 3);
			}
			delete.setString(2, r.getString(1));
			delete.executeUpdate();
			newdate = Long.parseLong(r.getString(1)) - ((int)(3.5 * 24 * 60 * 6000));
		}
		insert.setString(1, Long.toString(newdate));
		insert.setString(2, "W");
		for (int i = 0; i < b.length; ++i) {
			insert.setString(i + 3, Integer.toString(b[i] / 7));
		}
		insert.executeUpdate();
	}

	private void compressHEntries() throws SQLException {
		int[] b = new int[collumnList.length];
		delete.setString(1, "H");
		getlimit.setString(1, "H");
		getlimit.setInt(2, 24);
		ResultSet r = getlimit.executeQuery();
		long newdate = 0;
		while (r.next()) {
			for (int i = 0; i < b.length; ++i) {
				// start at 3,because date and tag do not have to be
				// averaged and are not relevant
				b[i] += r.getInt(i + 3);
			}
			delete.setString(2, r.getString(1));
			delete.executeUpdate();
			newdate = Long.parseLong(r.getString(1)) - (12 * 60 * 6000);
		}
		// insert new minute record
		insert.setString(1, Long.toString(newdate));
		insert.setString(2, "H");
		for (int i = 0; i < b.length; ++i) {
			insert.setString(i + 3, Integer.toString(b[i] / 24));
		}
		insert.executeUpdate();
	}

	private void compressMEntries() throws SQLException {
		int[] b = new int[collumnList.length];
		delete.setString(1, "M");
		getlimit.setString(1, "M");
		getlimit.setInt(2, 60);
		ResultSet r = getlimit.executeQuery();
		long newdate = 0;
		while (r.next()) {
			for (int i = 0; i < b.length; ++i) {
				// start at 3,because date and tag do not have to be
				// averaged and are not relevant
				b[i] += r.getInt(i + 3);
			}
			delete.setString(2, r.getString(1));
			delete.executeUpdate();
			newdate = Long.parseLong(r.getString(1)) - (30 * 6000);
		}
		insert.setString(1, Long.toString(newdate));
		insert.setString(2, "H");
		for (int i = 0; i < b.length; ++i) {
			insert.setString(i + 3, Integer.toString(b[i] / 60));
		}
		insert.executeUpdate();
	}

	private void compressSEntries() throws SQLException {
		int a = (60 * 1000) / Globals.POLLINGINTERVAL; 
		int[] b = new int[collumnList.length];
		delete.setString(1, "S");
		getlimit.setString(1, "S");
		getlimit.setInt(2, a);
		ResultSet r = getlimit.executeQuery();
		long newdate = 0;
		while (r.next()) {
			for (int i = 0; i < b.length; ++i) {
				// start at 3,because date and tag do not have to be
				// averaged and are not relevant
				b[i] += r.getInt(i + 3);
			}
			delete.setString(2, r.getString(1));
			delete.executeUpdate();
			newdate = Long.parseLong(r.getString(1)) - 3000;
		}
		insert.setString(1, Long.toString(newdate));
		insert.setString(2, "M");
		for (int i = 0; i < b.length; ++i) {
			insert.setString(i + 3, Integer.toString(b[i] / a));
		}
		insert.executeUpdate();
	}

	/**
	 * Parses the input from the actual component into something that can be
	 * entered into the database
	 * 
	 * @require message != null
	 * @ensure \result != null && result.length == collumnList.length +1
	 */
	protected abstract String[] parseInput(String message);
}
