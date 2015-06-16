package model;

import global.Globals;

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

	protected PreparedStatement check;
	protected PreparedStatement delete;
	protected PreparedStatement insert;
	protected PreparedStatement getlimit;

	/**
	 * The intelligence of this component, should never be null
	 */
	protected Intelligence intel;

	public Component(InetSocketAddress addr, Connection con) throws ClosedException{
		adr = addr;
		this.adr.getHostName();
		conn = con;

		System.out
				.println("Constructor called of component: " + getTableName());

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
			//intel.databaseError(e);
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
		} catch (SQLException e) {
			/*try {
				//intel.databaseError(e);
			} catch (ClosedException e1) {
				// hoeft niks
			}*/
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
	public void shutDown() throws ClosedException{
		try {
			Statement s = conn.createStatement();
			String sql;
			sql = "SELECT COUNT(*) FROM " + getTableName();
			ResultSet r = s.executeQuery(sql);
			r.next();
			// commit entry with only 0 to mark the shutdown point
			int[] x = new int[collumnList.length];
			for(int i =0; i< x.length;++i){
				x[i]=0;
			}
			update(System.currentTimeMillis(), x);
			conn.commit();
		} catch (SQLException e) {
			//intel.databaseError(e);
		}

		closeConnection();
	}
	
	/**
	 * Add 0 entries from the last entry in the database until now, to mark the period the component was offline
	 * @requires Database running
	 * @ensures Database back up to date and ready for use
	 */
	protected void startUp() throws ClosedException{
		//check if there are old entries in the database
		String sql = "SELECT COUNT(*) FROM " + getTableName();
		try {
			Statement s = conn.createStatement();
			ResultSet v = s.executeQuery(sql);
			v.next();
			if(v.getInt(1)>0){
				v.close();
				s.close();
				// enter new entries until now
				// first get the startpoint
				getlimit.setString(1, "M");
				getlimit.setInt(2, 1);
				v = getlimit.executeQuery();
				v.next();
				long current = v.getLong(1) + Globals.POLLINGINTERVAL;
				long end = System.currentTimeMillis();
				int[] str = new int[collumnList.length];
				for(int i =0; i< str.length; ++i){
					str[i] = 0;
				}
				//insert every polling interval a 0 entry
				while(current < end){
					update(current, str);
					current += Globals.POLLINGINTERVAL;
				}
				conn.commit();
			}
		} catch (SQLException e) {
			//intel.databaseError(e);
		}
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
	
	public Intelligence getIntelligence() {
		return intel;
	}

	public String getTableName() {
		String result = adr.toString();
		result = result.replaceAll(":|/|\\.", "_");
		return result.replaceAll("-", "_");
	}

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
		String sql = "CREATE TABLE " + getTableName()
				+ " (date BIGINT(64) not NULL, " + " tag CHAR(1) not NULL , ";
		for (String a : collumnList) {
			sql += a + " INTEGER, ";
		}
		sql += "PRIMARY KEY ( date), INDEX (tag))";
		return sql;
	}

	/**
	 * Updates the database with a new entry, parsed from the String[]
	 * 
	 * @requires message != null && message.length == collumnList.length +1
	 * @ensures data is correctly inserted
	 */
	public void update(Long date, int[] message) throws ClosedException{
		//TODO reenable check critical when everything works
		//intel.checkCritical(message);
		try {
			// tags are Minutes -> Hours -> Days
			// aka M->H->D
			// nested, because only possibility is when the previous was
			// converted
			ResultSet v;
			check.setString(1, "M");
			v = check.executeQuery();
			if (v.next() && v.getInt(1) == Globals.SQLMAXmin) {
				compressMEntries();
				check.setString(1, "H");
				v = check.executeQuery();
				if (v.next() && v.getInt(1) == Globals.SQLMAXhour) {
					compressHEntries();
				}
			}

			// actual insert
			insert.setString(1, Long.toString(date));
			insert.setString(2, "M");
			for (int i = 0; i < message.length; ++i) {
				insert.setInt(i + 3, message[i]);
			}
			insert.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			//intel.databaseError(e);
		}
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
		conn.commit();
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
		conn.commit();
	}
	/*
	// help function for update currently unused, but might be useful to keep
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
		conn.commit();
	}*/

	/**
	 * Parses the input from the actual component into something that can be
	 * entered into the database
	 * 
	 * @require message != null
	 * @ensure \result != null && result.length == collumnList.length
	 */
	protected abstract String[] parseInput(String message);
}
