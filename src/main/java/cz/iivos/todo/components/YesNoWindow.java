/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.iivos.todo.components;

import cz.iivos.todo.listeners.YesNoWindowListener;
import com.vaadin.ui.*;

/**
 * Komponenta pro potvrzovací dialog. 
 *
 * @author stefan
 * 
 */
public class YesNoWindow extends Window {

	
	Button yesBT, noBT;
	YesNoWindowListener listener;
	HorizontalLayout buttonLayout;
	VerticalLayout content;
	Label msgLB;
	
    //0.
    /**
     * Konstruktor.
     *
     * @param caption nadpis okna
     * @param message zpráva, která má být zobrazena
     * @param listener listener pro akci YES.
     */
    public YesNoWindow(String caption, String message, YesNoWindowListener listener) {
    	
        super(caption);
        
    	this.listener = listener;
    	
        content = new VerticalLayout();
        this.setContent(content);
        content.setWidth(450, Unit.PIXELS);
        content.setMargin(true);
        content.setSpacing(true);
        setModal(true);

        buttonLayout = new HorizontalLayout();
        msgLB = new Label(message);
        content.addComponent(msgLB);

        buttonLayout = new HorizontalLayout();
        content.addComponent(buttonLayout);
        buttonLayout.setSpacing(true);
        
        yesBT = new Button("Ano");
        buttonLayout.addComponent(yesBT);
        
        yesBT.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                onYes(event);
            }
        });
        
        noBT = new Button("Ne");
        buttonLayout.addComponent(noBT);
        noBT.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                close();
            }
        });
       
        center();
    }

    /**
     * Spuštění akce po stlačení YES.
     */
    public void onYes(Event event) {
    	listener.doYesAction(event);
        close();
    }
}
