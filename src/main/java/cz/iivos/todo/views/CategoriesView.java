package cz.iivos.todo.views;

import com.vaadin.data.Property;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.ui.AbstractColorPicker;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ColorPicker;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.colorpicker.ColorChangeEvent;
import com.vaadin.ui.components.colorpicker.ColorChangeListener;
import com.vaadin.ui.themes.ValoTheme;
import cz.iivos.todo.model.Category;
import cz.iivos.todo.model.User;
import cz.iivos.todo.model.service.CategoryService;
import cz.iivos.todo.model.service.CategoryServiceImpl;
import cz.iivos.todo.security.SecurityService;

import cz.iivos.todo.views.Navigation.ViewId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Trida stranky pro spravu kategorii ukolu
 *
 * @author Marek Svarc
 */
@SuppressWarnings("serial")
public class CategoriesView extends VerticalLayout implements View {

    /** Data uzivatele */
    private User user;

    /** Seznam kategorii */
    private List<Category> categories;

    /** Seznam checkBoxu v tabulce pro oznaceni mazanych kategorii */
    private List<CheckBox> delCheckBoxes;

    /** Pristup k uzivatelskym datum */
    final private SecurityService securityService;

    /** Pristup k datum kategorii */
    final private CategoryService categoryService;

    /** Navigace v aplikaci */
    final private Navigation navigation;

    /** Tabulka pro zobrazovani kategorii */
    final private Table tbCategories;

    /** Layout pro rozvrzeni stranky */
    final private HorizontalLayout hlTable;

