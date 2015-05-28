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
	}

	/**
	 * Removes a component from the list of active components, does not remove
	 * the component from our databases
	 * @throws ClosedException 
	 * 
	 * @requires c != null
	 * @ensures components.contains(c) = false;
	 */
	public void removeComponent(Component c) throws ClosedException {
		c.shutDown();
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
	 * @throws ClosedException when something goes wrong
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
			ResultSet tables = dbm.getTables(null, null,
					c.getTableName(), null);
			if (!tables.next()) {
				// no table for this address
				st = conn.createStatement();
				String sql = c.createTableSQL();
				st.executeUpdate(sql);
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

	// TODO remove test main after im done with testing
	public static void main(String[] args) {
		Model model = new Model();
		Worker w = new Worker(new InetSocketAddress("192.192.192.192", 123), model.createConnection(), model);
		try {
			model.addComponent(w);
		} catch (ClosedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		long start = System.currentTimeMillis();
		int i = 0;
		String[] message = { "15", "8", "2", "1" };
		while (System.currentTimeMillis()-start < (1000*60*60*5)) {//run 5 uur
			if (i%250 ==0 && i>0){
				System.out.print(i + ":");
				System.out.println("average time per update is : " +((System.currentTimeMillis()-start)/i) + " millisecs");
							}
			try {
				w.update(System.currentTimeMillis(), message);
			} catch (ClosedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
			
		}
		System.out.println("endtime inserts: " + (System.currentTimeMillis()-start));
		try {
			model.removeComponent(w);
		} catch (ClosedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
