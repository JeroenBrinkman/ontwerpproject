package model.intelligence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Component;
import model.Model;

public class DatabaseIntelligence extends Intelligence {

	private PreparedStatement st;
	public DatabaseIntelligence(Component comp, Model mod, Connection conn) throws ClosedException {
		super(comp, mod, conn);
		LIMITS = new int[comp.getKeys().length];
		String sql = "SELECT value FROM databasevalues WHERE name =  ? ";
		try {
			st = conn.prepareStatement(sql);
		} catch (SQLException e) {
			databaseError(e);
		}
	}

	@Override
	public void checkCritical(int[] newin) throws ClosedException {
		String[] cols = comp.getKeys();
		for (int i = 0; i < newin.length; ++i) {
			ResultSet r;
			try {
				st.setString(1, cols[i]);
				r = st.executeQuery();
				if(r.next()){
					if(newin[i] > r.getInt(1)){
						errorMail(cols[i] + " exceeded the critical value in " + comp.getTableName(), "critical value");
					}
				}
			} catch (SQLException e) {
				databaseError(e);
			}


		}
	}

}
