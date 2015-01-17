/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.iivos.todo.components;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;

import cz.iivos.todo.converter.AnyEntityConverter;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Combobox, ktory ma schopnost transformovat presentation type (tj. String - to
 * co v comboboxu vidime na type, ktery reprezentuje, napr. Boolean, ale muze
 * byt i komplikovanejsi entita) Duvod, proc neni converter navazany primo na
 * obycejny COmboBox, je ten, ze na ten nejde konverter navazat primo (resp. ne
 * jednoduse). V totmo pripade se tedy vyuziva pomocnej TextField, na ktery
 * converter navazat jde.
 *
 * @author stefan
 * @param <E> třída, představitelé které, se mají v comboBoxu zobrazit.
 */
public final class InputComboBox<E> extends ComboBox {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Konvertor mezi reprezentativním zobrazením dané entity v comboBoxu (String)
     * a danou entitou samotnou (E)
     */
    private Converter<String, E> converter;
    
    /**
     * Class třídy E 
     */
    private final Class<?> clsE;

    /**
     * Aktuálně vybraný objekt ze SQLcontaineru
     */
    private Item item; 
    private Property<?> prop; 

    //další pomocné proměnné:
    private final Logger logger = Logger.getLogger(InputComboBox.class.getName());

    /**
     * Pomocná komponenta TextField, která, jak bylo vysvětleno v hlavičce 
     * slouží na aplikaci konverteru. Nebude viditelná.
     */
    private final TextField tf; 
    
    /**
     * Název comboBoxu, je zhodný s názvem proměnné(property), která je typu E.
     */
    private final String fn; // field name
    
    /**
     * FieldGoup je nástroj na svázaní vaadinovské komponenty a nějakého jiného 
     * objekty, který dané entitě poskytuje informace k zobrazení.
     */
    private final FieldGroup fg;

    /**
     * Název typu E, (např java.util.Date).
     */
    private String propertyETypeName;
    
    /**
     * Slovník, ve kterém je klíčem reprezentativní název entity a hodnotou 
     * tato entita sama.
     */
    private final Map<String, E> map;

    //0.
    /**
     *
     * Konstruktor
     *
     * @param fg fieldGtoup
     * @param fn Field name 
     * @param map Slovník reprezentativní jméno/entita
     * @param clsE Class dané entity.
     */
    public InputComboBox(FieldGroup fg, String fn, Map<String, E> map,
        Class<?> clsE) {
        super(fn);
        this.fn = fn;

        this.map = map;

        this.clsE = clsE;
        this.tf = new TextField();
        this.fg = fg;
        this.involveFg();
        try {
            propertyETypeName = clsE.getCanonicalName();
            this.initConverter();
            this.initListener();
            this.initCombo();
        } catch (SecurityException e) {
            logger.warn(e.getLocalizedMessage(), e);
        }

    }

    //1.
    /**
     * Zaplete FieldGroup do struktury:
     *
     */
    private void involveFg() {
        this.fg.bind(tf, fn);
        this.setValue(tf.getValue());
    }

    // 2.
    /**
     * Inicializuje combo box.
     *
     */
    public void initCombo() {

        if (map != null) {
            for (String key : map.keySet()) {
                this.addItem(map.get(key));
                this.setItemCaption(map.get(key), key);
            }
        }

        this.initComboVal();
        this.setImmediate(true);
        this.setNewItemsAllowed(false);
        this.setNullSelectionAllowed(false);
        this.setVisible(true);
    }

    // 3.
    /**
     * Inicializace konvertoru pro pomocny TextField
     *
     */
    public void initConverter() {

        converter = new AnyEntityConverter<>(map, clsE);

        switch (propertyETypeName) {
            case "java.lang.String":
            //pozn. tenhle case je tady jen kvuli rychlosti, jinak staci na vsecno default.
            default:
                tf.setConverter(converter);
                break;

        }
    }

    // 4.
    /**
     * Inicializuje value change listener ComboBoxu
     *
     */
    @SuppressWarnings("serial")
    public void initListener() {
        this.addValueChangeListener(new ValueChangeListener() {

            /**
             *
             */
            private static final long serialVersionUID = -8428549162334253900L;

            @Override
            public void valueChange(
                com.vaadin.data.Property.ValueChangeEvent event) {
                @SuppressWarnings("unchecked")
                E val = (E) event.getProperty().getValue();

                if (val != null) {
                    switch (propertyETypeName) {

                        case "java.lang.String":
                            //pozn. tenhle case je tady jen kvuli rychlosti, jinak staci na vsecno default.
                            tf.setValue(val.toString());
                            break;
                        default:
                            String s;
                            s = (String) converter.convertToPresentation(val,
                                String.class, Locale.ENGLISH);
                            tf.setValue(s);
                            break;
                    }
                }
            }

        });
    }

    // 5.
    /**
     * Slouzi na nastaveni hodnoty v comboBoxe pri zobrazeni formuláře
     * tj pokud již jsou hodnoty známy, nastaví je jako výchozí hodnoty.
     */
    private void initComboVal() {

        if (fg.getItemDataSource() != null) {
            item = fg.getItemDataSource();
            prop = item.getItemProperty(fn);
            if (prop.getValue() != null) {
                this.setValue(prop.getValue());
            } else {
                //do nothing;
            }
        }
    }

}
