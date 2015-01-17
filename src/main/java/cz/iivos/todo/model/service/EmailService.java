package cz.iivos.todo.model.service;

import cz.iivos.todo.model.Task;
import cz.iivos.todo.model.User;

/**
 * Rozhraní pro třídy implementující odesílání zpráv e-mailem.
 * @author lukas
 */
public interface EmailService {
    
    /**
     * Odešle na e-mail uživatele upozornění na úkol.
     * @param user uživatel
     * @param task úkol
     */
    void sendEmailNotificationToUser(User user, Task task);
    
    /**
     * Odešle na e-mail uživatele link potvrzující registraci uživatele.
     * @param user uživatel
     * @param link potvrzovací link
     */
    void sendEmailConfirmationLink(User user, String link);
    
}
