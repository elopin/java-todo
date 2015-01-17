package cz.iivos.todo.views;

import cz.iivos.todo.components.*;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import cz.iivos.todo.components.SilentCheckBox;
import cz.iivos.todo.components.YesNoWindow;
import cz.iivos.todo.database.DBAdapter;
import cz.iivos.todo.database.DoDbConn;
import cz.iivos.todo.dialogs.TaskDlg;
import cz.iivos.todo.enums.RelativeDays;
import cz.iivos.todo.listeners.RefreshTaskLabelListener;
import cz.iivos.todo.listeners.RenewTodoListener;
import cz.iivos.todo.listeners.YesNoWindowListener;
import cz.iivos.todo.model.Category;
import cz.iivos.todo.model.Task;
import cz.iivos.todo.model.User;
import cz.iivos.todo.model.service.CategoryService;
import cz.iivos.todo.model.service.CategoryServiceImpl;
import cz.iivos.todo.model.service.TaskService;
import cz.iivos.todo.model.service.TaskServiceImpl;
import cz.iivos.todo.security.SecurityService;
import cz.iivos.todo.views.Navigation.ViewId;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author marek
 * @author stefan
 */
@SuppressWarnings("serial")
public final class TodosView extends VerticalLayout implements View, RefreshTaskLabelListener {

    private static final Logger logger = Logger.getLogger(TodosView.class.getName());

    /**
     * přihlášený uživatel
     */
    private User user;

    /**
     * Tlačítko pro vytvoření úkolu
     */
    private final Button btNewTodo;

    /**
     * Tlačítko pro úpravu úkolu
     */
    private final Button btEditTodo;

    /**
     * Tlačítko pro smazání úkolu
     */
    private final Button btDeleteTodo;

    /**
     * SQLContainer pro práci s tabulkou úkolů
     */
    private SQLContainer sqlContainer;

    /**
     * 1 položka SQLContainer-u (1 řádek v tabulce).
     */
    private Item item;

    /**
     * id této položky.
     */
    private Object itemId;

    /**
     * SQLContainer filter na filtrování historických(již splněných) úkolů
     */
    private final Filter filterHistory;

    /**
     * SQLContainer filter na filtrování nesmazaných úkolů od daného uživatele
     */
    private Filter filterBasic;

    
    /**
     * Layout pro filtrování podle kategorií.
     */
    private CategoryFilteringLayout vlCatFil;

    /**
     * Výpis celkového počtu úkolů a pro nejbližší dny
     */
    private final Label laTableTop;

    /**
     * Tabulka se seznamem úkolů
     */
    private Table ftTodos;

    /**
     * Přepínač pro změnu stavu úkolu
     */
    private final SilentCheckBox cbTodoDone;

    /**
     * Přepínač pro změnu zobrazování historie.
     */
    private final SilentCheckBox cbHistory;

    /**
     * Výpis informací o jednom úkolu
     */
    private final Label laTodoInfo;

    /**
     * Layout napravo od tabulky
     */
    private VerticalLayout vlTableRight;

    //ostatní pomocné entity:
    private final DBAdapter dbAdapter;
    private final SecurityService securityService;
    private final CategoryService categoryService;
    private final TaskService taskService;
    private final Navigation navigation;

