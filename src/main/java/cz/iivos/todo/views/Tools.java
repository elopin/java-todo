package cz.iivos.todo.views;

import com.vaadin.data.Item;
import com.vaadin.event.LayoutEvents;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import cz.iivos.todo.model.Task;

import cz.iivos.todo.model.User;
import cz.iivos.todo.model.service.TaskServiceImpl;
import cz.iivos.todo.security.SecurityService;
import cz.iivos.todo.views.Navigation.ViewId;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Trida obsahujici pomocne metody pro vytvareni GUI
 *
 * @author Marek Svarc
 */
public class Tools {

    /**
     * Vytvori hlavni menu aplikace
     */
    @SuppressWarnings("serial")
    public static VerticalLayout createApplicationMenu(
            final Navigation navigation, ViewId selectedView,
            User user) {

        VerticalLayout layout = new VerticalLayout();
        layout.addStyleName("appMenulayout");

        // jmeno aplikace
        Label laTitle = new Label(String.format("<strong>%s</strong>, %s ",
                selectedView.title, user.getDisplayName()));
        laTitle.setSizeFull();
        laTitle.setStyleName(ValoTheme.LABEL_BOLD);
        laTitle.addStyleName(ValoTheme.LABEL_LARGE);
        laTitle.setContentMode(ContentMode.HTML);

        // navigacni menu
        HorizontalLayout hlMain = new HorizontalLayout();
        hlMain.addComponent(laTitle);
        hlMain.setWidth(100, Unit.PERCENTAGE);

        ViewId[] views;
        if (user.isAdmin()) {
            views = new ViewId[]{ViewId.TODOS, ViewId.CATEGORIES, ViewId.USERLIST, ViewId.PROFILE, ViewId.LOGIN};
        } else {
            views = new ViewId[]{ViewId.TODOS, ViewId.CATEGORIES, ViewId.PROFILE, ViewId.LOGIN};
        }
        for (final ViewId view : views) {
            if (view != selectedView) {
                Button btn = new Button(view.title, (ClickEvent event) -> {
                    // odhlaseni uzivatele
                    if (view.id.equals(ViewId.LOGIN.id)) {
                        SecurityService s = new SecurityService();
                        s.logout();
                    }
                    navigation.navigateTo(view, null);
                });
                if (view.icon != null) {
                    btn.setIcon(view.icon);
                }
                btn.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
                btn.addStyleName(ValoTheme.BUTTON_SMALL);
                hlMain.addComponent(btn);
            }
        }
        hlMain.setExpandRatio(laTitle, 1.0f);

        // nacteni ukolu u kterych je potreba upozorneni
        TaskServiceImpl taskServ = new TaskServiceImpl();
        final List<Task> hotTasks = Task.getHotTasks(taskServ.findAllTasksByUser(user));

        // zobrazeni upozorneni na zhave ukoly
        VerticalLayout vlHotTasks = null;
        if (hotTasks.size() > 0) {
            Label laHotTasks = new Label(String.format(FontAwesome.EXCLAMATION_CIRCLE.getHtml()
                    + "  Máte nevyřešené úkoly (celkem %d), u kterých se blíží termín dokončení.", hotTasks.size()), ContentMode.HTML);
            laHotTasks.setStyleName(ValoTheme.LABEL_SMALL);

            vlHotTasks = new VerticalLayout(laHotTasks);
            vlHotTasks.setSizeFull();
            vlHotTasks.setStyleName("appHotTasksLayout");
            vlHotTasks.addLayoutClickListener((LayoutEvents.LayoutClickEvent event) -> {
                StringBuilder text = new StringBuilder("<strong>Seznam nevyřešených úkolů, u kterých se blíží termín dokončení</strong><ol>");
                for (Task task : hotTasks) {
                    text.append("<li>");
                    text.append(task.getTitle());
                    text.append("</li>");
                }
                text.append("</ol>");

                Notification msg = new Notification(text.toString(), Notification.Type.HUMANIZED_MESSAGE);
                msg.setHtmlContentAllowed(true);
                msg.setDelayMsec(-1);
                msg.show(Page.getCurrent());
            });
        }

        layout.addComponent(hlMain);
        if (vlHotTasks != null) {
            layout.addComponent(vlHotTasks);
        }
        return layout;
    }

