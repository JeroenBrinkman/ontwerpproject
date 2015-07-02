package model.intelligence;

import model.Component;
import model.Model;
import global.Globals;
import global.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;

/**
 * responsible for everything error related, has a couple of static methods for
 * sending mails and setting notifications
 * 
 * @author Jeroen
 *
 */
public abstract class Intelligence {

	public class ClosedException extends Exception {
		/**
		 * for some reason it needs this
		 */
		private static final long serialVersionUID = 2400467736031885588L;

		/**
		 * Thrown whenever a component has disconnect or has to disconnect, for
		 * example when the database connection fails
		 */
		public ClosedException() {
			super();
		}

		public ClosedException(String message) {
			super(message);
		}

		public ClosedException(String message, Throwable cause) {
			super(message, cause);
		}

		public ClosedException(Throwable cause) {
			super(cause);
		}
	}

	/**
	 * connection, same connection as the corresponding component
	 */
	protected Connection con;
	/**
	 * pointer to the component
	 */
	protected Component comp;
	/**
	 * pointer to the model
	 */
	protected Model mod;

	public Intelligence(Component comp, Model mod, Connection conn) {
		this.comp = comp;
		this.mod = mod;
		con = conn;
	}

	/**
	 * Sends an error Email to the preset addresses with a given subject and a
	 * given message
	 * 
	 * @requires message != null
	 * @requires subject != null
	 */
	public static void errorMail() {
		// TODO reset password
		String to = Globals.MAILTARGET;
		String from = Globals.MAILACCOUNT;
		final String username = Globals.MAILACCOUNT;
		final String password = Globals.MAILPASS;
		// Config for sending with gmail
		String host = "smtp.gmail.com";
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "587");
		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});

		try {
			Message email = new MimeMessage(session);
			email.setFrom(new InternetAddress(from));
			email.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(to));
			email.setSubject("Testing Subject");
			email.setText("Hello, this is sample for to check send "
					+ "email using JavaMailAPI ");
			Transport.send(email);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * sets a notification in the database, used by the website to generate
	 * notifications
	 * 
	 * @param at
	 *            Metric in which the error is detected
	 * @param message
	 *            Error message to be set
	 */
	public void errorNotification(String at, String message) {
		try {
			Statement st = con.createStatement();
			String sql = "INSERT INTO notifications VALUES( "
					+ comp.getTableName() + ", " + at + ", " + message + ", "
					+ System.currentTimeMillis() + ")";
			st.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static void errorSMS() {
		// TODO indien tmobile ofzo heel makkelijk, gewoon email naar
		// \nummer/@tmobile.com en dit wordt een sms naar het nummer (kosten van
		// ontvanger)
	}

	/**
	 * send error, and disconnect the relevant component, should be called when
	 * the database disconnects
	 * 
	 * @require e != null
	 * @ensure component disconnected and error mail send
	 * 
	 */
	public void databaseError(SQLException e) throws ClosedException {
		Logger.log(e.getMessage());
		if (Globals.LAST_ERROR == -1
				|| (System.currentTimeMillis() - Globals.LAST_ERROR) > Globals.MIN_ERROR_DELAY) {
			errorNotification(
					"cpu",
					"Database fail in "
							+ comp.getTableName()
							+ ". The component has been disconnected from the system with error : "
							+ e.getMessage());
			errorMail();
		}
		mod.removeComponent(comp);
		Logger.log("databaseconnection failed, ClosedException thrown");
		throw new ClosedException("alles kapot");
	}

	/**
	 * Called when the component disconnects from the system attempts to send an
	 * error mail, removes the component from the model. also sets a
	 * notification
	 */
	public void connectionError() {
		errorNotification("cpu", "Component " + comp.getTableName()
				+ " disconnected from the system.");
		Logger.log("componentdisconnected, sending mail");
		mod.removeComponent(comp);
	}

	/**
	 * Checks the new input data for the component for critical values, and
	 * sends the correct error messages if it finds these
	 * 
	 * @throws ClosedException
	 *             when the database dies
	 * 
	 * @requires newin != null
	 * @ensures correct errormessages are send
	 */
	public void checkCritical(long[] newin) throws ClosedException {
		String[] cols = comp.getKeys();
		for (int i = 0; i < newin.length; ++i) {
			// only implement for cpu mem and hdd here
			switch(cols[i]){
			case "cpu":
				if(newin[i]>Globals.CPUCRIT){
					errorNotification(cols[i], cols[i]
							+ " exceeded the critical value in "
							+ comp.getTableName());
					errorMail();
					Logger.log("error state found in " + comp.getTableName());
				};
			case "mem":
				if(newin[i]>Globals.MEMCRIT){
					errorNotification(cols[i], cols[i]
							+ " exceeded the critical value in "
							+ comp.getTableName());
					errorMail();
					Logger.log("error state found in " + comp.getTableName());
				};
			case "hdd":
				if(newin[i]>Globals.HDDCRIT){
					errorNotification(cols[i], cols[i]
							+ " exceeded the critical value in "
							+ comp.getTableName());
					errorMail();
					Logger.log("error state found in " + comp.getTableName());
				};
			}
		}
	}
}
