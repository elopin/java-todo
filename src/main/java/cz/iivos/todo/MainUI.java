package cz.iivos.todo;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import cz.iivos.todo.components.ErrorWindow;
import cz.iivos.todo.model.User;
import cz.iivos.todo.model.service.UserService;
import cz.iivos.todo.model.service.UserServiceImpl;
import cz.iivos.todo.security.SecurityService;
import cz.iivos.todo.views.CategoriesView;
import cz.iivos.todo.views.LoginView;
import cz.iivos.todo.views.Navigation;
import cz.iivos.todo.views.ProfileView;
import cz.iivos.todo.views.RegisterView;
import cz.iivos.todo.views.TodosView;
import cz.iivos.todo.views.UserListView;

@Theme("mytheme")
@SuppressWarnings("serial")
public class MainUI extends UI implements Navigation {

    private Navigator navigator;
    private SecurityService securityService;
    private UserService userService;

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = MainUI.class, widgetset = "cz.iivos.todo.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {
	securityService = new SecurityService();
	userService = new UserServiceImpl();

	//při chybě je zobrazeno dialogové okno
	this.setErrorHandler((com.vaadin.server.ErrorEvent event) -> {
	    UI.getCurrent().addWindow(new ErrorWindow(event.getThrowable()));
	});

	// layout pro vkladani obsahu
	VerticalLayout vlContent = new VerticalLayout();
	vlContent.setSizeUndefined();

	// hlavni layout stranky
	VerticalLayout vlPage = new VerticalLayout(vlContent);
	vlPage.setComponentAlignment(vlContent, Alignment.TOP_CENTER);

	// vlozeni layoutu do uzivatelskeho rozhrani
	setContent(vlPage);

	// inicializace objektu pro navigaci mezi pohledy
	navigator = new Navigator(this.getUI(), vlContent);
	LoginView loginView = new LoginView(this);
	navigator.addView("", loginView);
	navigator.addView(Navigation.ViewId.LOGIN.id, loginView);
	navigator.addView(Navigation.ViewId.REGISTER.id, new RegisterView(this));
	navigator.addView(Navigation.ViewId.TODOS.id, new TodosView(this));
	navigator.addView(Navigation.ViewId.PROFILE.id, new ProfileView(this));
	navigator.addView(Navigation.ViewId.CATEGORIES.id, new CategoriesView(this));
	navigator.addView(Navigation.ViewId.USERLIST.id, new UserListView(this));

	String user = request.getParameter("user");
	String token = request.getParameter("token");
	if (user != null && token != null) {
	    Long idUser = Long.parseLong(user);
	    User savedUser = userService.getUserById(idUser);
	    if (userService.confirmUser(savedUser, token)) {
		securityService.login(savedUser);
		navigateTo(Navigation.ViewId.TODOS, null);
	    }
	}

    }

    @Override
    public void navigateTo(ViewId view, String param) {
	if (param == null) {
	    navigator.navigateTo(view.id);
	} else {
	    navigator.navigateTo(view.id + "/" + param);
	}
    }

}
