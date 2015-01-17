package cz.iivos.todo.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import cz.iivos.todo.components.UserForm;
import cz.iivos.todo.model.User;
import cz.iivos.todo.model.service.EmailService;
import cz.iivos.todo.model.service.EmailServiceMock;
import cz.iivos.todo.model.service.UserService;
import cz.iivos.todo.model.service.UserServiceImpl;
import cz.iivos.todo.security.SecurityService;

@SuppressWarnings("serial")
public class RegisterView extends VerticalLayout implements View {

    private UserService userService;
    private SecurityService securityService;
    private EmailService emailService;

    private UserForm userForm;

    private Navigation navigation;

    /**
     * Vytvoří instanci třídy RegisterView a inicializuje komponenty
     */
    public RegisterView(final Navigation navigation) {
	this.navigation = navigation;
	securityService = new SecurityService();
	userService = new UserServiceImpl();
	emailService = new EmailServiceMock();
	userForm = new UserForm();

	final Button btRegister = new Button("Vytvořit účet", new Button.ClickListener() {

	    @Override
	    public void buttonClick(ClickEvent event) {

		if (userForm.save()) {
		    if (userForm.confirmPassword()) {
			User savedUser = userService.getUserByEmail(userForm.getUser().getEmail(), false);
			if (savedUser == null) {
			    savedUser = userService.saveUser(userForm.getUser());
			    String token = securityService.getConfirmToken();
			    userService.saveConfirmTokenForUser(savedUser, token);
			    String link = Page.getCurrent().getLocation().toString();
			    link = link.replaceAll("(#|\\?).*", "");
			    StringBuilder sb = new StringBuilder();
			    sb.append(link).append("?").append("user=").append(savedUser.getId()).append("&").append("token=").append(token);
			    emailService.sendEmailConfirmationLink(savedUser, sb.toString());
			    navigation.navigateTo(Navigation.ViewId.LOGIN, null);
			    Notification.show("Na uvedený e-mail byl odeslán odkaz, kterým dokončíte registraci.");
			} else {
			    Notification.show("Uživatel s uvedeným e-mailem již existuje!", Notification.Type.ERROR_MESSAGE);
			}
		    } else {
			Notification.show("Chyba v kontrole hesla!", Notification.Type.ERROR_MESSAGE);
		    }
		}
	    }

	});
	btRegister.setStyleName(ValoTheme.BUTTON_FRIENDLY);
	btRegister.setSizeFull();

	VerticalLayout vlForm = new VerticalLayout(userForm, btRegister);
	vlForm.setSpacing(true);
	vlForm.setMargin(true);

	this.addComponent(Tools.createPanelCaption("Registrace nového uživatele Úkolníčku"));
	this.addComponent(new Panel(vlForm));
	this.setWidth(400, Unit.PIXELS);
    }

    @Override
    public void enter(ViewChangeEvent event) {
	userForm.setUser(new User());
    }
}
