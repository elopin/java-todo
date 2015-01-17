package cz.iivos.todo.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * Enum představující periody opakování úkolu
 *
 * @author stefan
 */
public enum TaskRepetitions {

    NONE("žádné"), WEEKLY("týdně"), MONTHLY("měsíčně");

    public String name;

    TaskRepetitions(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    /**
     * vrací seznam jmen daných enumů
     * @return 
     */
    public static List<String> getPeriodsNames() {
        List<String> list = new ArrayList<>();

        for (TaskRepetitions r : TaskRepetitions.values()) {
            list.add(r.getName());
        }
        return list;
    }

    /**
     * vrací seznam short hodnot daných enumů
     * @return 
     */
    public static List<Integer> getOrdinals() {
        List<Integer> list = new ArrayList<>();
        for (TaskRepetitions r : TaskRepetitions.values()) {
            list.add(r.ordinal());
        }
        return list;
    }

}
