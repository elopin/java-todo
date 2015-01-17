package cz.iivos.todo.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import cz.iivos.todo.components.UserForm;
import cz.iivos.todo.components.YesNoWindow;
import cz.iivos.todo.model.User;
import cz.iivos.todo.model.service.UserService;
import cz.iivos.todo.model.service.UserServiceImpl;
import cz.iivos.todo.security.SecurityService;
import cz.iivos.todo.views.Navigation.ViewId;

/**
 * View s profilem uživatele. Zde lze provádět změny v osobních údajích
 * uživatelů nebo změnu hesla.
 *
 * @author lukas
 */
@SuppressWarnings("serial")
public class ProfileView extends VerticalLayout implements View {

    private SecurityService securityService;
    private UserService userService;
    private Navigation navigation;

    private User user;
    private UserForm userForm;

    public ProfileView(Navigation navigation) {
	this.navigation = navigation;
	securityService = new SecurityService();
	userService = new UserServiceImpl();
	userForm = new UserForm();
    }

    @Override
    public void enter(ViewChangeEvent event) {
	user = securityService.getCurrentUser();
	if (event.getParameters().isEmpty()) {
	    setUser(user);
	} else {
	    setUser(userService.getUserByEmail(event.getParameters(), false));
	}
	initLayout();
    }

    public void initLayout() {
	removeAllComponents();

	//inicializace horního panelu
	Tools.initApplicationLayout(this, navigation, ViewId.PROFILE, user);

	Button btPersonal = new Button("Změnit", (event) -> {

	    YesNoWindow confirmDialog = new YesNoWindow("Uložit změny", "Opravdu chcete uložit změny?", (action) -> {
		if (userForm.save()) {
		    userService.modifyUser(userForm.getUser());
		    if (userForm.passwordChanged()) {
			if (userForm.confirmPassword()) {
			    userService.modifyPassword(userForm.getUser());
			    Notification.show("Heslo bylo změněno!");
			} else {
			    Notification.show("Nastala chyba při pokusu o změnu hesla!", Notification.Type.ERROR_MESSAGE);
			}
		    }
		}
	    });
	    UI.getCurrent().addWindow(confirmDialog);
	});
	btPersonal.setStyleName(ValoTheme.BUTTON_TINY);
	btPersonal.addStyleName(ValoTheme.BUTTON_PRIMARY);

	this.addComponent(userForm);
	setExpandRatio(userForm, 4.0f);
	this.addComponent(btPersonal);
	setExpandRatio(btPersonal, 0.5f);

    }

    /**
     * Nastaví do view uživatele.
     *
     * @param u uživatel zobrazený ve formuláři
     */
    public void setUser(User u) {
	userForm.setUser(u);
	userForm.setAdmin(securityService.getCurrentUser().isAdmin());
	userForm.setEmailReadOnly(true);
	userForm.setAdminCheckEnabled(!securityService.getCurrentUser().getId().equals(u.getId()));
    }
}
