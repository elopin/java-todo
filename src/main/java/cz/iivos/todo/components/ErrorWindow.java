package cz.iivos.todo.components;

import com.vaadin.server.AbstractErrorMessage;
import com.vaadin.server.ErrorMessage;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * Okno se stacktrace.
 * @author lukas
 */
public class ErrorWindow extends Window {
    
    private VerticalLayout content;
    
    private Label error;
    
    public ErrorWindow(Throwable ex) {
	
	setCaption("Chyba běhu aplikace!");
	setWidth(500, Unit.PIXELS);
	setHeight(300, Unit.PIXELS);
	
	content = new VerticalLayout();
	setContent(content);
	
	ErrorMessage errMess = AbstractErrorMessage.getErrorMessageForException(ex);
	error = new Label(errMess.getFormattedHtmlMessage());
	error.setContentMode(ContentMode.HTML);
	error.addStyleName("failure");
	content.addComponent(error);
	
	this.center();
    }  
}
