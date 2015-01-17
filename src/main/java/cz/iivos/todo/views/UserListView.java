package cz.iivos.todo.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import cz.iivos.todo.components.YesNoWindow;
import cz.iivos.todo.model.User;
import cz.iivos.todo.model.service.UserService;
import cz.iivos.todo.model.service.UserServiceImpl;
import cz.iivos.todo.security.SecurityService;
import cz.iivos.todo.views.Navigation.ViewId;
import java.util.List;

/**
 *
 * @author lukas
 */
public class UserListView extends VerticalLayout implements View {

    private SecurityService securityService;

    private UserService userService;

    private Navigation navigation;

    private User user;

    private HorizontalLayout mainLayout;
    private VerticalLayout buttonLayout;

    private VerticalLayout tableLayout;
    private Table table;

    public UserListView(Navigation navigation) {
        this.navigation = navigation;
        this.securityService = new SecurityService();
        this.userService = new UserServiceImpl();

        mainLayout = new HorizontalLayout();
        mainLayout.setSizeFull();

	tableLayout = new VerticalLayout();
	tableLayout.setSizeFull();
        table = new Table();
	table.setSizeFull();

        buttonLayout = new VerticalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true);

    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        user = securityService.getCurrentUser();
        if (user != null) {
            initLayout();
            setUsers();
        } else {
            navigation.navigateTo(ViewId.LOGIN, null);
        }
    }

    private void initLayout() {
        removeAllComponents();
        mainLayout.removeAllComponents();
        buttonLayout.removeAllComponents();

        Tools.initApplicationLayout(this, navigation, ViewId.USERLIST, user);
        addComponent(mainLayout);
        setExpandRatio(mainLayout, 1.0f);

        Button editUser = new Button("Upravit", (event) -> {
            User selected = (User) event.getButton().getData();
            navigation.navigateTo(ViewId.PROFILE, selected.getEmail());
        });
        editUser.setEnabled(false);
        editUser.setSizeFull();
        editUser.setStyleName(ValoTheme.BUTTON_TINY);
        editUser.addStyleName(ValoTheme.BUTTON_PRIMARY);
        buttonLayout.addComponent(editUser);

        Button deleteUser = new Button("Odebrat", (event) -> {
            User selected = (User) event.getButton().getData();
            YesNoWindow confirmDialog = new YesNoWindow("Odebrání uživatele", "Opravdu chcete odebrat uživatele?", (action) -> {
                userService.deleteUser(selected);
                setUsers();
            });
            UI.getCurrent().addWindow(confirmDialog);
            Notification.show("Odebírám uživatele: " + selected.getDisplayName());
        });
        deleteUser.setEnabled(false);
        deleteUser.setSizeFull();
        deleteUser.setStyleName(ValoTheme.BUTTON_TINY);
        deleteUser.addStyleName(ValoTheme.BUTTON_DANGER);
        buttonLayout.addComponent(deleteUser);

        table.addContainerProperty("name", String.class, null, "Jméno a příjmení", null, Table.Align.LEFT);
        table.addContainerProperty("email", String.class, null, "E-mail", null, Table.Align.LEFT);
        table.addItemClickListener((event) -> {
            editUser.setEnabled(false);
            deleteUser.setEnabled(false);
            User selected = (User) event.getItemId();
            if (!selected.getId().equals(securityService.getCurrentUser().getId())) {
                deleteUser.setData(selected);
                deleteUser.setEnabled(true);
            }
            editUser.setData(selected);
            editUser.setEnabled(true);
        });
        tableLayout.addComponent(table);
	
        mainLayout.addComponent(tableLayout);
        mainLayout.addComponent(buttonLayout);

        mainLayout.setExpandRatio(tableLayout, 0.65f);
        mainLayout.setExpandRatio(buttonLayout, 0.35f);
    }

    private void setUsers() {
        table.removeAllItems();
        List<User> users = userService.getAllUsers();
        for (User u : users) {
            Object[] item = new Object[2];
            item[0] = u.getDisplayName();
            item[1] = u.getEmail();
            table.addItem(item, u);
        }
        table.setPageLength(table.size());
    }

}
