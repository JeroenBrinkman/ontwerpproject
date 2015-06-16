package model;

import java.net.InetSocketAddress;
import java.sql.*;
import java.util.ArrayList;

import model.intelligence.Intelligence.ClosedException;
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
		Connection conn = createConnection();
		DatabaseMetaData dbm;

		try {
			Statement st= conn.createStatement();
			dbm = conn.getMetaData();
			ResultSet tables = dbm.getTables(null, null, "notifications", null);
			if (!tables.next()) {
				st.executeUpdate("CREATE TABLE notifications (component VARCHAR(240),  attribute VARCHAR(240), message VARCHAR(240))");
			}
			st.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}


	}

	/**
	 * Removes a component from the list of active components, does not remove
	 * the component from our databases
	 * 
	 * @throws ClosedException
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
	public ArrayList<Component> getComponents() {
		return components;
	}

	/**
	 * Adds a component to the model (list of ative components). If there does
	 * not exist a database table for this component it will be created as well
	 * 
	 * @throws ClosedException
	 *             when something goes wrong
	 *
	 * @requires c != null
	 * @ensures components.contains(c) == true
	 * @ensures database entry for c exists
	 */
	public void addComponent(Component c) throws ClosedException {
		Connection conn = createConnection();
		Statement st = null;
		try {
			DatabaseMetaData dbm = conn.getMetaData();
			ResultSet tables = dbm
					.getTables(null, null, c.getTableName(), null);
			if (!tables.next()) {

				// no table for this address
				st = conn.createStatement();
				String sql = c.createTableSQL();
				st.executeUpdate(sql);
			} else {
				String[] cols = c.getKeys();
				st = conn.createStatement();
				for (int x = 0; x < cols.length; x++) {
					String sql = "SHOW COLUMNS FROM `" + c.getTableName()
							+ "` LIKE \'" + cols[x] + "\'";
					ResultSet col = st.executeQuery(sql);
					if (!col.next()) {
						String end = (x - 1) < 0 ? " FIRST" : " AFTER "
								+ Integer.toString(x - 1);
						sql = "ALTER TABLE " + c.getTableName() + " ADD "
								+ cols[x] + " INTEGER" + end;
						st.executeUpdate(sql);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
				conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		components.add(c);
		c.startUp();
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
			Class.forName(Globals.JDBC_DRIVER);

			conn = DriverManager.getConnection(Globals.DB_URL_DETAIL,
					Globals.USER, Globals.PASS);

		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

}
