package cz.iivos.todo.database;

import com.vaadin.shared.ui.colorpicker.Color;
import cz.iivos.todo.enums.TaskRepetitions;
import cz.iivos.todo.enums.TaskWarnings;
import cz.iivos.todo.model.Category;
import cz.iivos.todo.model.Task;
import cz.iivos.todo.model.User;
import cz.iivos.todo.security.SecurityService;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.log4j.Logger;

/**
 * Třída pro práci s databází.
 *
 * @author Emil Tomi
 */
public class DBAdapter {

    private final Logger logger = Logger.getLogger(DBAdapter.class);
    /**
     * Plně kvalifikované jméno ovladače.
     */
    private final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    /**
     * Spojení s databází.
     */
    private static Connection conn;
    /**
     * Statement pro provádění SQL dotazů.
     */
    Statement statement = null;
    /**
     * Výsledek dotazu.
     */
    ResultSet resultSet = null;
    /**
     * Příhlašovací údaje do databáze a údaje administrátora.
     */
    private ResourceBundle properties;
    /**
     * Bezpečnostní informační služba.
     */
    private SecurityService security;

    /**
     * Konstruktor, který vytvoření spojení s databází. *
     */
    public DBAdapter() {
	// nastavení lokálního prostředí
	Locale locale = new Locale("cz", "CZ");
	// načtení properties
	properties = ResourceBundle.getBundle("dbconnection_local",
		locale);
	security = new SecurityService();

	try {
	    // načtení ovladače databáze
	    Class.forName(JDBC_DRIVER);

	    // vytvoření připojení k databázi
	    conn = DriverManager
		    .getConnection(
			    "jdbc:mysql://"
			    + properties.getString("url")
			    + "/"
			    + properties.getString("dbname")
			    + "?useUnicode=true&characterEncoding=UTF8&zeroDateTimeBehavior=convertToNull",
			    properties.getString("user"),
			    properties.getString("password"));
	} catch (ClassNotFoundException | SQLException ex) {
	    logger.warn(ex.getLocalizedMessage());
	    throw new RuntimeException(ex);
	}
    }

    /**
     * Matoda, která vrací uživatele s hledaným emailem. 
     * @param email email hledaného uživatele.
     * @param deleted příznak, zda se má vyhledat i smazaný uživatel
     * @return vrací hledaného uživatele. *
     */
    public User getUserByEmail(String email, boolean deleted) {

	User user;
	String s = "SELECT id_lur, first_name, last_name, email, isAdmin FROM LOGIN_USER WHERE email = ? AND deleted = ?";
	try {
	    PreparedStatement st = conn.prepareStatement(s);
	    st.setString(1, email);
	    st.setBoolean(2, deleted);
	    ResultSet rs = st.executeQuery();

	    try {
		if (rs != null) {

		    if (rs.next()) {
			user = new User();
			user.setId(rs.getLong(1));
			user.setName(rs.getString(2));
			user.setSurname(rs.getString(3));
			user.setEmail(rs.getString(4));
			user.setAdmin(rs.getBoolean(5));
			return user;
		    }
		}
	    } finally {
		st.close();
	    }

	} catch (SQLException ex) {
	    logger.warn(ex.getLocalizedMessage());
	    throw new RuntimeException(ex);
	}
	return null;
    }
    
    /**
     * Vrací uživatele podle id. Vrací smazané a s nepotvrzenou registrací.
     * @param idUser id uživatele
     * @return uživatele
     */
    public User getUserById(Long idUser) {
	User user;
	String s = "SELECT id_lur, first_name, last_name, email, isAdmin FROM LOGIN_USER WHERE id_lur = ?";
	try {
	    PreparedStatement st = conn.prepareStatement(s);
	    st.setLong(1, idUser);
	    ResultSet rs = st.executeQuery();

	    try {
		if (rs != null) {

		    if (rs.next()) {
			user = new User();
			user.setId(rs.getLong(1));
			user.setName(rs.getString(2));
			user.setSurname(rs.getString(3));
			user.setEmail(rs.getString(4));
			user.setAdmin(rs.getBoolean(5));
			return user;
		    }
		}
	    } finally {
		st.close();
	    }

	} catch (SQLException ex) {
	    logger.warn(ex.getLocalizedMessage());
	    throw new RuntimeException(ex);
	}
	return null;
    }

