package cz.iivos.todo.security;

import com.vaadin.server.VaadinSession;
import cz.iivos.todo.model.User;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Bezpečnostní Informační Služba.
 *
 * @author lukas
 */
public class SecurityService {

    private MessageDigest md;
    
    private SecureRandom random;

    public SecurityService() {
	try {
	    md = MessageDigest.getInstance("MD5");
	} catch (NoSuchAlgorithmException ex) {
	    throw new RuntimeException(ex);
	}
	random = new SecureRandom();
    }

    /**
     * Přidá přihlášeného uživatele do session.
     *
     * @param user uživatel
     * 
     */
    public void login(User user) {
	VaadinSession.getCurrent().setAttribute(User.class, user);
    }

    /**
     * Odstraní uživatele ze session.
     */
    public void logout() {
	VaadinSession.getCurrent().setAttribute(User.class, null);
    }

    /**
     * Vrátí aktuálně přihlášeného uživatele, uloženého v session.
     *
     * @return aktuálně přihlášeného uživatele
     */
    public User getCurrentUser() {
	return VaadinSession.getCurrent().getAttribute(User.class);
    }

    /**
     * Porovná hesla.
     *
     * @param rawPassword heslo v podobě plain text
     * @param hashPassword hash hesla
     * @return TRUE, pokud jsou hesla stejná
     */
    public boolean matchPassword(String rawPassword, byte[] hashPassword) {
	if (hashPassword == null || rawPassword == null) {
	    return false;
	}
	return MessageDigest.isEqual(hashPassword, getEncryptedPassword(rawPassword));
    }

    /**
     * Vrátí hash hesla.
     *
     * @param password heslo, ze kterého bude vytvořen hash.
     * @return hash hesla
     */
    public byte[] getEncryptedPassword(String password) {
	try {
	    String saltedPassword = password.toUpperCase() + password;
	    md.update(saltedPassword.getBytes("UTF-8"));
	    return md.digest();
	} catch (UnsupportedEncodingException ex) {
	    throw new RuntimeException(ex);
	}
    }

    /**
     * Vrací náhodný token pro potvrzení registrace uživatele.
     * @return token pro potvrzení uživatele
     */
    public String getConfirmToken() {
	return new BigInteger(130, random).toString(32);
    }
}
