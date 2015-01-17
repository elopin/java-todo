package cz.iivos.todo.model.service;

import java.util.List;

import cz.iivos.todo.enums.RelativeDays;
import cz.iivos.todo.model.Task;
import cz.iivos.todo.model.User;

import java.util.Map;

/**
 * Rozhraní pro práci s úkolem.
 * 
 * @author stefan
 */
public interface TaskService {
	
    /**
     * Vrátí seznam všech úkolů daného uživatele.
     * @param usr uživatel kterého úkoly chceme získat
     * @return seznam všech úkolů daného uživatele
     */
	public List<Task> findAllTasksByUser(User usr);

    /**
     * Vrátí seznam aktuálních(neuzavřených) úkolů daného uživatele.
     * @param usr uživatel kterého úkoly chceme získat
     * @return seznam aktuálních úkolů daného uživatele
     */
	public List<Task> findActualTasksByUser(User usr);

	//public List<Task> findTasksOnDate(User usr, Date dat);

	public List<Task> findTasksOnDate(User usr, RelativeDays mode);
	
	/**
	 * Vyhledá a vrátí všechny úkoly, na které je potřeba upozornit.
	 * @return seznam úkolů
	 */
	public Map<User, List<Task>> findHotTasks();

}
