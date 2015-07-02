package model.intelligence;

import java.sql.Connection;

import model.Component;
import model.Model;

/**
 * intelligence for the manager
 * @author Jeroen
 *
 */
public class ManagerIntelligence extends Intelligence {

	public ManagerIntelligence(Component comp, Model mod, Connection conn)
			throws ClosedException {
		super(comp, mod, conn);
	}
}
