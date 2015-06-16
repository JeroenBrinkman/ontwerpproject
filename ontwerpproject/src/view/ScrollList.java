package view;


import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ScrollList extends JScrollPane{

	private static final long serialVersionUID = 1L;

	/**
	 * The JTextArea of this component.
	 */
	private JTextArea textArea;

	/**
	 * Creates an uneditable text area with a title and scrollbar.
	 * 
	 * @param title
	 *            The title this component should have.
	 */
	public ScrollList(Boolean console) {
		super(new JTextArea(), VERTICAL_SCROLLBAR_ALWAYS,
				HORIZONTAL_SCROLLBAR_NEVER);
		this.textArea = (JTextArea) getViewport().getComponent(0);
		textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Components"));
		
	}

	/**
	 * Adds a new line with the given text to the top of the text area.
	 * 
	 * @param text
	 *            The text to add.
	 */
	public void addText(String text) {
		textArea.insert(text + "\n", 0);
	}

	/**
	 * Replaces all the text with the given text.
	 * 
	 * @param text
	 *            The text the text area should contain.
	 */
	public void setText(String text) {
		textArea.setText(text);
	}
}
