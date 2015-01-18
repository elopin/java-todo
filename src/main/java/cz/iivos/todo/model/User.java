package cz.iivos.todo.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Entita uživatele.
 * 
 * @author lukas
 */
public class User implements Serializable {
    
    static String SESSION_USER = "user";
    
    private Long id;
    
    @NotNull(message = "E-mail musí být vyplněn!")
    @Pattern(regexp ="^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.(?:[a-zA-Z]{2,6})$", message = "Zadaný e-mail nemá správný formát!")
    private String email;
    
    @NotNull(message = "Jméno musí být vyplněno!")
    @NotEmpty(message = "Jméno nesmí být prázdné!")
    private String name;
    
    @NotNull(message = "Příjmení musí být vyplněno!")
    @NotEmpty(message = "Příjmení nesmí být prázdné!")
    private String surname;
    
    @NotNull(message = "Heslo musí být vyplněno!")
    @NotEmpty(message = "Heslo nesmí být prázdné!")
    private String password;
    
    private boolean admin;
    
    private transient List<Task> tasks;

    public User() { }
    
    public User(String email, String password) {
	this.email = email;
	this.password = password;
    }

    /**
     * @return the id
     */
    public Long getId() {
	return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
	this.id = id;
    }

    /**
     * @return the email
     */
    public String getEmail() {
	return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
	this.email = email;
    }

    /**
     * @return the name
     */
    public String getName() {
	return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
	this.name = name;
    }

    /**
     * @return the surname
     */
    public String getSurname() {
	return surname;
    }

    /**
     * @param surname the surname to set
     */
    public void setSurname(String surname) {
	this.surname = surname;
    }

    /**
     * @return the password
     */
    public String getPassword() {
	return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
	this.password = password;
    }

    /**
     * @return the admin
     */
    public boolean isAdmin() {
	return admin;
    }

    /**
     * @param admin the admin to set
     */
    public void setAdmin(boolean admin) {
	this.admin = admin;
    }

    /**
     * @return the tasks
     */
    public List<Task> getTasks() {
	return tasks;
    }

    /**
     * @param tasks the tasks to set
     */
    public void setTasks(List<Task> tasks) {
	this.tasks = tasks;
    }
    
    public void addTast(Task task) {
	if (tasks == null) {
	    tasks = new ArrayList<>();
	}
	tasks.add(task);
    }

    public String getDisplayName() {
	return name + " " + surname;
    }
}
