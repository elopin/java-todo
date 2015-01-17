package cz.iivos.todo.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * Enum představující doby upozornění před uplinutím úkolu.
 *
 * @author stefan
 */
public enum TaskWarnings {
	NONE("žádné upozornění"), DAY_BEFORE("den před"), WEEK_BEFORE("týden před");
    
	private final String name;
 
	private TaskWarnings(String s) {
		name = s;
	}
    
    public String getName() {
        return this.name;
    }

    /**
     * vrací seznam jmen daných enumů
     * @return 
     */
    public static List<String> getWarningNames() {
        List<String> list = new ArrayList<>();

        for (TaskWarnings w : TaskWarnings.values()) {
            list.add(w.getName());
        }
        return list;
    }

    /**
     * vrací seznam short hodnot daných enumů
     * @return 
     */
    public static List<Integer> getOrdinals() {
        List<Integer> list = new ArrayList<>();

        for (TaskWarnings w : TaskWarnings.values()) {
            list.add(w.ordinal());
        }
        return list;
    }

}

