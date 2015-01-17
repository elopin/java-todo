package cz.iivos.todo.database;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.junit.Test;

import cz.iivos.todo.logging.TestLogging;

/**
 * Class that test database connection and works with LOGS table.
 * 
 * @author Vít Foldyna
 *
 */
public class TestLogsTable {
	
	private static Logger logger = Logger.getLogger(TestLogsTable.class);
	private static final String TESTING_STRING = "Testing LOGS table";
	
	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Simple test for logging to the LOGS table.
	 */
	@Test
	public void test() {
		ResourceBundle properties = ResourceBundle.getBundle("dbconnection", new Locale("cz"));
		
		try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://"
                + properties.getString("url")
                + "/"
                + properties.getString("dbname")
                + "?useUnicode=true&characterEncoding=UTF8&zeroDateTimeBehavior=convertToNull",
                properties.getString("user"),
                properties.getString("password"));) {
			
			assertNotNull(con);
			
			// logging message to a LOGS table
			logger.debug(TESTING_STRING);
			
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select user_id, dated, logger, level, message from LOGS");
			
			// this loop might be commented, it is used just for rows printing
			while (rs.next()) {
				System.out.print(rs.getLong("user_id") + "\t");
				System.out.print(rs.getString("dated") + "\t");
				System.out.print(rs.getString("logger") + "\t");
				System.out.print(rs.getString("level") + "\t");
				System.out.print(rs.getString("message"));
				System.out.println();
			}
			
			// getting last row id without while loop (without printing out all rows)
			boolean hasLast = rs.last();
			
			// there must be at least one row in result set (we inserted one message at the beginning of the test)
			assertTrue(hasLast);
			// the last row message should equal String in testingString variable
			assertEquals("The message should equals " + TESTING_STRING, rs.getString("message"), TESTING_STRING);
			// the last row message should heve logging level DEBUG
			assertEquals("The log level should be DEBUG", rs.getString("level"), "DEBUG");
			
		} catch (SQLException e) {
			e.printStackTrace();
			fail("SQLException");
		}
	}
}
