package model;

import global.Globals;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
	protected InetSocketAddress adr;
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
	protected Connection conn;

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

	public Component(String hostname, Connection con) {
		conn = con;
		adr = new InetSocketAddress(hostname, 8000);

		try {
			String sql = "SELECT COUNT(*) FROM "
					+ getTableName() + " WHERE tag =  ? ";
			check = conn.prepareStatement(sql);

			sql = "SELECT * FROM " + getTableName()
					+ " WHERE tag = ? ORDER BY date ASC LIMIT ?";
			getlimit = conn.prepareStatement(sql);

			sql = "DELETE FROM " + getTableName()
					+ " WHERE tag =  ?  AND date = ?";
			delete = conn.prepareStatement(sql);
		} catch (SQLException e) {
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
	public Component(InetSocketAddress addr, Connection con) {
		adr = addr;
		conn = con;
		
		try {
			String sql = "SELECT COUNT(*) FROM "
					+ getTableName() + " WHERE tag =  ? ";
			check = conn.prepareStatement(sql);

			sql = "SELECT * FROM " + getTableName()
					+ " WHERE tag = ? ORDER BY date ASC LIMIT ?";
			getlimit = conn.prepareStatement(sql);

			sql = "DELETE FROM " + getTableName()
					+ " WHERE tag =  ?  AND date = ?";
			delete = conn.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Closes an active connection, if the database is not running this will
	 * generate SQL errors. will also close all statements
	 * 
	 * @requires Database is running && Connection != null
	 * @ensures Connection is closed
	 */
	protected void closeConnection() {
		try {
			check.close();
			insert.close();
			delete.close();
			getlimit.close();
			conn.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	/**
	 * aggregrates the database on this component and closes the connection.
	 * Should be called when a component disconnects from the system. The
	 * database will be readied for a gap in the data, no matter how big the gap
	 * is, this will be achieved by aggregrating the data and converting the
	 * tags to O
	 * 
	 * @requires Database running
	 * @ensures Connection closed && database ready for gap in data
	 */
	public void shutDown() {
		// lazy solution -> delete all S and M tags, convert all H, D and W tags
		// to O
		// TODO less lazy solution?
		try {
			// delete small amounts of data
			Statement s = conn.createStatement();
			String sql = "DELETE FROM " + getTableName()
					+ " WHERE tag = \'S\' OR tag = \'M\'";
			s.executeUpdate(sql);
			sql = "SELECT COUNT(*) FROM " + getTableName();
			ResultSet r = s.executeQuery(sql);
			r.next();
			if (r.getInt(1) == 0) {
				//droptable if its now empty (no use keeping an empty table)
				sql = "DROP TABLE " + getTableName();
				s.executeUpdate(sql);
			} else {
				sql = "UPDATE "
						+ getTableName()
						+ " SET tag = \'O\' WHERE tag = \'H\' OR tag = \'D\' OR tag=\'W\'";
				s.executeUpdate(sql);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		closeConnection();
	}

	/**
	 * Getter for the ip
	 * 
	 * @ensure \result = adr
	 * @pure
	 */
	public InetSocketAddress getAddress() {
		return adr;
	}
	
	public abstract String getTableName();
	
	/**
	 * getter for columnlist
	 */
	public String[] getKeys() {
		return this.collumnList;
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
				+ getTableName()
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
		try {
			// tags are Seconds -> Minutes -> Hours -> Days -> Weeks -> Other
			// aka S->M->H->D->W->O
			// nested, because only possibilty is when the previous was
			// converted
			check.setString(1, "S");
			ResultSet v = check.executeQuery();
			if (v.next() && v.getInt(1) == Globals.SQLMAXsec) {
				compressSEntries();
				check.setString(1, "M");
				v = check.executeQuery();
				if (v.next() && v.getInt(1) == Globals.SQLMAXmin) {
					compressMEntries();
					check.setString(1, "H");
					v = check.executeQuery();
					if (v.next() && v.getInt(1) == Globals.SQLMAXhour) {
						compressHEntries();
						check.setString(1, "D");
						v = check.executeQuery();
						if (v.next() && v.getInt(1) == Globals.SQLMAXday) {
							compressDEntries();
							check.setString(1, "W");
							v = check.executeQuery();
							if (v.next() && v.getInt(1) == Globals.SQLMAXweek) {
								compressWEntries();
							}
						}
					}
				}
			}

			// actual insert
			// TODO choose between system time and component time
			insert.setString(1, Long.toString(System.currentTimeMillis()));
			insert.setString(2, "S");
			for (int i = 0; i < message.length; ++i) {
				insert.setString(i + 3, message[i]);
			}
			insert.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// helpfunction for update
	private void compressWEntries() throws SQLException {
		int[] b = new int[collumnList.length];
		delete.setString(1, "W");
		getlimit.setString(1, "W");
		getlimit.setInt(2, 28); // 1 month = 4 weeks
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
			newdate = Long.parseLong(r.getString(1))
					- ((int) (14 * 7 * 24 * 60 * 6000));
		}
		insert.setString(1, Long.toString(newdate));
		insert.setString(2, "O");
		for (int i = 0; i < b.length; ++i) {
			insert.setString(i + 3, Integer.toString(b[i] / 28));
		}
		insert.executeUpdate();
	}

	// helpfunction for update
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
			newdate = Long.parseLong(r.getString(1))
					- ((int) (3.5 * 24 * 60 * 6000));
		}
		insert.setString(1, Long.toString(newdate));
		insert.setString(2, "W");
		for (int i = 0; i < b.length; ++i) {
			insert.setString(i + 3, Integer.toString(b[i] / 7));
		}
		insert.executeUpdate();
	}

	// help function for update
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

	// help function for update
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

	// help function for update
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
	 * @ensure \result != null && result.length == collumnList.length
	 */
	protected abstract String[] parseInput(String message);
}
