/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.iivos.todo.dialogs;

import com.vaadin.data.Item;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import cz.iivos.todo.components.InputFormLayout;
import cz.iivos.todo.listeners.OkCancelListener;
import cz.iivos.todo.listeners.RenewTodoListener;
import cz.iivos.todo.model.Task;

/**
 * Dialog pro editaci vlastnosti jedne ulohy
 * 
 * @author Marek Svarc
 * @author Stefan
 */
public class TaskDlg extends Window implements OkCancelListener {
    
    /**
     * Formulář na zadávání/úpravu úkolů.
     */
	final private InputFormLayout<Task> flInputForm;
    
    /**
     * Listener na obnovu základního view, ze kterého se 
     * do fromulářového okna vstupuje, po vyplnění formuláře.
     */
	private final RenewTodoListener listener;

    //0.
    /**
     * Konstruktor.
     * 
     * @param caption nadpis okna
     * @param sqlCont sqlcontainer na kterém je postavena tabulka s úkoly.
     * @param item vybraná položka z SQLcontaineru (řádek z tabulky).
     * @param lis Listener na obnovu view.
     */
	public TaskDlg(String caption, SQLContainer sqlCont, Item item, RenewTodoListener lis) {
		this.listener = lis;
		this.setCaption(caption);
		setModal(true);
		flInputForm = new InputFormLayout<Task>(Task.class, item, sqlCont, this);

		// obsah dialogu
		VerticalLayout content = new VerticalLayout(flInputForm);
		content.setSpacing(true);
		content.setMargin(true);

		setContent(content);
	}

	// pozri na popis OkCancelListener
	@Override
	public void doAdditionalOkAction() {
		listener.refreshTodo();
		close();
	}

	@Override
	public void doAdditionalCancelAction() {
		listener.refreshTodo();
		close();
    }

    @Override
    public void obnovFilter() {
		listener.refreshFilters();
    }

}
