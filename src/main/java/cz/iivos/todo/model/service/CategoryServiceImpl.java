package cz.iivos.todo.model.service;

import cz.iivos.todo.database.DBAdapter;
import cz.iivos.todo.model.Category;
import cz.iivos.todo.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Implementace rozhrazeni sluzby pro praci s kategroii
 *
 * @author Marek Svarc
 */
public class CategoryServiceImpl implements CategoryService {

    private Logger logger = Logger.getLogger(CategoryServiceImpl.class);

    private DBAdapter dbAdapter;

    public CategoryServiceImpl() {
        dbAdapter = new DBAdapter();
    }

    @Override
    public List<Category> findAllCategoriesByUser(User user) {
        return dbAdapter.findAllCategoriesByUser(user);
    }

    @Override
    public void addCategory(Category category) {
        dbAdapter.addCategory(category);
    }

    @Override
    public void modifyCategory(Category category) {
        dbAdapter.modifyCategory(category);
    }

    @Override
    public void deleteCategory(Category category) {
        dbAdapter.deleteCategory(category);
    }

    //stefan: potrebuji to kvuli praci s comboboxem:
    @Override
    public Map<String, Long> findAllCategoriesIdByUser(User user) {
        List<Category> listCat = this.findAllCategoriesByUser(user);
        Map<String, Long> map = new HashMap<>();
        for (Category c : listCat) {
            map.put(c.getRepresentativeName(), c.getId());
        }
        return map;
    }

}
