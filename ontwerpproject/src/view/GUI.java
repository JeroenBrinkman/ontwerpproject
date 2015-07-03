package view;

import global.Globals;
import global.Logger;

import javax.swing.*;

import model.Component;
import model.Model;
import model.Worker;
import model.intelligence.Intelligence.ClosedException;

import java.awt.GridLayout;
import java.net.InetSocketAddress;


/**
 * Quick and dirty debugging gui, prints console to the gui and shows amount of updates etc
 * @author Jeroen
 *
 */
public class GUI {
	private Model mod;
	private ScrollList complist;
	private JLabel lastupdate;
	private JLabel lastcompcon;
	private JLabel amountupdates;
	private Updater update;

	public static void main(String[] args) {
		Model mod = new Model();
		try {
			mod.addComponent(new Worker(
						new InetSocketAddress("192.192.192.192", 123),
						mod.createConnection(), mod));
		} catch (ClosedException e) {
			Logger.log(e.getMessage());
		}
		new GUI(mod);

	}
	public GUI(Model mod) {
		this.mod = mod;
		JFrame guiFrame = new JFrame();
		guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		guiFrame.setTitle("Monitoring system");
		guiFrame.setSize(600, 800);
		guiFrame.setLocationRelativeTo(null);
		GridLayout g = new GridLayout(2, 1);
		guiFrame.setLayout(g);
		guiFrame.add(new ConsoleScreen());
		guiFrame.add(compPanel());
		guiFrame.setVisible(true);
		update = new Updater(this);
		update.run();
		
	}

	private JPanel compPanel() {
		JPanel out = new JPanel();
		out.setLayout(new GridLayout(2,1));
		complist = new ScrollList(false);
		out.add(complist);
		JPanel right = new JPanel();
		right.setLayout(new GridLayout(1,3));
		lastupdate = new JLabel("Last update : NEVER");
		lastcompcon = new JLabel("Last component connected : NEVER");
		amountupdates = new JLabel("Total amount of updates : NONE");
		right.add(lastupdate);
		right.add(lastcompcon);
		right.add(amountupdates);
		out.add(right);
		return out;
	}

	public void updateCompList() {
		complist.setText("");
		for (Component c : mod.getComponents()) {
			complist.addText(c.getTableName());;
		}
	}
	
	public void updateMetaData(){
		lastupdate.setText("Last update : " + Globals.LAST_UPDATE);
		lastcompcon.setText("Last component connected : " + Globals.LAST_COMPONENT);
		amountupdates.setText("Total amount of updates : " + Globals.AMOUNT_UPDATES);
	}



}
