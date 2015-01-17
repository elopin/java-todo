package cz.iivos.todo.enums;

/**
 * Relativní dny.
 * 
 * @author Stefan
 * 
 */
public enum RelativeDays {
	
	TODAY("dnes"), YESTERDAY("včera"), TOMORROW("zítra");

    public String name;

    RelativeDays(String name) {
        this.name = name;
    }

}
