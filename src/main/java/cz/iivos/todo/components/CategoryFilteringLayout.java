package cz.iivos.todo.components;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.IsNull;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.ui.VerticalLayout;
import cz.iivos.todo.listeners.RefreshTaskLabelListener;
import cz.iivos.todo.model.Category;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Trida zodpovidajici componente, ktera obsahuje checkboxy 
 * pro filtrovani Ukolu podle Kategorii. 
 * 
 * @author Stefan
 */
public final class CategoryFilteringLayout extends VerticalLayout {

   /**
    * Listener, pomocí kterého se bude vynulovávat obsah TaskLabel.
    * (protože když se schová daný task v tabulce, nemá smysl, aby figuroval ve TaskLabel)
    */
    private final RefreshTaskLabelListener listener;
    
   /**
    * Množina id kategorií, které mají být viděny.
    */
    private final Set<Long> catIdsForFiltering;
    
   /**
    * SQLcontainer, na kterém je postavena tabulka s úkoly.
    */
    private final SQLContainer sqlContainer;
    
   /**
    * Komplexní sqlCOntainer filtr na zobrazení jen úkoly vybraných kategorií.
    */
    private Container.Filter filterCategories;
   
    
    //0.
    /**
     * Konstruktor.
     * @param sc sqlContainer tabulky, která se má filtrovat.
     * @param cats seznam všech kategorií, které se mají zobrazit.
     * @param listener listener pro vyčistěníí TaskLabel.
     */
    public CategoryFilteringLayout(SQLContainer sc, List<Category> cats, RefreshTaskLabelListener listener) {
        this.sqlContainer = sc;
        this.catIdsForFiltering = new HashSet<>();
        this.listener = listener;
        this.initLayout(cats);
    }

    // 1. 
    /**
     * Inicializuje category filter na SQLcontaier, vysledkem je, ze zobrazi iba
     * ty ukoly, ktere zodpovedaju danym kategoriam.
     *
     */
    public void refreshCategoryFilter() {
        
        this.sqlContainer.removeContainerFilter(filterCategories);

        if (catIdsForFiltering.isEmpty()) {
            // nezobrazi se nic:
            this.filterCategories = new Like("id_tak", "" + -1);
        } else {
            Filter filt;
            List<Filter> filts = new ArrayList<>();
            for (Long i : this.catIdsForFiltering) {
                switch (i + "") {
                    case "-1":
                        filt = new IsNull("id_tcy");
                        break;
                    default:
                        filt = new Like("id_tcy", "" + i);
                }

                filts.add(filt);
            }

            Filter[] filtsf = new Filter[filts.size()];
            filtsf = filts.toArray(filtsf);
            this.filterCategories = new Or(filtsf);
        }

        sqlContainer.addContainerFilter(filterCategories);
        sqlContainer.refresh();
    }

    // 2. 
    /**
     * remove category filter
     */
    public void removeCategoryFilter() {
        sqlContainer.removeContainerFilter(filterCategories);
        sqlContainer.refresh();
    }

    //3.
    /**
     * Init Layout
     * @param cats Seznam všech kategorií (daného uživatele).
     */
    public void initLayout(List<Category> cats) {
        
        this.removeAllComponents();
        
        CategoryCheckBox ccb;
        for (Category cat : cats) {
            ccb = new CategoryCheckBox(cat);
            this.catIdsForFiltering.add(cat.getId());
            ccb.setInternalValuea(Boolean.TRUE);
            this.addComponent(ccb);
        }
        ccb = new CategoryCheckBox(null);
        this.catIdsForFiltering.add(ccb.getCatId());
        ccb.setInternalValuea(Boolean.TRUE);
        this.addComponent(ccb);
    }

    /**
     * Třída představující checkboxy na filtrování dané kategorie.
     */
    public final class CategoryCheckBox extends SilentCheckBox {

        /**
         * id kategorie, kterou daný checkbox představuje.
         */
        private final Long catId;

        public CategoryCheckBox(Category cat) {

            if (cat != null) {
                this.catId = cat.getId();
                this.setCaption(cat.getRepresentativeName());
            } else {
                this.catId = -1L;
                this.setCaption("bez kategorie");
            }
            this.initCategoryChB();
        }
        
        public Long getCatId(){
            return catId;
        }

        //a1.
        /**
         * Inicializuje checkbox listener;
         */
        public void initCategoryChB() {

            this.addValueChangeListener((final Property.ValueChangeEvent event) -> {
                final boolean val = (boolean) event.getProperty().getValue();
                if (val) {
                    catIdsForFiltering.add(catId);
                    refreshCategoryFilter();
                } else {
                    boolean remove = catIdsForFiltering.remove(catId);
                    refreshCategoryFilter();
                    listener.refreshTaskLabel();
                }
            });
        }
    }
}
