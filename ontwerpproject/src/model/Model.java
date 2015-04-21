package model;

import java.sql.*;
import java.util.ArrayList;

import global.Globals;

public class Model {

	private ArrayList<Component> components;

	public Model() {
		createConnection();

	}

	public void removeComponent(Component c){
		components.remove(c);
	}
	
	public Component[] getComponents(){
		return (Component[]) components.toArray();
	}
	
	public void addComponent(Component c) {
		Connection conn = createConnection();
		Statement st = null;
		try {
			DatabaseMetaData dbm = conn.getMetaData();
			ResultSet tables = dbm.getTables(null, null,
					c.getInet().toString(), null);
			if (!tables.next()) {
				// no table for this address
				st = conn.createStatement();
				String sql = c.createTableSQL();
				st.executeUpdate(sql);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			closeConnection(conn);
			try {
				if (st != null)
					st.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		
		components.add(c);
		// TODO RRD implementation
	}

	public void checkDatabase(Connection con) throws SQLException {
		DatabaseMetaData dbm = con.getMetaData();
		for (String a : Globals.componentTypes) {
			ResultSet tables = dbm.getTables(null, null, a, null);
			if (tables.next()) {
				// Table exists, check collums
			} else {
				// Table doesnt exist, print error and quit
				System.out.println("SQLERROR:Table doesnt exist : " + a);
				System.exit(0);
			}
		}
	}

	public Connection createConnection() {
		Connection conn = null;
		try {
			// STEP 2: Register JDBC driver
			Class.forName(Globals.JDBC_DRIVER);

			// STEP 3: Open a connection
			conn = DriverManager.getConnection(Globals.DB_URL_DETAIL,
					Globals.USER, Globals.PASS);

		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		}
		return conn;
	}

	public void closeConnection(Connection conn) {
		try {
			if (conn != null)
				conn.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	public static void main(String[] args) {
		//Model model = new Model();
	}

}
