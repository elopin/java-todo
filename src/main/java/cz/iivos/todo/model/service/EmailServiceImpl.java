package cz.iivos.todo.model.service;

import cz.iivos.todo.model.Task;
import cz.iivos.todo.model.User;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Produkční implementace služby pro odesílání notifikací e-mailem.
 *
 * @author lukas
 * @see
 * http://www.mkyong.com/java/javamail-api-sending-email-via-gmail-smtp-example/
 */
public class EmailServiceImpl implements EmailService {

    private ResourceBundle resource;

    private Properties properties;

    private Session session;

    /**
     * Konstruktor vytvoří produkční službu pro odesílání notifikací e-mailem.
     */
    public EmailServiceImpl() {
	resource = ResourceBundle.getBundle("email");
	properties = new Properties();
	properties.put("mail.smtp.host", resource.getString("smtp"));
	properties.put("mail.smtp.socketFactory.port", resource.getString("port"));
	properties.put("mail.smtp.socketFactory.class",
		"javax.net.ssl.SSLSocketFactory");
	properties.put("mail.smtp.auth", "true");
	properties.put("mail.smtp.port", resource.getString("port"));

	session = Session.getDefaultInstance(properties,
		new javax.mail.Authenticator() {
		    @Override
		    protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(resource.getString("email"), resource.getString("password"));
		    }
		});
    }

    /**
     * Odešle upozornění o úkolu na e-mail uživatele.
     *
     * @param user upozorňovaný uživatel
     * @param task předmět upozornění
     */
    @Override
    public void sendEmailNotificationToUser(User user, Task task) {
	String email = user.getEmail();
	String subject = "Upozornění na úkol!";
	StringBuilder sb = new StringBuilder();
	sb.append("Název úkolu: ").append(task.getTitle()).append(System.lineSeparator());
	sb.append("Termín dokončení: ").append(task.getDeadline()).append(System.lineSeparator());
	sb.append("Popis úkolu: ").append(task.getDescription());
	
	sendMail(email, subject, sb.toString());
    }

    /**
     * Odešle odkaz pro potvrzení registrace uživatele.
     * @param user uživatel
     * @param link odkaz
     */
    @Override
    public void sendEmailConfirmationLink(User user, String link) {
	String email = user.getEmail();
	String subject = "Potvrzení registrace v Úkolovníčku";
	StringBuilder sb = new StringBuilder();
	sb.append("Děkujeme za registraci v Ǔkolovníčku,").append(System.lineSeparator());
	sb.append("Pro potvrzení registrace klikněte na následující odkaz: ").append(System.lineSeparator());
	sb.append(System.lineSeparator()).append(link).append(System.lineSeparator()).append(System.lineSeparator());
	sb.append("Pokud odkaz nefunguje, zkopírujte ho do adresního řádku Vašeho prohlížeče.");
	
	sendMail(email, subject, sb.toString());
    }
    
    /**
     * Odešle e-mail na danou e-mailovou adresu.
     * @param email e-mailová adresa
     * @param subject předmět zprávy
     * @param body tělo zprávy
     */
    private void sendMail(String email, String subject, String body) {
	try {
	    Message message = new MimeMessage(session);
	    message.setFrom(new InternetAddress(resource.getString("email")));
	    message.setRecipients(Message.RecipientType.TO,
		    InternetAddress.parse(email));
	    message.setSubject(subject);
	    message.setText(body);
	    Transport.send(message);

	} catch (MessagingException e) {
	    throw new RuntimeException(e);
	}
    }
}
