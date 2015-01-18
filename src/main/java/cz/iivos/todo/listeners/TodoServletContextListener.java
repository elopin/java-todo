package cz.iivos.todo.listeners;

import cz.iivos.todo.AppConstants;
import cz.iivos.todo.database.DBAdapter;
import cz.iivos.todo.model.service.NotificationService;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Listener spuštění aplikace na serveru. Inicializuje upozornění na úkoly a
 * kontrolu účtu admina.
 * @author lukas
 */
@WebListener
public class TodoServletContextListener implements ServletContextListener {
    
    private ScheduledExecutorService notification;
    
    private DBAdapter connection;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
	
	//kontrola účtu admina
	connection = new DBAdapter();
	connection.checkAdmin();
	
        //spuštění notifikací
	TimeZone timeZone = TimeZone.getTimeZone("CET");
	Calendar calendar = GregorianCalendar.getInstance(timeZone);
	Date now = calendar.getTime();
	System.out.println("NOW: " + now);
	calendar.set(Calendar.HOUR_OF_DAY, 23);
	calendar.set(Calendar.MINUTE, 59);
	calendar.set(Calendar.SECOND, 59);
	calendar.set(Calendar.MILLISECOND, 999);
	
	Date midnight = calendar.getTime();
	System.out.println("MIDNIGHT: " + midnight);
	
	Long delay = midnight.getTime() - now.getTime();
	
	System.out.println("Starting notification service in " + delay + "millis at " + midnight);
	notification = Executors.newSingleThreadScheduledExecutor();
	notification.scheduleAtFixedRate(new NotificationService(), delay, AppConstants.MILLIS_IN_DAY, TimeUnit.MILLISECONDS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
    
}
