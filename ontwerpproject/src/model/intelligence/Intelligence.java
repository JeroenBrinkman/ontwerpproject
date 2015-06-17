package model.intelligence;

import model.Component;
import model.Model;
import global.Globals;
import global.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;

public abstract class Intelligence {
	
	public class ClosedException extends Exception {
		  /**
		 * for some reason it needs this
		 */
		private static final long serialVersionUID = 2400467736031885588L;
		public ClosedException() { super(); }
		  public ClosedException(String message) { super(message); }
		  public ClosedException(String message, Throwable cause) { super(message, cause); }
		  public ClosedException(Throwable cause) { super(cause); }
		}
	protected PreparedStatement st;
	protected Connection con;
	protected Component comp;
	protected Model mod;
	protected int[] LIMITS; /*
							 * list of counters, keeps track of when we send a
							 * mail
							 */
	protected final static int LIMIT = 60 * 12; /*
												 * send mail max once per hour
												 * per issue
												 */// TODO update this to
													// correct value

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
	public static void errorMail(String message, String subject) {
		// TODO reset password
		String to = "test@test.test";// needs to be valid though
		String from = "monitoringontwerpproject@gmail.com";
		final String username = "monitoringontwerpproject@gmail.com";
		final String password = "T3st1234";
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
			//Transport.send(email); TODO uncomment when it works
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	public void errorNotification(String at, String message) {
		try {
			Statement st = con.createStatement();
			String sql = "INSERT INTO notifications VALUES( "+ comp.getTableName() + ", " + at + ", " + message +")";
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
	 * send error, and disconnect the relevant component
	 * 
	 * @require e != null
	 * @ensure component disconnected and error mail send
	 * 
	 */
	public void databaseError(SQLException e) throws ClosedException{
		if (Globals.LAST_DATABASE_ERROR == -1
				|| (System.currentTimeMillis() - Globals.LAST_DATABASE_ERROR) > Globals.MIN_DATABASE_ERROR_DELAY) {
			errorMail(
					"Database fail in "
							+ comp.getTableName()
							+ ". The component has been disconnected from the system with error : "
							+ e.getMessage(), "Database error");
		}
		mod.removeComponent(comp);
		Logger.log("databaseconnection failed, ClosedException thrown");
		throw new ClosedException("alles kapot");
	}

	public void connectionError() throws ClosedException{
		errorMail("Component " + comp.getTableName()
				+ " disconnected from the system.", "Component disconnected");
		mod.removeComponent(comp);

		throw new ClosedException("alles kapot");
	}

	/**
	 * Checks the new input data for the component for critical values, and
	 * sends the correct error messages if it finds these
	 * @throws ClosedException when the database dies
	 * 
	 * @requires newin != null
	 * @ensures correct errormessages are send
	 */
	public void checkCritical(long[] newin) throws ClosedException {
		String[] cols = comp.getKeys();
		for (int i = 0; i < newin.length; ++i) {
			ResultSet r;
			try {
				st.setString(1, cols[i]);
				r = st.executeQuery();
				if (r.next()) {
					if (newin[i] > r.getLong(1)) {
						String message = cols[i] + " exceeded the critical value in " + comp.getTableName();
						errorMail(message, "critical value");
						errorNotification(cols[i], message);
						Logger.log("error state found in " + comp.getTableName());
					}
				}
			} catch (SQLException e) {
				//TODO: WTF WAAROM GAAT DEZE ZO VAAK AF???
				Logger.log("Failed to find critical value table");
				errorMail("Failed to find the critical value table, will not be able to send alerts \n" + e.getMessage(), "unable to detect allerts");
			}

		}
	}

}
