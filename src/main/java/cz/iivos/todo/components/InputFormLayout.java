/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.iivos.todo.components;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import cz.iivos.todo.converter.DateConverter;
import cz.iivos.todo.enums.TaskRepetitions;
import cz.iivos.todo.enums.TaskWarnings;
import cz.iivos.todo.listeners.OkCancelListener;
import cz.iivos.todo.model.User;
import cz.iivos.todo.model.service.CategoryService;
import cz.iivos.todo.model.service.CategoryServiceImpl;
import cz.iivos.todo.security.SecurityService;
import cz.iivos.todo.views.TodosView;
import cz.iivos.todo.views.Tools;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import org.apache.log4j.Logger;

/**
 *
 * Do jiste miry univerzalni formular. Pro zadanou tridu (typu T) samo pomoci
 * reflexe zjisti ake ma parametry a podle nich vytvori potrebne policka a svaze
 * je s FieldGoup-em aby se zmeny ve formulari promitali primo do prislusnich
 * entit.
 *
 * @author Stefan
 * @param <T> Daná třída T, pro kterou se formulář vytvoří
 */
public final class InputFormLayout<T> extends FormLayout {

    /**
     * Identifikator:
     */
    private static final long serialVersionUID = 4947104793788125920L;

    /**
     * Class dané třídy T
     */
    private final Class<?> clsT;
    
    
    /**
     * Spolu s uzavřením formulárě se musí vykonat další akce v základním view,
     * ze kterého se formulář otevře, na to slouží tento listener.
     */
    private final OkCancelListener okCancelListener;
    
    /**
     * SQLcontainer, na kterém je postavena tabulka s úkoly.
     */
    private final SQLContainer sqlContainer;
    
    /**
     * FieldGoup je nástroj na svázaní vaadinovské komponenty a nějakého jiného 
     * objekty, který dané entitě poskytuje informace k zobrazení.
     */
    private final FieldGroup fg;
    
    /**
     * Slovník, ve kterém je klíčem název parametru a hodnotou 
     * pro něj vhodná interaktivní komponenta. (např. completion_date/DateField)
     */
    private final Map<String, Component> fieldMap;
    
    /**
     * Proměnná, která ukládá informaci o tom, jestli se bude upravovat 
     * již existující položka(false), nebo vytvářet nová(true).
     */
    private final boolean isNew;
    
    /**
     * Vybraná položka ze SQLContaineru (řádek z tabulky)
     */
    private Item item;
    
    /**
     * id této položky.
     */
    private Object itemId;
    
    /**
     * Layout, kde budou zobrazeny interaktivní 
     * komponenty všechny kromě tlačítek OK-CANCEL.  
     */
    private FormLayout fieldsFL;
    
    /**
     * Layout pro uložení tlačítek OK-CANCEL.
     */
    private HorizontalLayout buttonsHL;
    
    /**
     * Tlačítka pro potvrzení, resp. zrušení změn ve formuláři.
     */
    private Button okBT, cancelBT;
    
    //další pomocné proměnné:
    private final Logger logger = Logger.getLogger(InputFormLayout.class.getName());
    private final SecurityService securityService;
    private final CategoryService categoryService;

    //0.
    /**
     * Konstruktor.
     * 
     * @param clsT Class třídy T
     * @param item položka ze SQLContaineru
     * @param sqlCont SQL container na kterém je postavena tabulka s úkoly.
     * @param okl listener pro vykonání dodatečných akcí spojených s OK-CANCEL.
     */
    public InputFormLayout(Class<?> clsT, Item item, SQLContainer sqlCont, OkCancelListener okl) {

        this.categoryService = new CategoryServiceImpl();
        this.fieldMap = new HashMap<>();
        securityService = new SecurityService();
        this.fg = new FieldGroup();
        fg.setBuffered(false);
        this.sqlContainer = sqlCont;
        this.clsT = clsT;
        this.okCancelListener = okl;
        this.item = item;
        if (item == null) {
            isNew = true;
            sqlContainer.removeAllContainerFilters();
            itemId = sqlContainer.addItem();
            this.item = sqlContainer.getItem(itemId);
            sqlContainer.getItem(itemId).getItemProperty("title").setValue("název...");
            okCancelListener.obnovFilter();
        } else {
            isNew = false;
        }
        fg.setItemDataSource(this.item);
        this.initFieldsLayout();
        this.addButtons();

        // upravy vzhladu:
        this.setMargin(true);
        this.setSpacing(true);

    }