    /**
     * Metoda, která uloží registrujícího se uživatele.
     *
     * @param user uživatel, který se registruje.
     * @return vrací registrovaného uživatele.
     */
    public User saveUser(User user) {
	User savedUser = getUserByEmail(user.getEmail(), true);
	if (savedUser != null) {
	    return savedUser;
	}
	try {
	    PreparedStatement st = conn
		    .prepareStatement("INSERT INTO LOGIN_USER (first_name, last_name, email, password, deleted) values(?,?,?,?,?)");
	    st.setString(1, user.getName());
	    st.setString(2, user.getSurname());
	    st.setString(3, user.getEmail());
	    st.setBytes(4, security.getEncryptedPassword(user.getPassword()));
	    st.setBoolean(5, true);
	    st.executeUpdate();
	    st.close();
	    return getUserByEmail(user.getEmail(), true);
	} catch (SQLException ex) {
	    logger.warn(ex.getLocalizedMessage());
	    throw new RuntimeException(ex);
	}
    }

    /**
     * Metada, která změní údaje v profilu uživatele.
     *
     * @param user uživatel, který chce změnit vybrané údaje.
     * @return vrací uživatele s aktualizovaným profilem.
     */
    public User modifyUser(User user) {
	try {
	    PreparedStatement st = conn
		    .prepareStatement("UPDATE LOGIN_USER SET first_name = ?,"
			    + " last_name = ?,"
			    + " isAdmin = ?"
			    + " WHERE id_lur = ?");
	    st.setString(1, user.getName());
	    st.setString(2, user.getSurname());
	    st.setBoolean(3, user.isAdmin());
	    st.setLong(4, user.getId());
	    st.executeUpdate();
	    st.close();
	    return getUserByEmail(user.getEmail(), false);
	} catch (SQLException ex) {
	    logger.warn(ex.getLocalizedMessage());
	    throw new RuntimeException(ex);
	}
    }

    /**
     * Metoda, která změní heslo uživateli.
     *
     * @param user uživatel, který si chce změnit heslo.
     * @return vrací uživatele se změněným heslem.
     */
    public User modifyPassword(User user) {
	try {
	    PreparedStatement st = conn
		    .prepareStatement("UPDATE LOGIN_USER SET password = ? WHERE id_lur = ?");
	    st.setBytes(1, security.getEncryptedPassword(user.getPassword()));
	    st.setLong(2, user.getId());
	    st.executeUpdate();
	    st.close();
	    return getUserByEmail(user.getEmail(), false);
	} catch (SQLException ex) {
	    logger.warn(ex.getLocalizedMessage());
	    throw new RuntimeException(ex);
	}
    }

    /**
     * Metoda, která smaže uživatele a jeho úkoly.
     *
     * @param selected mazaný uživatel.
     */
    public void deleteUser(User selected) {
	try {
	    PreparedStatement st = conn
		    .prepareStatement("UPDATE TASK_CATEGORY SET deleted = 1 WHERE id_lur = ?");
	    st.setLong(1, selected.getId());
	    st.executeUpdate();
	    st = conn
		    .prepareStatement("UPDATE TASK SET deleted = 1 WHERE id_lur = ?");
	    st.setLong(1, selected.getId());
	    st.executeUpdate();
	    st = conn
		    .prepareStatement("UPDATE LOGIN_USER SET deleted = 1 WHERE id_lur = ?");
	    st.setLong(1, selected.getId());
	    st.executeUpdate();
	    st.close();
	} catch (SQLException ex) {
	    logger.warn(ex.getLocalizedMessage());
	    throw new RuntimeException(ex);
	}
    }

    /**
     * Metoda, která vrací seznam zaregistrovaných uživatelů.
     *
     * @return users list uživatelů.
     */
    public List<User> getAllUsers() {
	List<User> users = new ArrayList();
	String s = "SELECT id_lur, first_name, last_name, email, isAdmin FROM LOGIN_USER WHERE deleted = 0";
	try {
	    Statement st = conn.createStatement();
	    ResultSet rs = st.executeQuery(s);
	    try {
		if (rs != null) {
		    while (rs.next()) {
			User user = new User();
			user.setId(rs.getLong(1));
			user.setName(rs.getString(2));
			user.setSurname(rs.getString(3));
			user.setEmail(rs.getString(4));
			user.setAdmin(rs.getBoolean(5));
			users.add(user);
		    }
		}
	    } finally {
		st.close();
	    }
	} catch (SQLException ex) {
	    logger.warn(ex.getLocalizedMessage());
	    throw new RuntimeException(ex);
	}
	return users;
    }

