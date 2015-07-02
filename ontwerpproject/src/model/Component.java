package model;

import global.Globals;
import global.Logger;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.lang.String;

import model.intelligence.Intelligence;
import model.intelligence.Intelligence.ClosedException;

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

	// prepared sql statements never null
	protected PreparedStatement check;
	protected PreparedStatement delete;
	protected PreparedStatement insert;
	protected PreparedStatement getlimit;

	/**
	 * The intelligence of this component, should never be null
	 * 
	 * @invariant != null
	 */
	protected Intelligence intel;

	/**
	 * Constructor for the component, initializes all but one of the prepared
	 * statements, the insert statement should be implemented in the subclass,
	 * because this is different for every component
	 * 
	 * @requires addr != null && is valid
	 * @requires con != null
	 * @throws ClosedException
	 *             when the database connection fails, this will automaticly
	 *             remove the component from the model
	 */
	public Component(InetSocketAddress addr, Connection con)
			throws ClosedException {
		adr = addr;
		this.adr.getHostName();
		conn = con;

		try {
			conn.setAutoCommit(false);

			String sql = "SELECT COUNT(*) FROM " + getTableName()
					+ " WHERE tag =  ? ";
			check = conn.prepareStatement(sql);

			sql = "SELECT * FROM " + getTableName()
					+ " WHERE tag = ? ORDER BY date ASC LIMIT ?";
			getlimit = conn.prepareStatement(sql);

			sql = "DELETE FROM " + getTableName()
					+ " WHERE tag =  ?  AND date = ?";
			delete = conn.prepareStatement(sql);
		} catch (SQLException e) {
			intel.databaseError(e);
		}
	}

	/**
	 * Closes an active connection, will also close all statements.
	 * 
	 * 
	 * @ensures Connection is closed
	 */
	protected void closeConnection() {
		try {
			check.close();
			insert.close();
			delete.close();
			getlimit.close();
			conn.close();
		} catch (SQLException e) {
			try {
				intel.databaseError(e);
			} catch (ClosedException e1) {
				// hoeft niks
			}
		}
	}

	/**
	 * Add 0 entries from the last entry in the database until now, to mark the
	 * period the component was offline. Execution of this method might take a
	 * while depending on how long the component was offline, it is recommended
	 * to execute this in a different thread
	 * 
	 * @requires Database running
	 * @ensures Database back up to date and ready for use
	 */
	protected void startUp() throws ClosedException {
		// check if there are old entries in the database
		long startTime = System.currentTimeMillis();
		Logger.log_debug("Wachten op jeroen!");

		String sql = "SELECT COUNT(*) FROM " + getTableName();
		try {
			Statement s = conn.createStatement();
			ResultSet v = s.executeQuery(sql);
			v.next();
			if (v.getLong(1) > 0) {
				v.close();
				// enter new entries until now
				// first get the startpoint
				sql = "SELECT date FROM " + getTableName()
						+ " WHERE tag = \'M\' ORDER BY date DESC LIMIT 1";
				v = s.executeQuery(sql);
				v.next();
				long current = v.getLong(1) + Globals.POLLINGINTERVAL;
				long end = System.currentTimeMillis();
				long[] str = new long[collumnList.length];
				for (int i = 0; i < str.length; ++i) {
					str[i] = 0;
				}
				// insert every polling interval a 0 entry
				while (current < end) {
					update(current, str);
					current += Globals.POLLINGINTERVAL;
				}
				// conn.commit();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			intel.databaseError(e);
		}

		Logger.log_debug("Jeroen klaar gekomen in " + (System.currentTimeMillis() - startTime) + "!");
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

	/**
	 * Getter for the intelligence
	 * 
	 * @ensure \result = intel
	 * @pure
	 */
	public Intelligence getIntelligence() {
		return intel;
	}

	/**
	 * Getter for the tablename, the table name is derived from the ip
	 * 
	 * @ensure \result = tablename
	 * @pure
	 */
	public String getTableName() {
		String result = adr.toString();
		result = result.replaceAll(":|/|\\.", "_");
		return result.replaceAll("-", "_");
	}

	/**
	 * Getter for the names of the collums that are in the table of this
	 * component
	 * 
	 * @ensure \result = collumList
	 * @pure
	 */
	public String[] getKeys() {
		return this.collumnList;
	}
	
	/**
	 * 
	 * @return the connection
	 */ 
	public Connection getConnection() {
		return this.conn;
	}

	/**
	 * Getter for calls
	 * 
	 * @pure
	 */
	public abstract String[] getCalls();

	/**
	 * Abstract method, generates the sql for creating a new table for this
	 * component in String form
	 * 
	 * @ensure result != null && \result is valid
	 * @pure
	 */
	public String createTableSQL() {
		String sql = "CREATE TABLE " + getTableName()
				+ " (date BIGINT(64) not NULL, " + " tag CHAR(1) not NULL , ";
		for (String a : collumnList) {
			sql += a + " BIGINT(64), ";
		}
		sql += "PRIMARY KEY ( date), INDEX (tag))";
		return sql;
	}

	/**
	 * Updates the database with a new entry, parsed from the long[]
	 * 
	 * @requires message != null && message.length == collumnList.length
	 * @ensures data is correctly inserted and aggregation is applied
	 */
	public void update(Long date, long[] message) throws ClosedException {
		intel.checkCritical(message);
		try {
			// tags are Minutes -> Hours -> Days
			// aka M->H->D
			// nested, because only possibility is when the previous was
			// converted
			ResultSet v;
			check.setString(1, "M");
			v = check.executeQuery();
			if (v.next() && v.getLong(1) == Globals.SQLMAXmin) {
				compressMEntries();
				check.setString(1, "H");
				v = check.executeQuery();
				if (v.next() && v.getLong(1) == Globals.SQLMAXhour) {
					compressHEntries();
				}
			}

			// actual insert
			insert.setString(1, Long.toString(date));
			insert.setString(2, "M");
			for (int i = 0; i < message.length; ++i) {
				insert.setLong(i + 3, message[i]);
			}
			insert.executeUpdate();
			conn.commit();
			Globals.newUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			intel.databaseError(e);
		}
	}

	// help function for update
	private void compressHEntries() throws SQLException {
		long[] b = new long[collumnList.length];
		delete.setString(1, "H");
		getlimit.setString(1, "H");
		getlimit.setInt(2, 24);
		ResultSet r = getlimit.executeQuery();
		long newdate = 0;
		while (r.next()) {
			for (int i = 0; i < b.length; ++i) {
				// start at 3,because date and tag do not have to be
				// averaged and are not relevant
				b[i] += r.getLong(i + 3);
			}
			delete.setString(2, r.getString(1));
			delete.executeUpdate();
			newdate = Long.parseLong(r.getString(1)) - (12 * 60 * 6000);
		}
		// insert new minute record
		insert.setString(1, Long.toString(newdate));
		insert.setString(2, "H");
		for (int i = 0; i < b.length; ++i) {
			insert.setString(i + 3, Long.toString(b[i] / 24));
		}
		insert.executeUpdate();
		conn.commit();
	}

	// help function for update
	private void compressMEntries() throws SQLException {
		long[] b = new long[collumnList.length];
		delete.setString(1, "M");
		getlimit.setString(1, "M");
		getlimit.setInt(2, 60);
		ResultSet r = getlimit.executeQuery();
		long newdate = 0;
		while (r.next()) {
			for (int i = 0; i < b.length; ++i) {
				// start at 3,because date and tag do not have to be
				// averaged and are not relevant
				b[i] += r.getLong(i + 3);
			}
			delete.setString(2, r.getString(1));
			delete.executeUpdate();
			newdate = Long.parseLong(r.getString(1)) - (30 * 6000);
		}
		insert.setString(1, Long.toString(newdate));
		insert.setString(2, "H");
		for (int i = 0; i < b.length; ++i) {
			insert.setString(i + 3, Long.toString(b[i] / 60));
		}
		insert.executeUpdate();
		conn.commit();
	}

	/**
	 * Parses the input from the actual component into something that can be
	 * entered into the database
	 * 
	 * @require message != null
	 * @ensure \result != null
	 */
	public abstract long[] parseInput(String message);
	
	/**
	 * Returns the type of the component as defined in Globals
	 */
	public abstract int getType();
}
