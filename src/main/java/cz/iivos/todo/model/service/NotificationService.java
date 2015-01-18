package cz.iivos.todo.model.service;

import cz.iivos.todo.model.Task;
import cz.iivos.todo.model.User;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Služba upozorňující na úkoly uživatele. Kontrola upozornění je prováděna
 * jednou denně.
 *
 * @author lukas
 */
public class NotificationService implements Runnable {

    private final Logger logger = Logger.getLogger(NotificationService.class);
    
    private final TaskService taskService;

    private final EmailService emailService;

    public NotificationService() {
	taskService = new TaskServiceImpl();
	emailService = new EmailServiceMock();
    }

    @Override
    public void run() {
	logger.info("Start notification check!");
	Map<User, List<Task>> hotTasks = taskService.findHotTasks();
	hotTasks.entrySet().stream().forEach((entry) -> {
	    entry.getValue().stream().forEach((task) -> {
		emailService.sendEmailNotificationToUser(entry.getKey(), task);
	    });
	});
    }
}
