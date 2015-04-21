package model;

import java.net.InetAddress;
import java.net.UnknownHostException;
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
	// TODO fill this list
	protected String[] collumnList;
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
		String sql = "CREATE TABLE " + this.adr.toString()
				+ " (date DATE not NULL, ";
		for (String a : collumnList) {
			sql += a + " INTEGER, ";
		}

		sql += " PRIMARY KEY ( date ))";
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

	}

	/**
	 * Updates the database with a new entry, parsed from the String[]
	 * 
	 * @requires message != null && message.length == collumnList.length
	 */
	public void update(String[] message) {
		// TODO make this, maybe abstract?
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
