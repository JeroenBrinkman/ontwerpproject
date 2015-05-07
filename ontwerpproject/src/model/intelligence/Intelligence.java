package model.intelligence;

import model.Component;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public abstract class Intelligence {
	protected Component comp;

	public Intelligence(Component comp) {
		this.comp = comp;
	}

	public void errorMail(String message, String subject) {
		// TODO make this global
		String to = "test@test.test";//needs to be valid though
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

	}

	public void errorSMS() {

	}

	public abstract void checkCritical(String[] newin);

}