    /**
     * VytvoĹ™Ă­ textovĂ© pole roztaĹľenĂ© na celou ĹˇĂ­Ĺ™ku vlastnĂ­ka
     */
    public static TextField createFormTextField(String caption, boolean required) {
        TextField tf = new TextField(caption);
        tf.setStyleName(ValoTheme.TEXTFIELD_SMALL);
        tf.setSizeFull();
        tf.setValidationVisible(false);

        return tf;
    }

    /**
     * VytvoĹ™Ă­ textovĂ© pole roztaĹľenĂ© na celou ĹˇĂ­Ĺ™ku vlastnĂ­ka
     */
    public static TextArea createTextArea(String caption, boolean required) {
        TextArea ta = new TextArea(caption);
        ta.setRequired(required);
        ta.setSizeFull();

        return ta;
    }

    public static void initApplicationLayout(VerticalLayout layout,
            Navigation navigation, ViewId view, User user) {
        layout.addComponent(Tools.createApplicationMenu(navigation, view, user));
        layout.setSpacing(true);
        layout.setMargin(true);
        layout.setWidth(800, Unit.PIXELS);
        layout.setHeight(600, Unit.PIXELS);
        layout.addStyleName("applayout");
    }

    /**
     * VytvoĹ™Ă­ textovĂ© polepro zadĂˇnĂ­ hesla roztaĹľenĂ© na celou ĹˇĂ­Ĺ™ku
     * vlastnĂ­ka
     */
    public static PasswordField createFormPasswordField(String caption,
            boolean required) {
        PasswordField pf = new PasswordField(caption);
        pf.setStyleName(ValoTheme.TEXTFIELD_SMALL);
        pf.setSizeFull();
        pf.setValidationVisible(false);
        return pf;
    }

    /**
     * Vytvori standardizovany formular
     */
    public static FormLayout createFormLayout(String caption, boolean light,
            Component... components) {
        FormLayout fl = new FormLayout(components);
        fl.setCaption(caption);
        fl.setSpacing(true);
        fl.setWidth(100, Unit.PERCENTAGE);
        if (light) {
            fl.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        }
        fl.addStyleName("labeled");

        return fl;
    }

    /**
     * Vytvori hlavni nadpis
     */
    public static Label createPanelCaption(String caption) {
        Label la = new Label(caption);
        la.addStyleName(ValoTheme.LABEL_BOLD);
        la.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        la.addStyleName(ValoTheme.LABEL_COLORED);
        la.addStyleName(ValoTheme.LABEL_LARGE);
        la.addStyleName("appCaption");

        return la;
    }


    // 3. stefan
    /**
     * Ziska mapu nazvov 'parameter : jeho typ' danej triedy ako string.
     *
     * @param cls Class dana tridy.
     * @return 
     * @throws java.lang.NoSuchFieldException 
     */
    public static Map<String, Class<?>> getTypParametrov(Class<?> cls)
            throws NoSuchFieldException, SecurityException {

        Map<String, Class<?>> typy = new HashMap<>();

        Set<String> zozPar = Tools.getClassProperties(cls, false);

        for (String p : zozPar) {
            Type typ = cls.getDeclaredField(p).getType();
            typy.put(p, (Class<?>) typ);
        }
        return typy;
    }

