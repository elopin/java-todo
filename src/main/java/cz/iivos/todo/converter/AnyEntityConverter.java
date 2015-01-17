 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.iivos.todo.converter;

import com.vaadin.data.util.converter.Converter;
import java.util.Locale;
import java.util.Map;

/**
 * Umoznuje zkonvertovat na String a zpet jakoukoli entitu typu E.
 *
 * @author Stefan
 * @param <E> třída entity
 */
public class AnyEntityConverter<E> implements Converter<String, E> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Slovník, ve kterém je klíčem reprezentativní název entity a hodnotou 
     * tato entita sama.
     * Pro zjednodušení celé situace a umožnění univerzálnosti si tento 
     * dopředu připravíme. Tj. konverter logiku převodu neřeší.
     */
    private final Map<String, E> map;
    
    /**
     * Class třídy E
     */
    private final Class<?> cls;

    //0.
    /**
     * Konstruktor.
     * 
     * @param map Slovník, ve kterém je klíčem reprezentativní název entity a hodnotou tato entita sama.
     * @param cls Class třídy E
     */
    public AnyEntityConverter(Map<String, E> map, Class<?> cls) {
        this.map = map;
        this.cls = cls;
    }

    @Override
    public E convertToModel(String value, Class<? extends E> targetType, Locale locale) throws Converter.ConversionException {
        return map.get(value);
    }

    @Override
    public String convertToPresentation(E value, Class<? extends String> targetType, Locale locale) throws Converter.ConversionException {
        for (String key : map.keySet()) {
            if (map.get(key) == value || value.equals(map.get(key))) {
                return key;
            }
        }
        return "null";
    }

    @Override
    public Class<E> getModelType() {
        return (Class<E>) cls;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }

}
