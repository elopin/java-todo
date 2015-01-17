package cz.iivos.todo.model.service;

import cz.iivos.todo.database.DBAdapter;
import cz.iivos.todo.model.User;
import cz.iivos.todo.security.SecurityService;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Implementace služby pro práci s uživatelem.
 *
 * @author lukas
 */
public class UserServiceImpl implements UserService {

    private Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

    private DBAdapter dbAdapter;

    /**
     * BIS.
     */
    private SecurityService securityService;

    public UserServiceImpl() {
	securityService = new SecurityService();
	dbAdapter = new DBAdapter();
    }

    @Override
    public User getUserByEmail(String email, boolean deleted) {
	return dbAdapter.getUserByEmail(email, deleted);
    }

    @Override
    public User saveUser(User user) {
	return dbAdapter.saveUser(user);
    }

    @Override
    public User modifyUser(User user) {
	return dbAdapter.modifyUser(user);
    }

    @Override
    public User modifyPassword(User user) {
	return dbAdapter.modifyPassword(user);
    }

    @Override
    public List<User> getAllUsers() {
	return dbAdapter.getAllUsers();
    }

    @Override
    public void deleteUser(User selected) {
	dbAdapter.deleteUser(selected);
    }

    @Override
    public byte[] getEncryptedPasswordByEmail(String email) {
	return dbAdapter.getPasswordHashByEmail(email);
    }

    @Override
    public void saveConfirmTokenForUser(User savedUser, String token) {
	dbAdapter.saveTokenForNewUser(savedUser, token);

    }

    @Override
    public User getUserById(Long idUser) {
	return dbAdapter.getUserById(idUser);
    }

    @Override
    public boolean confirmUser(User user, String token) {
	String savedToken = dbAdapter.getTokenForUser(user.getId());
	if (savedToken != null) {
	    if (savedToken.equals(token)) {
		dbAdapter.confirmUser(user.getId());
		return true;
	    }
	}
	return false;
    }
}
