package cz.iivos.todo.views;

import com.vaadin.server.FontAwesome;

/**
 * Rozhraní definující navigaci mezi jednotlivými pohledy v aplikci
 *
 * @author Marek Svarc
 */
public interface Navigation {

    /** Identifikátory pohledů na které lze v aplikaci přesměrovat */
    public enum ViewId {

        LOGIN("login", "Odhlásit", FontAwesome.SIGN_OUT),
        REGISTER("register", "Registrovat", null),
        TODOS("todos", "Úkoly", FontAwesome.CALENDAR),
        PROFILE("profile", "Profil", FontAwesome.GEAR),
        CATEGORIES("categories", "Kategorie", FontAwesome.EYE),
        USERLIST("userList", "Seznam uživatelů", FontAwesome.USER);

        private ViewId(String id, String title, FontAwesome icon) {
            this.id = id;
            this.title = title;
            this.icon = icon;
        }

        /** Identifikator pohledu */
        public final String id;

        /** Popis pohledu */
        public final String title;

        /** Identifikator ikony zobrazene v menu */
        public final FontAwesome icon;
    }

    /**
     * Metoda provede změnu pohledu dle parametru view
     *
     * @param view Identifikátor pohledu
     * @param param Parametr předávaný pohledu do metody Enter (může být null)
     */
    void navigateTo(ViewId view, String param);
}
