package model.intelligence;

import java.sql.Connection;
import java.sql.SQLException;

import model.Component;
import model.Model;

public class WorkerIntelligence extends Intelligence {

	public WorkerIntelligence(Component comp, Model mod, Connection conn)
			throws ClosedException {
		super(comp, mod, conn);
		LIMITS = new int[comp.getKeys().length];
		// worker values = { name - value}
		String sql = "SELECT value FROM workervalues WHERE name =  ? ";
		try {
			st = conn.prepareStatement(sql);
		} catch (SQLException e) {
			databaseError(e);
		}
	}
}
