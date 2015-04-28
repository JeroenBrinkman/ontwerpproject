package model;

import global.Globals;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.lang.String;
import java.sql.Statement;

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
	protected Model model; //TODO make connection shared

	/**
	 * Creates a new component, tries to parse an inetadress from the given
	 * string
	 * 
	 * @requires ip is valid && ip != null
	 * @requires mod != null
	 * @ensures model = mod
	 * @ensures adr = ip (parsed)
	 */
	public Component(String ip, Model mod) {
		model = mod;
		try {
			adr = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			System.out.println("invalid ip on component");
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
	public Component(InetAddress ip, Model mod) {
		adr = ip;
		model = mod;
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
	 * Compresses the database by removing everything before a certain date.
	 * Expensive operation, should not be called to often. (iterates over every
	 * entry in the database)
	 * 
	 * @requires Database running
	 * @ensures \oldDatabase.size >= \newDatabase.size
	 */
	public void compressSQLDatabase() {
		Connection conn = model.createConnection();
		Statement st = null;
		long delb4 = System.currentTimeMillis() - Globals.MYSQLMAXTIME;
		try {
			st = conn.createStatement();
			String sql = "DELETE FROM " + Globals.getTableName(adr.toString())
					+ " WHERE date < " + delb4;
			st.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			model.closeConnection(conn);
		}
	}

	/**
	 * Updates the database with a new entry, parsed from the String[]
	 * 
	 * @requires message != null && message.length == collumnList.length +1
	 */
	public void update(String[] message) {
		// TODO make this
		Connection conn = model.createConnection();
		// fix de roundrobin
		try {
			
			PreparedStatement st1;
			String sql = "SELECT COUNT(*) FROM "
					+ Globals.getTableName(adr.toString())
					+ " WHERE tag =  ? ";
			st1 = conn.prepareStatement(sql);
			// tags are Seconds -> Minutes -> Hours -> Days -> Weeks -> Other
			// aka S->M->H->D->W->O
						st1.setString(1, "S");
			ResultSet v = st1.executeQuery();
			Statement st3 = null;
			PreparedStatement st4 = null;
			sql = "DELETE FROM " + Globals.getTableName(adr.toString())
					+ " WHERE tag =  ?  AND date = ?";
			st4 = conn.prepareStatement(sql);
			//do minutes
			if (v.next() && v.getInt(1) == Globals.SQLMAXsec) {
				int a = (60 * 1000) / Globals.POLLINGINTERVAL; // amount of
																// entries per
																// minute
				int[] b = new int[collumnList.length];
				sql = "SELECT * FROM " + Globals.getTableName(adr.toString())
						+ " WHERE tag = \'S\' ORDER BY date ASC LIMIT " + a;
				st3 = conn.createStatement();
				st4.setString(1, "S");
				
				ResultSet r = st3.executeQuery(sql);
				long newdate = 0;
				while (r.next()) {
					for (int i = 0; i < b.length; ++i) {
						// start at 3,because date and tag do not have to be
						// averaged and are not relevant
						b[i] += r.getInt(i+3);
					}
					st4.setString(2, r.getString(1));
					st4.executeUpdate();
					newdate = Long.parseLong(r.getString(1)) -3000;
				}
				// insert new minute record
				sql = "INSERT INTO " + Globals.getTableName(adr.toString()) + " VALUES ( " + newdate + ", 'M' ";
				
				for(int i = 0; i<b.length; ++i){
					sql += ", " + (b[i]/a);
				}
				sql += ")";
				st3.executeUpdate(sql);
			}
			st1.setString(1, "M");
			v = st1.executeQuery();
			/**if (v.getInt(1) == Globals.SQLMAXmin) {
				// TODO compress shit
			}
			st1.setString(1, "H");
			v = st1.executeQuery();
			if (v.getInt(1) == Globals.SQLMAXhour) {
				// TODO compress shit
			}
			st1.setString(1, "D");
			v = st1.executeQuery();
			if (v.getInt(1) == Globals.SQLMAXday) {
				// TODO compress shit
			}
			st1.setString(1, "W");
			v = st1.executeQuery();
			if (v.getInt(1) == Globals.SQLMAXweek) {
				// TODO compress shit
			}**/

			// actual insert
			Statement st2 = conn.createStatement();
			sql = "INSERT INTO " + Globals.getTableName(adr.toString())
					+ " VALUES( " + System.currentTimeMillis() + ", " + " 'S'"; // TODO
																				// choose
																				// between
																				// system
																				// time
																				// or
																				// worker
																				// time
			for (int i = 0; i < message.length; ++i) {
				sql += ", " + message[i];
			}
			sql += ")";
			st2.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			model.closeConnection(conn);
		}

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
