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
	protected String[] collumnList = {"notinitialisedakacomponentincorrect"};
	/**
	 * Pointer to the model for database connections
	 * 
	 * @invariant model != null
	 */
	protected Model model;

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
	public String createTableSQL(){
		String sql = "CREATE TABLE " + Globals.getTableName(this.adr.toString())
				+ " (date BIGINT(64) not NULL, "
				+ " tag CHAR(1) not NULL , ";
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
		long delb4  = System.currentTimeMillis() - Globals.MYSQLMAXTIME;
		try {
			st = conn.createStatement();
			String sql = "DELETE FROM " + Globals.getTableName(adr.toString()) + " WHERE date < " + delb4;
			st.executeUpdate(sql);
		} catch (SQLException e){
			e.printStackTrace();
		}finally{
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
		//fix de roundrobin
		try {
			PreparedStatement st1;
			String sql = "SELECT AMOUNT(*) FROM "
					+ Globals.getTableName(adr.toString())
					+ " WHERE tag = ?";
			st1 = conn.prepareStatement(sql);
			//tags are Seconds -> Minutes -> Hours -> Days -> Weeks -> Other
			// aka S->M->H->D->W->O
			st1.setString(1, "S");
			ResultSet v = st1.executeQuery();
			if(v.getInt(0)==12){
				//TODO compress shit
			}else{
				//TODO make compress for other tags aswell
			}
			Statement st2 = conn.createStatement();
			sql = "INSERT INTO " + Globals.getTableName(adr.toString()) 
					+ " VALUES( " + System.currentTimeMillis() + ", "
					+ " S";
			for(int i=0; i<message.length;++i){
				sql += ", " + message[i] ;
			}
			sql+= ")";
			st2.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
