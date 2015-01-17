package cz.iivos.todo.testing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import cz.iivos.todo.model.Task;
import cz.iivos.todo.model.User;

/**
 * Testovací třída, na které je ukázáno použití jUnit pro testování.
 * @author Vít Foldyna
 *
 */
public class DefaultTest {
	private Task task;
	private User user;
	
	/**
	 * Tato metoda proběhne před metodou s anotací @Test.
	 * Používá se pro provedení činnosti nutné pro testy (pro set up).
	 */
	@Before
	public void createTestObject() {
		// Vytváření objektu task. Je použit pro ukázku testování.
		task = new Task();
		task.setId_tak(1234L);
		task.setTitle("My test title");
		
		// Vytváření objektu user. Je použit pro ukázku testování.
		user = new User();
		user.setPassword("1234");
		user.setEmail("admin@admin.cz");
	}

	/**
	 * Testovací metoda. Zde probíhá samotné testování. V této metodě je testován objekt task.
	 */
	@Test
	public void testTask() {
		Long id = task.getId_tak();
		String title = task.getTitle();
		
		assertNotNull(task);						// objekt task by neměl být null
		assertTrue(id == 1234L);					// id by se mělo rovnat Long 1234
		assertFalse(title.equals("abcdef"));		// titulek by se neměl rovnat řetězci 'abcdef'
		assertTrue(title.equals("My test title"));	// titulek by se měl rovnat řetězci 'My test title'
	}
	
	/**
	 * Metoda testující objekt user.
	 */
	@Test
	public void testUser() {
		String passwd = user.getPassword();
		String email = user.getEmail();
		
		assertNotNull(passwd);
		assertNull(user.getName());		// v metodě s anotací @Before se nenastoval, proto by měl být null
		assertNotNull(email);
		assertTrue(email.contains("@"));
	}
}
