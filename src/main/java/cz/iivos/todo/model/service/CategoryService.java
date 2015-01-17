package cz.iivos.todo.model.service;

import cz.iivos.todo.model.Category;
import cz.iivos.todo.model.User;

import java.util.List;
import java.util.Map;

/**
 * Rozhrazeni sluzby pro praci s kategroii
 *
 * @author Marek Svarc
 */
public interface CategoryService {

    /** Vrati seznam vsech kategorii daneho uzivatele. */
    public List<Category> findAllCategoriesByUser(User user);
    
    /** Prida kategorii do databaze */
    public void addCategory(Category category);

    /** Zmeni vlstnosti kategorie v databazi */
    public void modifyCategory(Category category);

    /** Odstrani kategorii z databaze */
    public void deleteCategory(Category category);

    /** ziska Idcka vsech kategorii prislouchajicich danemu uzivateli*/
	Map<String, Long> findAllCategoriesIdByUser(User user);
}
