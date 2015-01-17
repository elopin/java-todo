package cz.iivos.todo.model.service;

import cz.iivos.todo.AppConstants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

import java.util.Date;

import cz.iivos.todo.database.DBAdapter;
import cz.iivos.todo.enums.RelativeDays;
import cz.iivos.todo.enums.TaskWarnings;
import cz.iivos.todo.model.Task;
import cz.iivos.todo.model.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum představující periody opakování úkolu
 *
 * @author stefan
 */
public class TaskServiceImpl implements TaskService {

    private Logger logger = Logger.getLogger(TaskService.class.getName());

    private final DBAdapter dbAdapter;

    private final UserService userService;

    //0.
    /**
     * Konstruktor.
     */
    public TaskServiceImpl() {
        dbAdapter = new DBAdapter();
        userService = new UserServiceImpl();
    }

    @Override
    public List<Task> findAllTasksByUser(User usr) {
        //Date datum;

        return dbAdapter.findAllTasksByUser(usr);

    }

    @Override
    public List<Task> findActualTasksByUser(User usr) {
        return dbAdapter.findActualTasksByUser(usr);
    }

    @Override
    public List<Task> findTasksOnDate(User usr, RelativeDays mode) {

        Date d;
        Calendar calL, calR;
        calL = Calendar.getInstance();
        calR = Calendar.getInstance();

        switch (mode) {
            case TODAY:
                //calL.setTime(new Date());
                calL.add(Calendar.DATE, 0);
                break;
            case YESTERDAY:
                calL.add(Calendar.DATE, -1);
                break;
            case TOMORROW:
                calL.add(Calendar.DATE, 1);
                break;
            default:
                logger.info("Zatim funkce pocita jenom s dneskem / vcerejskem!!");
                return null;
        }

        List<Task> vsjo = dbAdapter.findActualTasksByUser(usr);
        List<Task> vyhovujuce = new ArrayList<>();

        for (Task t : vsjo) {
            d = t.getDeadline();
            if (d != null){
                calR.setTime(d);
                boolean sameDay = (calL.get(Calendar.YEAR) == calR.get(Calendar.YEAR))
                    && (calL.get(Calendar.DAY_OF_YEAR) == calR.get(Calendar.DAY_OF_YEAR));
                if (sameDay) {
                    vyhovujuce.add(t);
                }
            }
        }
        return vyhovujuce;
    }

    @Override
    public Map<User, List<Task>> findHotTasks() {
        Date now = new Date();
        Map<User, List<Task>> hotTasks = new HashMap<>();
        userService.getAllUsers().stream().forEach((user) -> {
            List<Task> hot = new ArrayList<>();
            findActualTasksByUser(user).stream().forEach((task) -> {
                if (!task.getWarning_period().equals(TaskWarnings.NONE)) {
                    int dayPeriod = 0;
                    if (task.getWarning_period().equals(TaskWarnings.DAY_BEFORE)) {
                        dayPeriod = 1;
                    } else {
                        dayPeriod = 7;
                    }

                    Date deadLine = task.getDeadline();
                    Date border = new Date(deadLine.getTime() - dayPeriod * (AppConstants.MILLIS_IN_DAY));
                    if (deadLine.after(now) && deadLine.after(border)) {
                        hot.add(task);
                    }
                }
            });
            if (!hot.isEmpty()) {
                hotTasks.put(user, hot);
            }
        });

        return hotTasks;

    }

}