    /**
     * Metoda, která vrací všechny úkoly daného uživatele.
     *
     * @param user uživatel u kterého hledáme úkoly.
     * @return vrací seznam nalezených úkolů.
     */
    public List<Task> findAllTasksByUser(User user) {
	try {
	    String id = Long.toString(user.getId());
	    List<Task> tasks = new ArrayList<>();
	    String s = "SELECT id_tak, title, description, repetition_period, warning_period, completed,"
		    + "creation_date, completion_date, deadline FROM TASK WHERE deleted = 0 AND id_lur = ?";
	    PreparedStatement st = conn.prepareStatement(s);
	    st.setInt(1, Integer.parseInt(user.getId().toString()));
	    ResultSet rs = st.executeQuery();

	    if (rs != null) {
		while (rs.next()) {
		    Task task = new Task();
		    task.setId_tak(rs.getLong(1));
		    task.setTitle(rs.getString(2));
		    task.setDescription(rs.getString(3));
		    //kvuli zavedeni enum pro period a warning:
		    task.setRepetition_period(TaskRepetitions.values()[rs.getShort(4)]);
		    task.setWarning_period(TaskWarnings.values()[rs.getShort(5)]);

		    task.setCompleted(rs.getBoolean(6));
		    task.setCreation_date(rs.getTimestamp(7));
		    task.setCompletion_date(rs.getTimestamp(8));
		    task.setDeadline(rs.getTimestamp(9));
		    tasks.add(task);
		}
	    }
	    st.close();
	    return tasks;
	} catch (SQLException ex) {
	    logger.warn(ex.getLocalizedMessage());
	    throw new RuntimeException(ex);
	}
    }

    /**
     * Metoda, která vrací nedokončené úkoly daného uživatele.
     *
     * @param user uživatel u kterého hledáme nedokončené úkoly.
     * @return vrací seznam nalezených nedokončených úkolů.
     */
    public List<Task> findActualTasksByUser(User user) {
	try {
	    List<Task> tasks = new ArrayList<>();
	    String s = "SELECT id_tak, title, description, repetition_period, warning_period, completed,"
		    + "creation_date, completion_date, deadline FROM TASK WHERE deleted = 0 AND completed = 0 AND id_lur = ?";
	    PreparedStatement st = conn.prepareStatement(s);
	    st.setInt(1, Integer.parseInt(user.getId().toString()));
	    ResultSet rs = st.executeQuery();

	    if (rs != null) {
		while (rs.next()) {
		    Task task = new Task();
		    task.setId_tak(rs.getLong(1));
		    task.setTitle(rs.getString(2));
		    task.setDescription(rs.getString(3));
		    //kvuli zavedeni enum pro period a warning:
		    task.setRepetition_period(TaskRepetitions.values()[rs.getShort(4)]);
		    task.setWarning_period(TaskWarnings.values()[rs.getShort(5)]);

		    task.setCompleted(rs.getBoolean(6));
		    task.setCreation_date(rs.getTimestamp(7));
		    task.setCompletion_date(rs.getTimestamp(8));
		    task.setDeadline(rs.getTimestamp(9));
		    tasks.add(task);
		}
	    }
	    st.close();
	    return tasks;
	} catch (SQLException ex) {
	    logger.warn(ex.getLocalizedMessage());
	    throw new RuntimeException(ex);
	}
    }

