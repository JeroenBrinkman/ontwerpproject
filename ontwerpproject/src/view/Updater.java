package view;

import global.Globals;

public class Updater extends Thread {
	private GUI gui;

	public Updater(GUI gui) {
		this.gui = gui;
	}

	@Override
	public void run() {
		while (true) {
			gui.updateCompList();
			gui.updateMetaData();
			try {
				sleep(Globals.GUI_UPDATE);
			} catch (InterruptedException e) {
			}
		}
	}
}
