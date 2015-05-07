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
		String to = "jeroen.brinkman@home.nl";
		String from = "hoi@hai.nl";
		String host = "localhost";
		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", host);
		Session session = Session.getInstance(properties);
		try {
			MimeMessage email = new MimeMessage(session);
			email.setFrom(new InternetAddress(from));
			email.addRecipient(Message.RecipientType.TO,
					new InternetAddress(to));
			if (subject == null) {
				email.setSubject("Alles kapot");
			} else {
				email.setSubject(subject);
			}
			if (message == null) {
				email.setText("niks meer heel");
			} else {
				email.setText(message);
			}

			Transport.send(email);
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}

	public void errorPopup() {

	}

	public void errorSMS() {

	}

	public abstract void checkCritical(String[] newin);

}