    //0.
    /**
     * Konstruktor
     *
     * @param navigation
     */
    public TodosView(final Navigation navigation) {

        filterHistory = new Compare.Equal("completed", Boolean.FALSE);

        this.navigation = navigation;

        dbAdapter = new DBAdapter();
        securityService = new SecurityService();
        taskService = new TaskServiceImpl();
        categoryService = new CategoryServiceImpl();

        // inicializace komponent
        laTableTop = new Label();
        laTableTop.setSizeFull();
        laTableTop.setStyleName(ValoTheme.LABEL_BOLD);

        cbTodoDone = new SilentCheckBox();
        cbTodoDone.setCaption("Splněno");
        cbTodoDone.setStyleName(ValoTheme.CHECKBOX_LARGE);
        cbTodoDone.setWidth(100, Unit.PERCENTAGE);
        cbTodoDone.setVisible(false);

        initSplnenoCB();

        cbHistory = new SilentCheckBox();
        cbHistory.setCaption("Historie");
        cbHistory.setStyleName(ValoTheme.CHECKBOX_LARGE);
        cbHistory.setWidth(100, Unit.PERCENTAGE);
        cbHistory.setEnabled(true);
        cbHistory.setValue(Boolean.FALSE);
        initHistoryCB();

        laTodoInfo = new Label();
        laTodoInfo.setContentMode(ContentMode.HTML);
        laTodoInfo.setWidth(100, Unit.PERCENTAGE);

        btNewTodo = new Button("Přidat", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                if (sqlContainer != null) {
                    TaskDlg tdlg = new TaskDlg("Nový úkol", sqlContainer, null,
                        new UpdateTodoListener());
                    UI.getCurrent().addWindow(tdlg);
                }
            }
        });

        btNewTodo.setSizeFull();
        btNewTodo.setStyleName(ValoTheme.BUTTON_TINY);
        btNewTodo.addStyleName(ValoTheme.BUTTON_FRIENDLY);

        btEditTodo = new Button("Upravit", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if (item != null) {
                    TaskDlg tdlg = new TaskDlg("Vlastnosti úkolu",
                        sqlContainer, item, new UpdateTodoListener());
                    UI.getCurrent().addWindow(tdlg);
                } else {
                    Notification.show("Vyber nejdříve řádek v tabulce!");
                }
            }
        });

        btEditTodo.setSizeFull();
        btEditTodo.setStyleName(ValoTheme.BUTTON_TINY);
        btEditTodo.addStyleName(ValoTheme.BUTTON_PRIMARY);

        // Smazani Ukolu:
        btDeleteTodo = new Button("Odstranit", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                if (item != null) {
                    final YesNoWindow window = new YesNoWindow("Upozornenie",
                        "Chcete úkol smazat?", new DeleteTaskListener());
                    UI.getCurrent().addWindow(window);
                } else {
                    Notification.show("Vyber nejdříve řádek v tabulce!");
                }
            }
        });

        btDeleteTodo.setSizeFull();
        btDeleteTodo.setStyleName(ValoTheme.BUTTON_TINY);
        btDeleteTodo.addStyleName(ValoTheme.BUTTON_DANGER);

    }

    //1.
    /**
     * Inicializace hlavního layoutu. rozmístění komponent.
     */
    public void initLayout() {
        
        removeAllComponents();
        Tools.initApplicationLayout(this, navigation, ViewId.TODOS, user);
        // rozmisteni komponent na strance
        HorizontalLayout hlTableTop = new HorizontalLayout(laTableTop,
            cbHistory);
        hlTableTop.setWidth(100, Unit.PERCENTAGE);

        HorizontalLayout hlTableRightButtons = new HorizontalLayout(btNewTodo,
            btEditTodo, btDeleteTodo);
        hlTableRightButtons.setSpacing(true);
        hlTableRightButtons.setWidth(100, Unit.PERCENTAGE);
        hlTableRightButtons.setExpandRatio(btNewTodo, 0.333f);
        hlTableRightButtons.setExpandRatio(btEditTodo, 0.333f);
        hlTableRightButtons.setExpandRatio(btDeleteTodo, 0.333f);

        user = securityService.getCurrentUser();
        try {
            // sqlcontainer pro tabulku:
            if (user != null){
               sqlContainer = DoDbConn.getContainer("TASK");  
               this.addBasicFilter();
               this.addHistoryFilter();
               this.initTaskTB();
            }


        } catch (SQLException | NoSuchFieldException | SecurityException ex) {
            logger.warn(ex.getLocalizedMessage());
            throw new RuntimeException(ex);
        }


        List<Category> cats = categoryService.findAllCategoriesByUser(user);
        vlCatFil = new CategoryFilteringLayout(this.sqlContainer, cats, this);

        vlTableRight = new VerticalLayout(hlTableRightButtons, cbTodoDone,
            laTodoInfo, vlCatFil);
        vlTableRight.setSpacing(true);
        vlTableRight.setSizeFull();
        vlTableRight.setStyleName("todoInfoLayout");
        vlTableRight.setExpandRatio(laTodoInfo, 1.0f);

        HorizontalLayout hlTable = new HorizontalLayout(ftTodos, vlTableRight);
        hlTable.setSizeFull();
        hlTable.setExpandRatio(ftTodos, 0.65f);
        hlTable.setExpandRatio(vlTableRight, 0.35f);

        this.addComponent(hlTableTop);
        this.addComponent(hlTable);
        this.setExpandRatio(hlTable, 1.0f);
    }

    //2.
    /**
     * Inicializuji se hodnoty po vstoupeni na stranku:
     *
     */
    private void updateTodoInfo() {
        user = securityService.getCurrentUser();

        // update labelu vybranÉHO úkolu:
        if (item != null) {
            cbTodoDone.setVisible(true);
        } else {
            cbTodoDone.setVisible(false);
        }
        this.updateTaskLabel();

        // update labelu 2
        String laTopStr = this.getTopLabelText();
        laTableTop.setValue(laTopStr);
        this.addBasicFilter();
    }

    // 3. stefan
    /**
     * Inicializuje tabulku úkolů
     */
    private void initTaskTB() throws NoSuchFieldException, SecurityException {

        ftTodos = new Table();
        ftTodos.setSizeFull();

        String[] colNames = {"Název", "Kategorie", "Termín"};
        String[] dBcolNames = {"title", "id_tcy", "deadline"};

        ftTodos.setContainerDataSource(sqlContainer);
        ftTodos.setVisibleColumns((Object[]) dBcolNames);
        ftTodos.setColumnHeaders(colNames);

        ftTodos.setSelectable(true);

        ftTodos.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                itemId = ftTodos.getValue();
                item = ftTodos.getItem(itemId);
                if (item != null) {
                    cbTodoDone.setInternalValuea((Boolean) item
                        .getItemProperty("completed").getValue());
                    cbTodoDone.markAsDirty();
                    cbTodoDone.setVisible(true);

                } else {
                    cbTodoDone.setVisible(false);

                }
                updateTaskLabel();
            }
        });
    }

    // 5.1. stefan
    /**
     * Prida filter basic.
     *
     */
    public void addBasicFilter(){
        if (user != null) {
            sqlContainer.removeContainerFilter(filterBasic);
            filterBasic = new And(new Like("id_lur", "" + user.getId()),
                new Compare.Equal("deleted", Boolean.FALSE));
            sqlContainer.addContainerFilter(filterBasic);
        } 
        sqlContainer.refresh();
    } 


    
    // 5. stefan
    /**
     * Inicializuje history filter na SQLcontaier, vysledkom je, ze zobrazi iba
     * ukoly uzivatela, ktory je prihlaseny. Pripadne ich historia.
     *
     */
    public void addHistoryFilter() {

        if (user != null) {
            sqlContainer.addContainerFilter(filterHistory);
        } 
        sqlContainer.refresh();
    }

    // 6. stefan
    /**
     * Odstrani filter historie, tj. ukazu sa aj ukoncene ukoly.
     *
     */
    public void removeHistoryFilter() {

        sqlContainer.removeContainerFilter(filterHistory);
        sqlContainer.refresh();

    }

    //7.
    /**
     * Obnoví zobrazovací label vybraného úkolu.
     *
     */
    public void updateTaskLabel() {
        String labStr;

        if (item != null) {
            labStr = Tools.createLabelText(item);
        } else {
            labStr = "<p>Není vybrán žádný úkol.</p>";

            cbTodoDone.setVisible(false);
        }
        laTodoInfo.setValue(labStr);

    }

    //8.
    /**
     * Inicializuje checkbox-history listener;
     */
    public void initHistoryCB() {

        cbHistory.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(final ValueChangeEvent event) {
                final boolean val = (boolean) event.getProperty().getValue();
                if (user != null) {
                    if (val) {
                        removeHistoryFilter();
                    } else {
                        addHistoryFilter();
                        refreshTaskLabel();
                    }
                } else {
                    return;
                }
            }
        });
    }

    //9.
    /**
     * Inicializuje checkbox-splneno listener;
     */
    public void initSplnenoCB() {
        cbTodoDone.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(final ValueChangeEvent event) {
                final boolean val = (boolean) event.getProperty().getValue();
                if (item != null) {
                    item.getItemProperty("completed").setValue(val);
                    if (val) {
                        item.getItemProperty("completion_date").setValue(
                            new Date());
                        if (!cbHistory.getValue()) {
                            itemId = null;
                            item = null;
                            cbTodoDone.setInternalValuea(false);
                            cbTodoDone.setVisible(false);
                        }
                    } else {
                        item.getItemProperty("completion_date").setValue(null);
                    }
                    try {
                        sqlContainer.commit();
                        Notification.show("Úkol změněn!");
                        updateTodoInfo();

                    } catch (UnsupportedOperationException | SQLException ex) {
                        logger.warn(ex.getLocalizedMessage());
                        throw new RuntimeException(ex);
                    }
                } else {
                    return;
                }

            }
        });
    }

    //10.
    /**
     * Získá text pro label s oznamem o úkolech.
     */
    public String getTopLabelText() {
        int pocT, pocD, pocZ;

        user = securityService.getCurrentUser();
        StringBuilder finalStr = new StringBuilder();

        List<Task> list = dbAdapter.findActualTasksByUser(user);
        pocT = list.size();
        pocD = taskService.findTasksOnDate(user, RelativeDays.TODAY).size();
        pocZ = taskService.findTasksOnDate(user, RelativeDays.TOMORROW).size();

        finalStr.append("Nesplněné úkoly: ");
        finalStr.append("celkem ").append(pocT);
        finalStr.append(" na dnes ").append(pocD);
        finalStr.append(" na zítra ").append(pocZ);

        // "Počet úkolů: celkem %d, na dnes %d, na zítra %d", 45, 4, 12));
        return finalStr.toString();
    }

    @Override
    public void refreshTaskLabel() {
        item = null;
        itemId = null;
        this.updateTaskLabel();
    }

    /**
     * Vnitřní třída implementující Yes-No Listener
     */
    public class DeleteTaskListener implements YesNoWindowListener {

        public DeleteTaskListener() {

        }

        @Override
        public void doYesAction(Event event) {

            if (itemId != null) {
                item.getItemProperty("deleted").setValue(true);
            } else {
                Notification.show("Není co mazat!");
                return;
            }

            try {
                sqlContainer.commit();
                item = null;
                itemId = null;
                updateTaskLabel();
                updateTodoInfo();
                Notification.show("Úkol úspešne vymazán!");

            } catch (UnsupportedOperationException | SQLException e) {
                try {
                    sqlContainer.rollback();
                } catch (UnsupportedOperationException | SQLException ex) {
                    Notification.show("Mazanie sa nepodarilo!", Notification.Type.ERROR_MESSAGE);
                    logger.warn(ex.getLocalizedMessage());
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    
    /**
     * Vnitřní třída implementující Listener na refresh tohoto view.
     */
    public class UpdateTodoListener implements RenewTodoListener {

        @Override
        public void refreshTodo() {
            updateTodoInfo();
        }
        
        @Override
        public void refreshFilters() {
            addBasicFilter();
            addHistoryFilter();
        }
    }


    @Override
    public void enter(ViewChangeEvent event) {
        if (securityService.getCurrentUser() != null) {
            user = securityService.getCurrentUser();
            initLayout();
            updateTodoInfo();
        } else {
            navigation.navigateTo(ViewId.LOGIN, null);
        }
    }

}
