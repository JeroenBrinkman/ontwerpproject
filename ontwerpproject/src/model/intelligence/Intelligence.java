package model.intelligence;

import model.Component;
import model.Model;

import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;

public abstract class Intelligence {
	protected Component comp;
	protected Model mod;
	protected int[] CRITS = { 80, 90, 99 };// TODO init crits in subclasses
	protected int[] LIMITS; /* list of counters, keeps track of when we send a mail*/
	protected final static int LIMIT = 60 * 12; /* send mail max once per hour per issue*///TODO update this to correct value

	public Intelligence(Component comp, Model mod) {
		this.comp = comp;
		this.mod = mod;
	}

	/**
	 * Sends an error Email to the preset addresses with a given subject and a
	 * given message
	 * 
	 * @requires message != null
	 * @requires subject != null
	 */
	public void errorMail(String message, String subject) {
		// TODO make this global
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
			Transport.send(email);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	public void errorPopup() {
		// TODO wegschrijven in textfile of in de webserver database

	}

	public void errorSMS() {
		// TODO indien tmobile ofzo heel makkelijk, gewoon email naar
		// \nummer/@tmobile.com en dit wordt een sms naar het nummer (kosten van
		// ontvanger)
	}
	
	/**
	 * send error, and disconnect the relevant component
	 * 
	 */
	public void databaseError(){
		errorMail("Database connection fail in " + comp.getTableName() + ". The component has been disconnected from the system.","Database error");
		
	}

	/**
	 * Checks the new input data for the component for critical values, and
	 * sends the correct error messages if it finds these
	 * 
	 * @requires newin != null
	 * @ensures correct errormessages are send
	 */
	public abstract void checkCritical(String[] newin);

}
