package cz.iivos.todo.model;

import cz.iivos.todo.enums.TaskRepetitions;
import cz.iivos.todo.enums.TaskWarnings;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Entita úkolu uživatele.
 *
 * @author Štefan
 * @author Marek Švarc
 */
public class Task {

    final private Logger logger = Logger.getLogger(Task.class.getName());
    /**
     * id ukolu.
     */
    private Long id_tak;

    /**
     * id uzivatele, kteremu patri ukol.
     */
    private Long id_lur;

    /**
     * id kategorie do ktere ukol patri.
     */
    private Long id_tcy;

    /**
     * opakovani ukolu.
     */
    private TaskRepetitions repetition_period; //"none", "weekly", "monthly"

    /**
     * doba pred terminem/dobou opakovani ukolu, kdy dojde uzivateli upozorneni
     * o blizicim se termine.
     */
    private TaskWarnings warning_period; //"dayBefore", "weekBefore"

    /**
     * popis ukolu.
     */
    private String description;

    /**
     * Titulek, nazev ukolu
     */
    private String title;

    /**
     * je ukol splnen?.
     */
    private Boolean completed;

    /**
     * datum vzniku ukolu.
     */
    private Date creation_date;

    /**
     * termin ukolu.
     */
    private Date deadline;

    /**
     * datum ukonceni ukolu.
     */
    private Date completion_date;

    public Long getId_tak() {
        return id_tak;
    }

    public void setId_tak(Long id_tak) {
        this.id_tak = id_tak;
    }

    public Long getId_lur() {
        return id_lur;
    }

    public void setId_lur(Long id_lur) {
        this.id_lur = id_lur;
    }

    public Long getId_tcy() {
        return id_tcy;
    }

    public void setId_tcy(Long id_tcy) {
        this.id_tcy = id_tcy;
    }

    public TaskRepetitions getRepetition_period() {
        return repetition_period;
    }

    public void setRepetition_period(TaskRepetitions repetition_period) {
        this.repetition_period = repetition_period;
    }

    public TaskWarnings getWarning_period() {
        return warning_period;
    }

    public void setWarning_period(TaskWarnings warning_period) {
        this.warning_period = warning_period;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Date getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(Date creation_date) {
        this.creation_date = creation_date;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Date getCompletion_date() {
        return completion_date;
    }

    public void setCompletion_date(Date completition_date) {
        this.completion_date = completition_date;
    }

    /**
     * Rika, jestli dany ukol prekrocil termin upozorneni: tj. day_before,
     * week_before.. Pokus není zvolena perioda opakování, pak se warning
     * vztahuje na Deadline, pokud nastavena je, pak se vztahuje na x * perioda
     * + creation_date.
     *
     * @return
     */
    public boolean isHot() {
        // test zda jiz uloha neni dokoncena nebo nema stanoven termin nebo nepozaduje varovani
        if (getCompleted() || (this.getDeadline() == null) || (getWarning_period() == TaskWarnings.NONE)) {
            return false;
        }

        // casove useky v milisekundach
        Long day = (24 * 3600 * 1000L);
        Long week = 7 * day;

        // aktualni cas
        Date now = new Date();
        long nowTime = now.getTime();

        // zakladni termin dokonceni
        long hotTime = getDeadline().getTime();

        // termin jiz byl prekrocen nema cenu pokracovat v testovani
        if (hotTime < nowTime) {
            return true;
        }

        // pro periodicke ulohy je treba nalezt termin nejblize aktualnimu casu
        switch (this.getRepetition_period()) {
            case WEEKLY:
                hotTime = calcHotTimeByMillisPeriod(hotTime, nowTime, week);
                break;
            case MONTHLY:
                hotTime = calcHotTimeByMonthPeriod(hotTime);
                break;
        }

        // testovani zda je nutno na ulohu upozornit
        switch (getWarning_period()) {
            case DAY_BEFORE:
                return nowTime >= (hotTime - day);
            case WEEK_BEFORE:
                return nowTime >= (hotTime - week);
            default:
                return false;
        }
    }

    /**
     * Sestavi novy seznam ukolu, ktery bude obsahovat pouze ukoly u kterych je
     * potreba upozorneni
     *
     * @param tasks seznam vsech ukolu
     * @return seznam ukolu
     */
    static public List<Task> getHotTasks(List<Task> tasks) {
        List<Task> hotTasks = new ArrayList<>();
        if (tasks != null) {
            for (Task task : tasks) {
                if (task.isHot()) {
                    hotTasks.add(task);
                }
            }
        }
        return hotTasks;
    }

    /**
     * Vypocte nejblizsi termin dokonceni ze zakladniho terminu a periody
     * definovane v milisekundach
     *
     * @param hotTime zakladni termin dokonceni ukolu
     * @param nowTime aktualni cas
     * @param period perioda opakovani ukolu v milisekundach
     * @return nejblizsi termin dokonceni ze zakladniho terminu a periody
     */
    private long calcHotTimeByMillisPeriod(long hotTime, long nowTime, long period) {
        if (period == 0) {
            return hotTime;
        } else {
            long x = (long) Math.ceil((nowTime - hotTime) / (double) period);
            return hotTime + x * period;
        }
    }

    /**
     * Vypocte nejblizsi termin dokonceni ze zakladniho terminu a mesicni
     * periody
     *
     * @param hotTime aktualni cas
     * @return nejblizsi termin dokonceni ze zakladniho terminu a mesicni
     * periody
     */
    private long calcHotTimeByMonthPeriod(long hotTime) {
        // kalendar pro aktualni datum
        Calendar calNow = Calendar.getInstance();
        // kalendar pro zakladni termin
        Calendar calHot = Calendar.getInstance();
        calHot.setTimeInMillis(hotTime);
        // inkrementace zakladniho terminu po mesicich, tak aby prekrocil aktualni datum
        while (calNow.after(calHot)) {
            calNow.roll(Calendar.MONTH, true);
        }
        return calNow.getTimeInMillis();
    }

}
