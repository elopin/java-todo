package cz.iivos.todo.views;

import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import cz.iivos.todo.model.User;
import cz.iivos.todo.model.service.UserService;
import cz.iivos.todo.model.service.UserServiceImpl;
import cz.iivos.todo.security.SecurityService;

import cz.iivos.todo.views.Navigation.ViewId;

/**
 * Třída komponenty pro přihlášení do systému
 */
@SuppressWarnings("serial")
public class LoginView extends VerticalLayout implements View {

        
        private SecurityService securityService;
	
	private UserService userService;
    
	/** Textové pole pro zadani uzivatelskeho emailu */
	private TextField tfEmail;
	
	/** Textové pole pro zadání hesla */
	private PasswordField tfPassword;

	public LoginView(final Navigation navigation) {
	        
	        //inicializace BIS
            
	        securityService = new SecurityService();
		userService = new UserServiceImpl();

		// Vytvareni komponent

		tfEmail = Tools.createFormTextField("Email", true);
		tfPassword = Tools.createFormPasswordField("Heslo", true);

		final Button btLogin = new Button("Přihlásit", new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
			    User user = userService.getUserByEmail(tfEmail.getValue(), false);
			    
			    if (user != null) {
				byte[] userPasshash = userService.getEncryptedPasswordByEmail(user.getEmail());
			        if (securityService.matchPassword(tfPassword.getValue(), userPasshash)) {
				    securityService.login(user);
				    navigation.navigateTo(ViewId.TODOS, null);
			        } else {
				    Notification.show("Neplatné přihlašovací údaje!");
			        }  
			    } else {
				Notification.show("Chyba přihlášení!");
			    }
			}
		});
		btLogin.setStyleName(ValoTheme.BUTTON_FRIENDLY);
                btLogin.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		btLogin.setSizeFull();

		final Button btRegister = new Button("Registrovat", new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				navigation.navigateTo(ViewId.REGISTER, null);
			}
		});
		btRegister.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);

		// Zarovnani komponent na stranku
		
		HorizontalLayout hlButtons = new HorizontalLayout(btRegister, btLogin);
		hlButtons.setSizeFull();
		hlButtons.setSpacing(true);
		hlButtons.setExpandRatio(btLogin, 1.0f);

		VerticalLayout vlForm = new VerticalLayout(tfEmail, tfPassword, hlButtons);
		vlForm.setMargin(true);
		vlForm.setSpacing(true);

		this.addComponent(Tools.createPanelCaption("Přihlášení do Úkolníčku"));
		this.addComponent(new Panel(vlForm));
		this.setWidth(300, Unit.PIXELS);
	}

	@Override
	public void enter(ViewChangeEvent event) {
	    tfPassword.setValue("");
	}

}
