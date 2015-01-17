package cz.iivos.todo.model;

import com.vaadin.shared.ui.colorpicker.Color;
import cz.iivos.todo.interfaces.RepresentativeName;

/**
 * Trida kategorie ukolu
 *
 * @author Marek Svarc
 * @author Stefan
 */
public class Category implements RepresentativeName {

    /** Unikatni identifikator */
    private Long id;

    /** Unikatni identifikator vlastnika kategorie */
    private Long userId;

    /** Nazev kategorie */
    private String title;

    /** Barva pozadi vypisu ukolu */
    private Color backColor;

    /** Barva textu vypisu ukolu */
    private Color textColor;

    public Category() {
        this.id = -1L;
        this.title = "";
        this.backColor = Color.WHITE;
        this.textColor = Color.BLACK;
    }

    public Category(Long id, Long userId, String title, Color backColor, Color textColor) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.backColor = backColor;
        this.textColor = textColor;
    }

    /** Vraci unikatni identifikator kategorie */
    public Long getId() {
        return id;
    }

    /** Nastavi unikatni identifikator kategorie */
    public void setId(Long id) {
        this.id = id;
    }

    /** Vraci unikatni identifikator vlastnika kategorie */
    public Long getUserId() {
        return userId;
    }

    /** Nastavi unikatni identifikator vlastnika kategorie */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /** Vraci nazev kategorie */
    public String getTitle() {
        return title;
    }

    /** Nastavi nazev kategorie */
    public void setTitle(String title) {
        this.title = title;
    }

    /** Vraci barvu pozadi kategorie */
    public Color getBackColor() {
        return backColor;
    }

    /** Nastavi barvu pozadi kategorie */
    public void setBackColor(Color backColor) {
        this.backColor = backColor;
    }

    /** Vraci barvu textu kategorie */
    public Color getTextColor() {
        return textColor;
    }

    /** Nastavi barvu textu kategorie */
    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    //stefan
    @Override
    public String getRepresentativeName() {
        if (title != null && !"".equals(title)){
            return this.title;
        } else {
            return "provizorny nazev";
        }
    }
}
