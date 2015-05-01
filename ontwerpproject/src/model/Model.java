package model;

import java.sql.*;
import java.util.ArrayList;

import global.Globals;

/**
 * The model class represents the actual query system, it has an entry in its
 * component list for every existing component in the life system. Components
 * can be added and removed at any time. every component has an database entry,
 * one such entry can not be removed, but will eventually be removed after
 * enough time has passed. (automatic database compression, called from the
 * scheduler). This class also manages connections to the mysql database.
 * 
 * @author Jeroen
 *
 */
public class Model {

	/**
	 * The actual model, all currently active components in the real system are
	 * in this list
	 * 
	 * @invariant components != null;
	 */
	private ArrayList<Component> components;

	/**
	 * Constructor for a new model. initializes the components list
	 */
	public Model() {
		components = new ArrayList<Component>();
	}

	/**
	 * Removes a component from the list of active components, does not remove
	 * the component from our databases
	 * 
	 * @requires c != null
	 * @ensures components.contains(c) = false;
	 */
	public void removeComponent(Component c) {
		components.remove(c);
	}

	/**
	 * Getter for the component list
	 * 
	 * @ensures \result != null && \result == components
	 * @pure
	 */
	public Component[] getComponents() {
		return (Component[]) components.toArray();
	}

	/**
	 * Adds a component to the model (list of ative components). If there does
	 * not exist a database table for this component it will be created as well
	 *
	 * @requires c != null
	 * @ensures components.contains(c) == true
	 * @ensures database entry for c exists
	 */
	public void addComponent(Component c) {
		Connection conn = createConnection();
		Statement st = null;
		try {
			DatabaseMetaData dbm = conn.getMetaData();
			ResultSet tables = dbm.getTables(null, null,
					Globals.getTableName(c.getInet().toString()), null);
			if (!tables.next()) {
				// no table for this address
				st = conn.createStatement();
				String sql = c.createTableSQL();
				st.executeUpdate(sql);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection(conn);
			try {
				if (st != null)
					st.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}

		components.add(c);
	}

	/**
	 * Creates a connection to the mysql database en returns this connection
	 * object If the database is not running this will generate a lot of errors
	 * 
	 * @ensures \result != null if data is running
	 * @requires Database running
	 */
	public Connection createConnection() {
		Connection conn = null;
		try {
			// STEP 2: Register JDBC driver
			Class.forName(Globals.JDBC_DRIVER);

			// STEP 3: Open a connection
			conn = DriverManager.getConnection(Globals.DB_URL_DETAIL,
					Globals.USER, Globals.PASS);

		} catch (SQLException se) {
			// TODO Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// TODO Handle errors for Class.forName
			e.printStackTrace();
		}
		return conn;
	}

	/**
	 * Closes an active connection, if the database is not running this will
	 * generate SQL errors
	 * 
	 * @requires Database is running
	 * @ensures Connection is closed
	 */
	public void closeConnection(Connection conn) {
		try {
			if (conn != null)
				conn.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	// TODO remove test main after im done with testing
	public static void main(String[] args) {
		Model model = new Model();
		Worker w = new Worker("192.192.192.192", model.createConnection());
		model.addComponent(w);
		long start = System.currentTimeMillis();
		int i = 0;
		while (System.currentTimeMillis() - start < 60000) {
			String[] message = { "15", "8", "2" };
			w.update(message);
			i++;
		}
		System.out.println(i);
	}

}
