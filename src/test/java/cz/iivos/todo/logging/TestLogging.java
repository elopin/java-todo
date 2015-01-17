package cz.iivos.todo.logging;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * Třída testující logování. Obsahuje krátký tutoriál jak logování používat.
 * @author Vít Foldyna
 *
 */
public class TestLogging {

	private static Logger logger = Logger.getLogger(TestLogging.class);

	/**
	 * Testovací metoda. 
	 * Pro spuštění klikněte pravým tlačítkem na třídu -> run as -> jUnit test.
	 */
	@Test
	public void test() {

		/**
		 * Priorita jednotlivých úrovní:
		 * DEBUG 
		 * INFO 
		 * WARN 
		 * ERROR 
		 * FATAL
		 * 
		 * Priorita se nastavuje v souboru log4j.properties (pro vývoj je nastavena na DEBUG).
		 * Pokud se nastaví priorita na DEBUG, vypíší se všechny zprávy.
		 * Pokud se nastaví priorita na WARN, vypíší se zprávy na úrovni WARN, ERROR, FATAL.
		 * Pokud se nastaví priorita na FATAL, vypíší se zprávy na úrovni FATAL.
		 * 
		 * ========
		 * Použití:
		 * ========
		 * Ve třídě, kde chcete logovat si vytvořte proměnnou typu Logger:
		 * private static Logger logger = Logger.getLogger(clazz);
		 * Jako parametr metody getLogger dejte název danné třídy, pro kterou budete logovat (zde TestLogging.class)
		 * Název třídy se pak zobrazuje ve výpisu a je možné lehce dohledat, které třídě výpis patří.
		 */

		logger.debug("Debug message");
		logger.info("Info message");
		logger.warn("Warn message");
		logger.error("Error message");
		logger.fatal("Fatal message");
	}
	
	/**
	 * Testovací metoda. Automaticky loguje každých "sleepTime" milisekund 
	 * Pro spuštění klikněte pravým tlačítkem na třídu -> run as -> jUnit test.
	 * Tato metoda je defaultně zakomentovaná.
	 */
//	@Test
//	public void testLoop() {
//		int sleepTime = 10_000;
//		int numOfRuns = 30;
//		
//		for (int i = 0; i < numOfRuns; i++) {
//			logger.debug("Debug message " + i);
//			logger.info("Info message " + i);
//			logger.warn("Warn message " + i);
//			logger.error("Error message " + i);
//			logger.fatal("Fatal message " + i);
//			
//			try {
//				Thread.sleep(sleepTime);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//	}
}
