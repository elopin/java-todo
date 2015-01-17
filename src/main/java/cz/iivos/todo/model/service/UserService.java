package cz.iivos.todo.model.service;

import cz.iivos.todo.model.User;
import java.util.List;

/**
 * Služba pro práci s uživatelem.
 * @author lukas
 */
public interface UserService {
    
    /**
     * Vrátí uživatele podle e-mailu.
     * @param email email uživatele
     * @deleted příznak, zda se má vyhledat i smazaný uživatel
     * @return instanci uživatele, pokud v databázi existuje, jinak vrací null
     */
    User getUserByEmail(String email, boolean deleted);
    
    /**
     * Uloží nového uživatele. 
     * @param user uživatel k uložení do databáze
     * @return aktuální verzi uživatele
     */
    User saveUser(User user);
    
    /**
     * Změní údaje v profilu uživatele.
     * @param user uživatel, který chce změnit vybrané údaje.
     * @return aktuální verzi uživatele.
     */
    User modifyUser(User user);
    
    /**
     * Změní heslo uživatele.
     * @param user uživatel, který chce změnit heslo.
     * @return aktuální verzi uživatele.
     */
    User modifyPassword(User user);
    
    /**
     * Vrátí seznam všech uživatelů.
     * @return seznam vrácených uživatelů.
     */
    List<User> getAllUsers();
    
    /**
     * Smaže uživatele a jeho úkoly.
     * @param selected mazaný uživatel.     * 
     */
    public void deleteUser(User selected);    

    /**
     * Vrací hash hesla uživatele podle e-mailu.
     * @param email email uživatele
     * @return hash hesla
     */
    public byte[] getEncryptedPasswordByEmail(String email);

    /**
     * Uloží token, s jehož pomocí uživatel potvrdí registraci e-mailu.
     * @param savedUser uložený, ale nepotvrzený uživatel
     * @param token token, pomocí kterého bude potvrzena registrace uživatele
     */
    public void saveConfirmTokenForUser(User savedUser, String token);

    /**
     * Vrátí uživatele podle id.
     * @param idUser id uživatele
     * @return uživatele nalezeného podle id
     */
    public User getUserById(Long idUser);

    /**
     * Potvrdí registraci uživatele. Porovná uložený token s tokenem z URL.
     * @param user uživatel
     * @param token potvrzovací token
     * @return TRUE, pokud je uživatel úspěšně potvrzen
     */
    public boolean confirmUser(User user, String token);
}