    /** Vytvori tlacitko pro pridani kategorie */
    private Button createAddButton() {
        Button btn = new Button("Přidat", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                addNewCategory();
            }
        });
        btn.setSizeFull();
        btn.setStyleName(ValoTheme.BUTTON_TINY);
        btn.addStyleName(ValoTheme.BUTTON_FRIENDLY);

        return btn;
    }

    /** Vytvori tlacitko pro smazani kategorie */
    private Button createDeleteButton() {
        Button btn = new Button("Odstranit", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                deleteCheckedCategories();
            }
        });
        btn.setSizeFull();
        btn.setStyleName(ValoTheme.BUTTON_TINY);
        btn.addStyleName(ValoTheme.BUTTON_DANGER);

        return btn;
    }

    /** Vytvori policko pro obecnou editaci barvy */
    private ColorPicker createColorPicker() {
        ColorPicker cp = new ColorPicker();
        cp.setHSVVisibility(false);
        cp.setHistoryVisibility(false);
        cp.setRGBVisibility(false);
        cp.setDefaultCaptionEnabled(false);
        cp.setWidth(100, Unit.PERCENTAGE);
        cp.setStyleName(ValoTheme.BUTTON_TINY);

        return cp;
    }

    /** Vytvori textove pole pro editaci nazvu kategorie */
    private TextField createTextFieldForTitle(final Category category) {
        final TextField tf = new TextField();
        tf.setValue(category.getTitle());
        tf.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                category.setTitle(tf.getValue());
                categoryService.modifyCategory(category);
            }
        });
        tf.setData(category.getId());
        tf.setStyleName(ValoTheme.TEXTFIELD_TINY);
        tf.setWidth(100, Unit.PERCENTAGE);

        return tf;
    }

    /** Vytvori policko pro editaci barvy pozadi */
    private ColorPicker createColorPickerForBackColor(final Category category) {
        ColorPicker cp = createColorPicker();
        cp.setData(category.getId());
        cp.setColor(category.getBackColor());
        cp.addColorChangeListener(new ColorChangeListener() {

            @Override
            public void colorChanged(ColorChangeEvent event) {
                category.setBackColor(event.getColor());
                categoryService.modifyCategory(category);
            }
        });

        return cp;
    }

    /** Vytvori policko pro editaci barvy pozadi */
    private ColorPicker createColorPickerForTextColor(final Category category) {
        ColorPicker cp = createColorPicker();
        cp.setData(category.getId());
        cp.setColor(category.getTextColor());
        cp.addColorChangeListener(new ColorChangeListener() {

            @Override
            public void colorChanged(ColorChangeEvent event) {
                category.setTextColor(event.getColor());
                categoryService.modifyCategory(category);
            }
        });

        return cp;
    }

    public CategoriesView(Navigation navigation) {
        this.navigation = navigation;

        securityService = new SecurityService();
        categoryService = new CategoryServiceImpl();

        // tlacitka
        VerticalLayout vlTableButtons = new VerticalLayout(createAddButton(), createDeleteButton());
        vlTableButtons.setSpacing(true);
        vlTableButtons.setWidth(120, Unit.PIXELS);

        // tabulka kategori
        tbCategories = new Table();
        tbCategories.setSizeFull();
        tbCategories.addContainerProperty(1, CheckBox.class, null);
        tbCategories.setColumnHeader(1, "");
        tbCategories.addContainerProperty(2, TextField.class, null);
        tbCategories.setColumnHeader(2, "Název kategorie");
        tbCategories.setColumnExpandRatio(2, 1);
        tbCategories.addContainerProperty(3, ColorPicker.class, null);
        tbCategories.setColumnHeader(3, "Barva pozadí");
        tbCategories.addContainerProperty(4, ColorPicker.class, null);
        tbCategories.setColumnHeader(4, "Barva textu");

        // rozdeleni na strance
        hlTable = new HorizontalLayout(tbCategories, vlTableButtons);
        hlTable.setSpacing(true);
        hlTable.setSizeFull();
        hlTable.setExpandRatio(tbCategories, 1);
    }

    public void initLayout() {
        removeAllComponents();
        // menu stranky
        Tools.initApplicationLayout(this, navigation, ViewId.CATEGORIES, user);

        this.addComponent(hlTable);
        this.setExpandRatio(hlTable, 1.0f);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        user = securityService.getCurrentUser();
        if (user != null) {
            initLayout();
            updateView(true);
        } else {
            navigation.navigateTo(ViewId.LOGIN, null);
        }
    }

    /** Prida novou kategorii */
    public void addNewCategory() {
        // pridani kategorie do databaze
        categoryService.addCategory(new Category(0L, user.getId(), "Nová kategorie", Color.WHITE, Color.BLACK));
        // aktualizace stranky
        updateView(true);
    }

    /** Odmaze vybrane kategorie a aktualizuje zobrazeni */
    public void deleteCheckedCategories() {
        // stanoveni poctu vybranych kategorii
        int selCount = 0;
        for (CheckBox item : delCheckBoxes) {
            if (item.getValue()) {
                ++selCount;
            }
        }
        // mazani vybranych kategorii
        if (selCount <= 0) {
            Notification.show("Nejsou vybrané kategorie pro odmazání", Notification.Type.ERROR_MESSAGE);
        } else {
            for (int i = delCheckBoxes.size() - 1; i >= 0; --i) {
                if (delCheckBoxes.get(i).getValue()) {
                    int index = Integer.parseInt(delCheckBoxes.get(i).getId());
                    categoryService.deleteCategory(categories.get(index));
                }
            }
            updateView(true);
        }
    }

    /** Aktualizauje zobrazeni stranky */
    public void updateView(boolean loadCategories) {

        // nacteni kategorii z dtabaze
        if (loadCategories) {
            categories = categoryService.findAllCategoriesByUser(user);
        }

        // pomocny seznam zaskrtavacich tlacitek
        delCheckBoxes = new ArrayList<>(categories.size());

        // odstraneni puvodnich prvku v tabulce
        tbCategories.removeAllItems();

        // pridavani radek do tabulky
        for (int i = 0; i < categories.size(); ++i) {
            Category category = categories.get(i);

            CheckBox cb = new CheckBox();
            cb.setId(Integer.toString(i));

            tbCategories.addItem(new Object[]{cb, createTextFieldForTitle(category),
                createColorPickerForBackColor(category), createColorPickerForTextColor(category)},
                    category.getId());

            delCheckBoxes.add(cb);
        }
    }
}
