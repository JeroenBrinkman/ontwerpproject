package view;

//Imports are listed in full to show what's being used //could just import javax.swing.* and java.awt.* etc.. 
import javax.swing.*;

import model.Component;
import model.Model;
import model.Worker;
import model.intelligence.Intelligence.ClosedException;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.InetSocketAddress;
import java.util.ArrayList;

public class GUI {
	private Model mod;
	private JList<String> complist;

	// Note: Typically the main method will be in a
	// separate class. As this is a simple one class
	// example it's all in the one class.
	public static void main(String[] args) {
		Model mod = new Model();
		new GUI(mod);
		try {
			mod.addComponent(new Worker(
						new InetSocketAddress("192.192.192.192", 123),
						mod.createConnection(), mod));
		} catch (ClosedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public GUI(Model mod) {
		this.mod = mod;
		JFrame guiFrame = new JFrame();
		guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		guiFrame.setTitle("Monitoring system");
		guiFrame.setSize(800, 800);
		guiFrame.setLocationRelativeTo(null);
		GridLayout g = new GridLayout(1, 2);
		guiFrame.setLayout(g);
		guiFrame.add(compPanel());
		guiFrame.add(compPanel());
		// make sure the JFrame is visible
		guiFrame.setVisible(true);
	}

	private JPanel compPanel() {
		JPanel out = new JPanel();
		complist = new JList<String>();
		updateCompList(mod.getComponents());
		JLabel comps = new JLabel("Components:");
		out.add(comps);
		out.add(complist);
		return out;
	}

	public void updateCompList(ArrayList<Component> comps) {
		int index = complist.getSelectedIndex();
		DefaultListModel<String> model = new DefaultListModel<String>();
		for (Component c : comps) {
			model.addElement(c.getTableName());
		}
		complist.setModel(model);
		complist.setSelectedIndex(index);
	}

	private JPanel logPanel() {
		return null;
	}



}
