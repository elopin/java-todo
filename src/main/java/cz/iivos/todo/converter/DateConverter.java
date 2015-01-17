package cz.iivos.todo.converter;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;
/**
 * Konvertor mezi java.util.Date a Timestamp(získaným SQL dotazem z DB).
 * 
 * @author Stefan
 */
public class DateConverter implements Converter<Date, Timestamp> {

    private static final long serialVersionUID = 1L;
    
    @Override
    public Timestamp convertToModel(Date value,
            Class<? extends Timestamp> targetType, Locale locale)
            throws ConversionException {
        return value == null ? null : new Timestamp(value.getTime());
    }

    @Override
    public Date convertToPresentation(Timestamp value,
            Class<? extends Date> targetType, Locale locale)
            throws ConversionException {
        return value;
    }

    @Override
    public Class<Timestamp> getModelType() {
        return Timestamp.class;
    }

    @Override
    public Class<Date> getPresentationType() {
        return Date.class;
    }

    
}