    /**
     * Metoda pro vytvoření nového úkolu.
     *
     * @param task úkol, který se má uložit do databáze.
     * @param user uživatl, který chce daný úkol vytvořit.
     */
    public void createTask(Task task, User user) {
	try {

	    Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    String createDate = formatter.format(task.getCreation_date());
	    String completitionDate = formatter.format(task.getCompleted());
	    String deadline = formatter.format(task.getCompletion_date());

	    PreparedStatement st = conn
		    .prepareStatement("INSERT INTO TASK (title, description, repetition_period, warning_period, creation_date,"
			    + "completion_date, deadline, id_lur) values(?,?,?,?,?,?,?,?)");
	    st.setString(1, task.getTitle());
	    st.setString(2, task.getDescription());

	    st.setShort(3, (short) task.getRepetition_period().ordinal());
	    st.setShort(4, (short) (task.getWarning_period().ordinal()));

	    st.setString(5, createDate);
	    st.setString(6, completitionDate);
	    st.setString(7, deadline);
	    st.setString(8, Long.toString(user.getId()));
	    st.executeUpdate();
	    st.close();

	} catch (SQLException ex) {
	    logger.warn(ex.getLocalizedMessage());
	    throw new RuntimeException(ex);
	}
    }

    /**
     * Metoda, která smaže úkol daného užiatele.
     *
     * @param task úkol, který má být smazán.
     */
    public void delete(Task task) {
	try {
	    PreparedStatement st = conn
		    .prepareStatement("UPDATE TASK SET deleted = 1 WHERE id_tak = ?");
	    st.setInt(1, Integer.parseInt(task.getId_tak().toString()));
	    st.executeUpdate();
	    st.close();
	} catch (SQLException ex) {
	    logger.warn(ex.getLocalizedMessage());
	    throw new RuntimeException(ex);
	}
    }

    /**
     * Metoda, která vrací všechny kategorie daného uživatele.
     *
     * @param user uživatel u kterého hledáme kategorie.
     * @return vrací seznam nalezených kategorií.
     */
    public List<Category> findAllCategoriesByUser(User user) {
	try {
	    List<Category> categories = new ArrayList<>();
	    String s = "SELECT id_tcy, title, backgroundColor, textColor FROM TASK_CATEGORY WHERE id_lur = ? AND deleted = 0";
	    try (PreparedStatement st = conn.prepareStatement(s)) {
		st.setInt(1, Integer.parseInt(user.getId().toString()));
		ResultSet rs = st.executeQuery();

		if (rs != null) {
		    while (rs.next()) {
			Category category = new Category();
			category.setId(rs.getLong(1));
			category.setTitle(rs.getString(2));
			category.setBackColor(new Color(rs.getInt(3)));
			category.setTextColor(new Color(rs.getInt(4)));
			categories.add(category);
		    }
		}
	    }
	    return categories;
	} catch (SQLException ex) {
	    logger.warn(ex.getLocalizedMessage());
	    throw new RuntimeException(ex);
	}
    }

    /**
     * Metoda, která vytvoří novou kategorii.
     *
     * @param category kategorie, která se má uložit.
     */
    public void addCategory(Category category) {
	try {
	    try (PreparedStatement st = conn
		    .prepareStatement("INSERT INTO TASK_CATEGORY (title, backgroundColor, textColor, id_lur) values (?,?,?,?)")) {
		st.setString(1, category.getTitle());
		st.setInt(2, category.getBackColor().getRGB());
		st.setInt(3, category.getTextColor().getRGB());
		st.setLong(4, category.getUserId());
		st.executeUpdate();
	    }
	} catch (SQLException ex) {
	    logger.warn(ex.getLocalizedMessage());
	    throw new RuntimeException(ex);
	}
    }

    /**
     * Metoda, která aktualizuje kategorii.
     *
     * @param category kategorie, která je aktualizována.
     */
    public void modifyCategory(Category category) {
	try {
	    try (PreparedStatement st = conn
		    .prepareStatement("UPDATE TASK_CATEGORY SET title = ?, backgroundColor = ?, textColor = ? WHERE id_tcy = ?")) {
		st.setString(1, category.getTitle());
		st.setInt(2, category.getBackColor().getRGB());
		st.setInt(3, category.getTextColor().getRGB());
		st.setLong(4, category.getId());
		st.executeUpdate();
	    }
	} catch (SQLException ex) {
	    logger.warn(ex.getLocalizedMessage());
	    throw new RuntimeException(ex);
	}
    }

    /**
     * Metoda, která maže kategorii uživatele.
     *
     * @param category kategorie, která má být smazána.
     */
    public void deleteCategory(Category category) {
	Long id = category.getId();
	try {
	    PreparedStatement st = conn
		    .prepareStatement("UPDATE TASK_CATEGORY SET deleted = 1 WHERE id_tcy = ?");
	    st.setLong(1, id);
	    st.executeUpdate();
	    st = conn
		    .prepareStatement("UPDATE TASK SET id_tcy = NULL WHERE id_tcy = ?");
	    st.setLong(1, id);
	    st.executeUpdate();
	    st.close();
	} catch (SQLException ex) {
	    logger.warn(ex.getLocalizedMessage());
	    throw new RuntimeException(ex);
	}
    }

