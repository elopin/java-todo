/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.iivos.todo.listeners;

import com.vaadin.ui.Component.Event;

/**
 * Listener por vsechna dialogova okna s vyberem yes/no
 *
 * @author stefan
 */
public interface YesNoWindowListener {
    
	public void doYesAction(Event event);
    
}

