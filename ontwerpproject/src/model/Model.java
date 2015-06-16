package model;

import java.net.InetSocketAddress;
import java.sql.*;
import java.util.ArrayList;

import view.GUI;
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

	private GUI gui;

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
	 * @throws ClosedException
	 * 
	 * @requires c != null
	 * @ensures components.contains(c) = false;
	 */
	public void removeComponent(Component c) {
		components.remove(c);
		if (gui != null) {
			gui.updateCompList(components);
		}
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
				for (int x =0; x< cols.length;x++) {
					String sql = "SHOW COLUMNS FROM `" +c.getTableName()+ "` LIKE \'" +  cols[x] +"\'";
					ResultSet col = st.executeQuery(sql);
					if(!col.next()){
						String end = (x-1)<0?" FIRST":" AFTER "+ Integer.toString(x-1);
						sql = "ALTER TABLE " + c.getTableName() + " ADD " + cols[x] + " INTEGER" + end;
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
		if (gui != null) {
			gui.updateCompList(components);
			Globals.LAST_COMPONENT = System.currentTimeMillis();
		}
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

	public void addGUIObserver(GUI gui) {
		this.gui = gui;
	}

	// TODO remove test main after im done with testing
	public static void main(String[] args) {
		try {
			Model model = new Model();
			long start = System.currentTimeMillis();
			Worker w = new Worker(
					new InetSocketAddress("192.192.192.192", 123),
					model.createConnection(), model);
			model.addComponent(w);
			int i = 0;
			int[] message = new int[w.getKeys().length];
			for (int j = 0; j < message.length; ++j) {
				message[j] = j;
			}
			while (/* System.currentTimeMillis() - start < (1000 * 60 * 60 * 5) */i < 5) {

				if (i % 500 == 0) {
					int mb = 1024;

					// Getting the runtime reference from system
					Runtime runtime = Runtime.getRuntime();

					System.out
							.print("##### Heap utilization statistics [KB] #####");

					// Print used memory
					System.out.print("\t Used Memory: \t"
							+ (runtime.totalMemory() - runtime.freeMemory())
							/ mb);

					// Print free memory
					System.out.print("\t Free Memory: \t"
							+ runtime.freeMemory() / mb);

					// Print total available memory
					System.out.print("\t Total Memory: \t"
							+ runtime.totalMemory() / mb);

					// Print Maximum available memory
					System.out.println("\t Max Memory: \t"
							+ runtime.maxMemory() / mb);

				}

				w.update(System.currentTimeMillis(), message);
				i++;
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (ClosedException e) {
			e.printStackTrace();
		}

	}

}
