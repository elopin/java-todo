package cz.iivos.todo.model.service;

import cz.iivos.todo.model.Task;
import cz.iivos.todo.model.User;
import org.apache.log4j.Logger;

/**
 * Mock třída pro odesílání e-mailů.
 * @author lukas
 */
public class EmailServiceMock implements EmailService {
    
    private Logger logger = Logger.getLogger(EmailService.class.getName());

    @Override
    public void sendEmailNotificationToUser(User user, Task task) {
	logger.info("Odesílám upozornění uživateli " + user.getDisplayName() + " na úkol se jménem " + task.getTitle());
    }

    @Override
    public void sendEmailConfirmationLink(User user, String link) {
	logger.info("Odesílám link potvrzující registraci uživatele " + link);
    }
    
}
