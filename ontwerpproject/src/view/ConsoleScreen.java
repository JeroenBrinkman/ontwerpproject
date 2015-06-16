package view;

import java.awt.Font;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class ConsoleScreen extends JScrollPane {

	private static final long serialVersionUID = 8779438680651538749L;
	private JTextArea textArea;
	
	public ConsoleScreen(){
		super(new JTextArea(), VERTICAL_SCROLLBAR_ALWAYS,
				HORIZONTAL_SCROLLBAR_NEVER);
		this.textArea = (JTextArea) getViewport().getComponent(0);
		textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Console Messages"));
		redirectSystemStreams();
	}

	private void updateTextArea(final String text) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				textArea.append(text);
			}
		});
	}

	private void redirectSystemStreams() {
		OutputStream out = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				updateTextArea(String.valueOf((char) b));
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				updateTextArea(new String(b, off, len));
			}

			@Override
			public void write(byte[] b) throws IOException {
				write(b, 0, b.length);
			}
		};

		System.setOut(new PrintStream(out, true));
		System.setErr(new PrintStream(out, true));
	}
}
