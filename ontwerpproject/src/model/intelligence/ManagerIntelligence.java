package model.intelligence;

import java.sql.Connection;

import java.sql.SQLException;

import model.Component;
import model.Model;

public class ManagerIntelligence extends Intelligence {

	public ManagerIntelligence(Component comp, Model mod, Connection conn)
			throws ClosedException {
		super(comp, mod, conn);
		LIMITS = new int[comp.getKeys().length];
		String sql = "SELECT value FROM managervalues WHERE name =  ? ";
		try {
			st = conn.prepareStatement(sql);
		} catch (SQLException e) {
			databaseError(e);
		}
	}
}
