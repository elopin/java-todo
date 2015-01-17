package cz.iivos.todo.components;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import cz.iivos.todo.model.User;
import cz.iivos.todo.views.Tools;
import org.apache.log4j.Logger;

/**
 * Komponenta představující formulář pro data uživatele.
 *
 * @author lukas
 */
public class UserForm extends FormLayout {

    private Logger logger = Logger.getLogger(UserForm.class.getName());

    private BeanFieldGroup bfg;

    /**
     * Textové pole pro zadani jména uživatele
     */
    private TextField tfName;

    /**
     * Textové pole pro zadání příjmení uživatele
     */
    private TextField tfSurname;

    /**
     * Textové pole pro zadání emailu uživatele
     */
    private TextField tfEmail;

    /**
     * Textové pole pro zadání hesla uživatele
     */
    private PasswordField tfPassword1;

    /**
     * Textové pole pro zadání potvrzení hesla uživatele
     */
    private PasswordField tfPassword2;

    /**
     * CheckBox pro určení administrátora.
     */
    private CheckBox chbAdmin;

    /**
     * Příznak, že komponentu používá uživatel s právem administrace.
     */
    private boolean admin;

    /**
     * Konstruktor inicializuje formulářové prvky.
     */
    public UserForm() {

	bfg = new BeanFieldGroup(User.class);
	bfg.setBuffered(true);
	admin = false;

	tfName = Tools.createFormTextField("Jméno", true);
	tfName.setNullRepresentation("");
	tfName.setInputPrompt("Zadejte jméno...");

	tfSurname = Tools.createFormTextField("Příjmení", true);
	tfSurname.setNullRepresentation("");
	tfSurname.setInputPrompt("Zadejte příjmení...");

	tfEmail = Tools.createFormTextField("Email", true);
	tfEmail.setNullRepresentation("");
	tfEmail.setInputPrompt("Zadejte e-mail...");

	tfPassword1 = Tools.createFormPasswordField("Heslo", true);
	tfPassword1.setNullRepresentation("");

	tfPassword2 = Tools.createFormPasswordField("Potvrzení hesla", true);
	tfPassword2.setNullRepresentation("");

	chbAdmin = new CheckBox("Administrátor", false);
	chbAdmin.setVisible(false);

	setMargin(true);
	setSpacing(true);
	addComponent(tfName);
	addComponent(tfSurname);
	addComponent(tfEmail);
	addComponent(tfPassword1);
	addComponent(tfPassword2);
	addComponent(chbAdmin);

	bfg.bind(tfName, "name");
	bfg.bind(tfSurname, "surname");
	bfg.bind(tfEmail, "email");
	bfg.bind(chbAdmin, "admin");
    }

    public void setUser(User user) {
	if (user.getEmail() == null) {
	    bfg.bind(tfPassword1, "password");
	}
	bfg.setItemDataSource(user);
    }

    public User getUser() {
	return (User) bfg.getItemDataSource().getBean();
    }

    /**
     * Nastaví příznak použití komponenty administrátorem.
     *
     * @param a příznak, zda komponentu používá administrátor. Pokud ano, je
     * zviditelněn checkbox pro určení administrátora
     */
    public void setAdmin(boolean a) {
	this.admin = a;
	chbAdmin.setVisible(admin);
    }

    /**
     * Porovná zadané heslo s potvrzením hesla.
     *
     * @return TRUE, pokud je potvrzení hesla v pořádku
     */
    public boolean confirmPassword() {
	if (tfPassword1.getValue().equals(tfPassword2.getValue())) {
	    if (bfg.getField("password") == null) {
		((User) bfg.getItemDataSource().getBean()).setPassword(tfPassword1.getValue());
	    }
	    return true;
	}
	return false;
    }

    /**
     * Vrací TRUE, pokud byl proveden pokus o změnu hesla.
     *
     * @return TRUE, pokud byl proveden pokus o změnu hesla
     */
    public boolean passwordChanged() {
	return !(tfPassword1.getValue() == null || tfPassword1.getValue().isEmpty());
    }

    /**
     * Znemožní změnu e-mailu.
     *
     * @param readOnly příznak, zda má být e-mail editovatelný
     */
    public void setEmailReadOnly(boolean readOnly) {
	tfEmail.setReadOnly(readOnly);
    }

    /**
     * Uloží data z formuláře do beanu. Před uložením provede validaci
     * formuláře.
     * @return TRUE, pokud uložení proběhlo v pořádku
     */
    public boolean save() {
	try {
	    tfPassword2.validate();
	    tfPassword2.setValidationVisible(true);
	    tfName.setValidationVisible(true);
	    tfSurname.setValidationVisible(true);
	    tfEmail.setValidationVisible(true);
	    tfPassword1.setValidationVisible(true);
	    bfg.commit();
	    return true;

	} catch (FieldGroup.CommitException ex) {
	    logger.warn(ex);
	}
	return false;
    }

    /**
     * Zpřístupní zaškrtávací políčko administrátora. Administrátorovi by neměl
     * mít možnost si sám odebrat administrátorství.
     * @param adminCheckEnabled 
     */
    public void setAdminCheckEnabled(boolean adminCheckEnabled) {
	chbAdmin.setEnabled(adminCheckEnabled);
    }
}
