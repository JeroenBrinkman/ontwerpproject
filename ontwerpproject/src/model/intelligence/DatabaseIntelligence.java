package model.intelligence;

import java.sql.Connection;

import model.Component;
import model.Model;

/**
 * intelligence for the database
 * @author Jeroen
 *
 */
public class DatabaseIntelligence extends Intelligence {

	public DatabaseIntelligence(Component comp, Model mod, Connection conn)
			throws ClosedException {
		super(comp, mod, conn);
	}
}
