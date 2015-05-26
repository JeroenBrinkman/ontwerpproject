package model.intelligence;

import model.Component;
import model.Model;

public class ManagerIntelligence extends Intelligence {

	public ManagerIntelligence(Component comp, Model mod) {
		super(comp, mod);
		LIMITS = new int[comp.getKeys().length];
	}

	@Override
	public void checkCritical(String[] newin) {
		if (newin.length != comp.getKeys().length) {
			// TODO crash with error
		} else {
			String[] keys = comp.getKeys();
			for (int i = 0; i < newin.length; ++i) {
				if (Integer.parseInt(newin[i]) > CRITS[i]) {
					if (LIMITS[i] == 0) {
						errorMail("Components " + keys[i] + " is critical : "
								+ newin[i], comp.getTableName());
						LIMITS[i] = LIMIT;
					} else {
						LIMITS[i]++;
					}
				} else {
					if (LIMITS[i] > 0)
						LIMITS[i]--;
				}
			}
		}
	}
}