    //1.
    /**
     * Vytvoří formulář s danými políčkami, šitými na míru (šité na
     * mieru typov: Long, String(TextArea/TextField), Boolean(CheckBox),
     * Date(DateField), enum(ComboBox, SelectList, TwinColSelect...)).
     */
    public void initFieldsLayout() {

        fieldsFL = new FormLayout();
        fieldsFL.setMargin(true);
        fieldsFL.setSpacing(true);

        String propertyTypeName; // nazov typu danej property danej ent.

        Map<String, Class<?>> mapPar = new HashMap<>();
        try {
            mapPar = Tools.getTypParametrov(clsT);

        } catch (NoSuchFieldException | SecurityException ex) {
            logger.warn(ex.getLocalizedMessage(), ex);
            return;
        }

        for (String pn : mapPar.keySet()) {

            propertyTypeName = mapPar.get(pn).getCanonicalName();

            switch (propertyTypeName) {
                case "java.lang.Long":
                    if (pn.contains("id_")) {
                        switch (pn) {
                            case "id_tcy":
                                //POZN: parametry POJO by se meli jmenovat stejne ako 
                                // stloupce tabulky a identifikator by se mel jmenovat jen 'id'..
                                User user = securityService.getCurrentUser();
                                Map<String, Long> map
                                    = categoryService.findAllCategoriesIdByUser(user);
                                InputComboBox<Long> cb;
                                cb = new InputComboBox<>(fg, pn, map, Long.class);
                                fieldMap.put(pn, cb);
                                break;
                            default:
                                break;
                        }
                    } else {
                        Component fi = bindTextField(pn);
                        fieldMap.put(pn, fi);//fieldsFL.addComponent(fi);
                    }
                    break;

                case "java.lang.String":
                    switch (pn) {
                        case "description":
                            fieldMap.put(pn, bindTextArea(pn));
                            break;
                        case "title":
                            TextField field = bindTextField(pn);
                            field.setRequired(true);
                            fieldMap.put(pn, field);
                            break;
                        default:
                            fieldMap.put(pn, bindTextField(pn));
                            break;
                    }

                    break;

                case "java.lang.Boolean":
                    switch (pn) {
                        case "deleted":
                        case "completed":
                            break;
                        default:
                            fieldMap.put(pn, bindCheckBox(pn));
                    }
                    break;

                case "java.util.Date":
                    switch (pn) {
                        case "creation_date":
                        case "completion_date":
                            //do nothing. tyto datumy se vyplní automaticky.
                            break;
                        default:
                            fieldMap.put(pn, bindUtilDateField(pn));
                            break;
                    }
                    break;

                case "cz.iivos.todo.enums.TaskRepetitions":
                    Map map = Tools.makeEnumMap(TaskRepetitions.getPeriodsNames(), 
                        TaskRepetitions.getOrdinals());
                    InputComboBox<Integer> cb = 
                        new InputComboBox<>(fg, pn, map, Integer.class);
                    fieldMap.put(pn, cb);
                    break;
                    
                case "cz.iivos.todo.enums.TaskWarnings":
                    Map map1 = Tools.makeEnumMap(TaskWarnings.getWarningNames(), 
                        TaskWarnings.getOrdinals());
                    InputComboBox<Integer> cb1 = 
                        new InputComboBox<>(fg, pn, map1, Integer.class);
                    fieldMap.put(pn, cb1);
                    break;
                default:
                    // do nothing
                    break;
            }
        }

        this.completeForm();
        fieldsFL.setEnabled(true);
        this.addComponent(fieldsFL);
    }

