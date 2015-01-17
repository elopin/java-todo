package cz.iivos.todo.components;

import com.vaadin.ui.CheckBox;

/**
 * CheckBox, který umožňuje změnu hodnoty, s i bez zpuštění ValueChangeListeneru
 * 
 * @author stefan
 * */
public class SilentCheckBox extends CheckBox {
        private static final long serialVersionUID = -6208978581410404086L;
        
        public SilentCheckBox() {
            super();
        }

        
        /**
         * Metoda pro tichou změnu hodnoty.
         */
        public void setInternalValuea(Boolean newValue) {
            if (newValue == null)
                newValue = false;
            super.setInternalValue(newValue); 
        }
    }    