package view;

//Imports are listed in full to show what's being used //could just import javax.swing.* and java.awt.* etc.. 
import global.Globals;

import javax.swing.*;

import model.Component;
import model.Model;
import model.Worker;
import model.intelligence.Intelligence.ClosedException;
import java.awt.GridLayout;
import java.net.InetSocketAddress;
import java.util.ArrayList;

public class GUI {
	private Model mod;
	private ScrollList complist;
	private JLabel lastupdate;
	private JLabel lastcompcon;
	private JLabel amountupdates;
	

	// Note: Typically the main method will be in a
	// separate class. As this is a simple one class
	// example it's all in the one class.
	public static void main(String[] args) {
		Model mod = new Model();
		try {
			mod.addComponent(new Worker(
						new InetSocketAddress("192.192.192.192", 123),
						mod.createConnection(), mod));
		} catch (ClosedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		guiFrame.add(compPanel());
		guiFrame.add(compPanel());
		// make sure the JFrame is visible
		guiFrame.setVisible(true);
	}

	private JPanel compPanel() {
		JPanel out = new JPanel();
		out.setLayout(new GridLayout(2,1));
		complist = new ScrollList();
		updateCompList(mod.getComponents());
		JLabel comps = new JLabel("Components:");
		out.add(comps);
		out.add(complist);
		JPanel right = new JPanel();
		right.setLayout(new GridLayout(1,0));
		lastupdate = new JLabel("Last update : NEVER");
		lastcompcon = new JLabel("Last component connected : NEVER");
		amountupdates = new JLabel("Total amount of updates : NONE");
		right.add(lastupdate);
		right.add(lastcompcon);
		right.add(amountupdates);
		out.add(right);
		return out;
	}

	public void updateCompList(ArrayList<Component> comps) {
		complist.setText("");
		for (Component c : comps) {
			complist.addText(c.getTableName());;
		}
		updateMetaData();
	}
	
	public void updateMetaData(){
		lastupdate.setText("Last update : " + Globals.LAST_UPDATE);
		lastcompcon.setText("Last component connected : " + Globals.LAST_COMPONENT);
		amountupdates.setText("Total amount of updates : " + Globals.AMOUNT_UPDATES);
	}

	private JPanel logPanel() {
		return null;
	}



}