    //2.
    /**
     * Rozvhne fields na Layout podle uživatelské logiky věci: pokud logiku
     * neznáme, naháže je tam náhodně:
     *
     */
    private void completeForm() {

        switch (clsT.getCanonicalName()) {
            case "cz.iivos.todo.model.Task":
                fieldMap.get("title").setCaption("název: ");
                fieldsFL.addComponent(fieldMap.get("title"));

                fieldMap.get("description").setCaption("popis: ");
                fieldsFL.addComponent(fieldMap.get("description"));

                fieldMap.get("id_tcy").setCaption("kategorie: ");
                fieldsFL.addComponent(fieldMap.get("id_tcy"));

                fieldMap.get("repetition_period").setCaption("opakování: ");
                fieldsFL.addComponent(fieldMap.get("repetition_period"));

                fieldMap.get("warning_period").setCaption("upozornění: ");
                fieldsFL.addComponent(fieldMap.get("warning_period"));

                fieldMap.get("deadline").setCaption("termín: ");
                fieldsFL.addComponent(fieldMap.get("deadline"));

                break;

            default:
                //náhodné rozvržení:
                for (String key : fieldMap.keySet()) {
                    fieldsFL.addComponent(fieldMap.get(key));
                }
        }
    }

    // 3.
    /**
     * Vytvori pole pre textField a zviaze ho s FG.
     *
     * @param fn
     * @return
     */
    public TextField bindTextField(String fn) {
        TextField field = new TextField(fn);
        fg.bind(field, fn);
        return field;
    }

    // 4.
    /**
     * Vytvori pole pre textAreu a zviaze ho s FG.
     *
     * @param fn
     * @return
     */
    public TextArea bindTextArea(String fn) {
        TextArea field = new TextArea(fn);
        fg.bind(field, fn);
        return field;
    }

    // 5.
    /**
     * Vytvori checkBox a zviaze ho s FG.
     *
     * @param fn
     * @return
     */
    public CheckBox bindCheckBox(String fn) {
        CheckBox field = new CheckBox(fn);
        fg.bind(field, fn);
        return field;
    }

    // 6.
    /**
     * Vytvori pole pre util.Date a zviaze ho s FG.
     *
     * @param fn
     * @return
     */
    public PopupDateField bindUtilDateField(String fn) {

        PopupDateField field = new PopupDateField(fn);
        field.setConverter(new DateConverter());
        field.setImmediate(true);
        field.setTimeZone(TimeZone.getTimeZone("GMT+1:00"));
        field.setLocale(new Locale("cz", "CZ"));

        field.setResolution(Resolution.MINUTE);

        field.setDateFormat("yyyy-MM-dd HH:mm:ss");
        fg.bind(field, fn);
        return field;
    }
    
    // 7.
    /**
     * Vytvoří, inicializuje a přidá tlačítka OK-CANCEL.
     *
     */
    private void addButtons() {

        buttonsHL = new HorizontalLayout();
        buttonsHL.setMargin(true);
        buttonsHL.setSpacing(true);

        okBT = new Button("Ok");
        cancelBT = new Button("Cancel");
        cancelBT.setEnabled(true);
        okBT.setEnabled(true);

        okBT.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if (isNew) {
                    User user = securityService.getCurrentUser();
                    sqlContainer.removeAllContainerFilters();
                    sqlContainer.getItem(itemId).getItemProperty("id_lur").setValue(user.getId());
                    sqlContainer.getItem(itemId).getItemProperty("deleted").setValue(Boolean.FALSE);
                    sqlContainer.getItem(itemId).getItemProperty("completed").setValue(Boolean.FALSE);
                    sqlContainer.getItem(itemId).getItemProperty("creation_date").setValue(new Date());
                    okCancelListener.obnovFilter();
                }

                
                // ulozenie zmien do DB:
                try {
                    fg.commit();
                    sqlContainer.commit();
                    fieldsFL.setEnabled(false);
                    okCancelListener.doAdditionalOkAction();

                    Notification.show("Úkol byl úspešně uložen!");
                } catch (SQLException | UnsupportedOperationException | CommitException e) {
                    logger.warn(e.getLocalizedMessage(), e);
                }
            }
        });

        cancelBT.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                okBT.setEnabled(true);
                if (isNew) {
                    sqlContainer.removeItem(itemId);
                }
                okCancelListener.doAdditionalCancelAction();
            }
        });
        TodosView s;
        buttonsHL.addComponent(okBT);
        buttonsHL.addComponent(cancelBT);

        this.addComponent(buttonsHL);

    }

}