    /**
     * Metoda, která ověří zda existuje administrátor. Pokud ne, metoda
     * administrátora vytvoří.
     */
    public void checkAdmin() {
	String email = properties.getString("adminEmail");
	String password = properties.getString("adminPassword");
	byte[] passwordHash = getPasswordHashByEmail(email);
	
	User admin = getUserByEmail(email, false);
	boolean exists = false;
	if (admin != null) {
	    if (security.matchPassword(password, passwordHash)) {
		exists = true;
	    }
	}

	if (!exists) {
	    try {
		PreparedStatement st = conn
			.prepareStatement("INSERT INTO LOGIN_USER (first_name, last_name, email, password, isAdmin) values('admin','admin',?,?, 1)");
		st.setString(1, properties.getString("adminEmail"));
		st.setBytes(2, security.getEncryptedPassword(password));
		st.executeUpdate();
		st.close();
		admin = getUserByEmail(email, true);
		confirmUser(admin.getId());
	    } catch (SQLException ex) {
		logger.warn(ex.getLocalizedMessage());
		throw new RuntimeException(ex);
	    }
	}
    }

    /**
     * Vrací hash hesla uživatele pro autentizaci.
     *
     * @param email
     * @return vrací hash hesla podle e-mailu uživatele
     */
    public byte[] getPasswordHashByEmail(String email) {

	byte[] hash = null;
	try {
	    PreparedStatement st = conn
		    .prepareStatement("SELECT password FROM LOGIN_USER WHERE email = ?");
	    st.setString(1, email);
	    ResultSet result = st.executeQuery();

	    if (result.next()) {
		hash = result.getBytes("password");
	    }
	} catch (SQLException ex) {
	    logger.warn(ex.getLocalizedMessage());
	    throw new RuntimeException(ex);
	}
	return hash;
    }

    public void saveTokenForNewUser(User savedUser, String token) {
	String savedToken = getTokenForUser(savedUser.getId());
	if (savedToken != null) {
	    deleteTokenForUser(savedUser.getId());
	}
	try {
		PreparedStatement st = conn
			.prepareStatement("INSERT INTO LOGIN_USER_TOKEN (id_lur, token) values(?,?)");
		st.setLong(1, savedUser.getId());
		st.setString(2, token);
		st.executeUpdate();
		st.close();
	    } catch (SQLException ex) {
		logger.warn(ex.getLocalizedMessage());
		throw new RuntimeException(ex);
	    }
    }
    
    public String getTokenForUser(long idUser) {
	String token = null;
	try {
		PreparedStatement st = conn
			.prepareStatement("SELECT token FROM LOGIN_USER_TOKEN WHERE id_lur = ?");
		st.setLong(1, idUser);
		ResultSet result = st.executeQuery();
		if (result.next()) {
		    token = result.getString("token");
		}
		st.close();
	    } catch (SQLException ex) {
		logger.warn(ex.getLocalizedMessage());
		throw new RuntimeException(ex);
	    }
	return token;
    }
    
    public void deleteTokenForUser(Long idUser) {
	try {
		PreparedStatement st = conn
			.prepareStatement("DELETE FROM LOGIN_USER_TOKEN WHERE id_lur = ?");
		st.setLong(1, idUser);
		st.executeUpdate();
		st.close();
	    } catch (SQLException ex) {
		logger.warn(ex.getLocalizedMessage());
		throw new RuntimeException(ex);
	    }
    }

    public void confirmUser(Long idUser) {
	try {
	    PreparedStatement st = conn
		    .prepareStatement("UPDATE LOGIN_USER SET deleted = 0 WHERE id_lur = ?");
	    st.setLong(1, idUser);
	    st.executeUpdate();
	    st.close();
	    deleteTokenForUser(idUser);
	} catch (SQLException ex) {
	    logger.warn(ex.getLocalizedMessage());
	    throw new RuntimeException(ex);
	}
    }

    
}
