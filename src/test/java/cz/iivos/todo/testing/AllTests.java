package cz.iivos.todo.testing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * This is test suite.
 * Running this class (run as -> jUnit test) will run all test cases (classes with jUnit tests).
 * @author Vít Foldyna
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ DefaultTest.class, DefaultTest2.class })
public class AllTests {

}
