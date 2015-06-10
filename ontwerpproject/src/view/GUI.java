package view;

//Imports are listed in full to show what's being used //could just import javax.swing.* and java.awt.* etc.. 
import javax.swing.*;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GUI {
	// Note: Typically the main method will be in a
	// separate class. As this is a simple one class
	// example it's all in the one class.
	public static void main(String[] args) {
		new GUI();
	}

	public GUI() {
		JFrame guiFrame = new JFrame();
		guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		guiFrame.setTitle("Monitoring system");
		guiFrame.setSize(800, 800);
		guiFrame.setLocationRelativeTo(null);
		GridLayout g = new GridLayout(2,2);
		guiFrame.setLayout(g);




		// make sure the JFrame is visible
		guiFrame.setVisible(true);
	}

}
