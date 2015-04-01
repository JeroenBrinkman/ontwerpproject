package model;

import java.sql.*;

import global.Globals;

public class Model {

	private Component[] components;

	public Model() {
		createConnection();

	}
	
	public void checkDatabase(Connection con) throws SQLException{
		DatabaseMetaData dbm = con.getMetaData();
		for(String a: Globals.componentTypes){
			ResultSet tables = dbm.getTables(null, null, a, null);
			if (tables.next()) {
				// Table exists, check collums
			}
			else {
				//Table doesnt exist, print error and quit
				System.out.println("SQLERROR:Table doesnt exist : " + a);
				System.exit(0);
			}
		}
	}

	public void createConnection() {
		Connection conn = null;
		Statement stmt = null;
		try {
			// STEP 2: Register JDBC driver
			Class.forName(Globals.JDBC_DRIVER);

			// STEP 3: Open a connection
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(Globals.DB_URL_DETAIL, Globals.USER, Globals.PASS);

			// STEP 4: Execute a query
			System.out.println("Creating database...");
			stmt = conn.createStatement();

			String sql = "CREATE DATABASE test";
			stmt.executeUpdate(sql);
			System.out.println("Database created successfully...");
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
			}// nothing we can do
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}// end finally try
		}// end try
	}
	public static void main(String[] args){
		Model model = new Model();
	}

}