    // 4. stefan
    /**
     * Ziska nazvy parametrov danej triedy ako zoznam typu String
     *
     * @param cls Class dane tridy
     * @param keepId Ziska nazvy parametrov danej triedy ako zoznam typu String
     * @return 
     * @throws java.lang.NoSuchFieldException
     */
    public static Set<String> getClassProperties(Class<?> cls, boolean keepId)
            throws NoSuchFieldException, SecurityException {

        Set<String> properties = new HashSet<>();

        for (Method method : cls.getDeclaredMethods()) {

            String methodName = method.getName();
            if (methodName.startsWith("set")) {
                properties.add(Character.toLowerCase(methodName.charAt(3))
                        + methodName.substring(4));
            }
        }

        if (!keepId) {
            if (properties.contains("id")) {
                properties.remove("id");
            }
        }

        return properties;
        
        
        
    }   
    
    //5.
     /** Makes map of String / Enum for list of enums.
     *
     * @param listS seznam Stringu
     * @param listI zodpovidajici seznam integeru.
     * @return
     */
    public static Map<String, Integer> makeEnumMap(List<String> listS, List<Integer> listI) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < listI.size(); i++) {
            map.put(listS.get(i), listI.get(i));
        }
        return map;
    }

    // 6.
    /**
     * Vraci info text do labelu na hlavni ToDo strance.
     *
     * @param item polozka z SQLContaineru, nad kterou se ma vytvorit Label
     * @return
     */
    public static String createLabelText(Item item) {

        StringBuilder lab = new StringBuilder(); // Using default 16 character

        // size
        lab.append(getHtmlLabelText("Úkol", item.getItemProperty("title").getValue().toString()));

        // 1. popis:
        final String description = "Popis";
        if (item.getItemProperty("description") != null && item.getItemProperty("description").getValue() != null) {
            lab.append(getHtmlLabelText(description, item.getItemProperty("description").getValue().toString()));
        } else {
            lab.append(getHtmlLabelText(description, "popis není zadán"));
        }

        // 2. opakovani ukolu:
        final String repetitionPeriod = "Opakování úkolu";
        if (item.getItemProperty("repetition_period") != null
                && item.getItemProperty("repetition_period").getValue() != null) {
            switch (item.getItemProperty("repetition_period").getValue().toString()) {
                case "0":
                    lab.append(getHtmlLabelText(repetitionPeriod, "opakování není zadáno"));
                    break;
                case "1":
                    lab.append(getHtmlLabelText(repetitionPeriod, "každý týden"));
                    break;
                case "2":
                    lab.append(getHtmlLabelText(repetitionPeriod, "každý měsíc"));
                    break;
                default:
                    lab.append(getHtmlLabelText(repetitionPeriod, "jiná perioda"));
                    break;
            }
        } else {
            lab.append(getHtmlLabelText(repetitionPeriod, "chybí perioda"));
        }

        // 3. splneno:
        final String deadline = "Termín";
        if (item.getItemProperty("completed") != null
                && item.getItemProperty("completed").getValue() != null) {
            switch (item.getItemProperty("completed").getValue().toString()) {
                case "false":
                case "FALSE":
                    if (item.getItemProperty("deadline").getValue() != null) {
                        lab.append(getHtmlLabelText(deadline, item.getItemProperty("deadline").getValue().toString()));
                    } else {
                        lab.append(getHtmlLabelText(deadline, "termín není zadán"));
                    }
                    break;
                case "true":
                case "TRUE":
                    if (item.getItemProperty("completion_date").getValue() != null) {
                        lab.append(getHtmlLabelText(deadline, "Úkol splněn dne "
                                + item.getItemProperty("completion_date").getValue().toString()));
                    } else {
                        lab.append(getHtmlLabelText(deadline, "termín není zadán"));
                    }
                    break;
            }
        } else {
            lab.append(getHtmlLabelText(deadline, "splněno"));
        }

        return lab.toString();
    }

    private static String getHtmlLabelText(String caption, String text) {
        return String.format("<div class=v-label-%s><strong>%s</strong></div><i>%s</i>",
                ValoTheme.LABEL_BOLD, caption, text);
    }

}
