package model.intelligence;

import model.Component;
import model.Model;

public class WorkerIntelligence extends Intelligence {

	public WorkerIntelligence(Component comp, Model mod) {
		super(comp, mod);
		LIMITS = new int[comp.getKeys().length];
	}

	@Override
	public void checkCritical(String[] newin) {
		if (newin.length != comp.getKeys().length) {
			// TODO crash with error
		} else {


		}
	}
}
