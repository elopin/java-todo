package cz.iivos.todo.database;

import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;

import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import org.apache.log4j.Logger;

/**
 * Trieda, ktora sa stara o pripojenie k DB. connectionpool ma synchronizaciu
 * implicitne vyriesenu, preto sa o nu viac netreba starat. Je tu len pre
 * potrebu SQLContaineru.
 *
 * @author veres
 *
 *
 */
public class DoDbConn {

    private final static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    
    private static JDBCConnectionPool connectionPool = null;
    
    private final static Logger logger = Logger.getLogger(DoDbConn.class.getName());

    static {
        Locale locale = new Locale("cz", "CZ");
        ResourceBundle properties = ResourceBundle.getBundle("dbconnection",
            locale);
        connectToDb(JDBC_DRIVER, "jdbc:mysql://" + properties.getString("url")
            + "/" + properties.getString("dbname") + "?useUnicode=true&characterEncoding=UTF8&zeroDateTimeBehavior=convertToNull",
            properties.getString("user"), properties.getString("password"),
            2, 55);

    }

    // 1.
    /**
     * Metoda která tvoří spojení s DB ve formě pool-u jednotlivých spojení.
     */
    private static void connectToDb(String dbDriver, String dbURL,
        String dbUser, String dbPwd, int iniCon, int maxCon) {
        try {
            connectionPool = new SimpleJDBCConnectionPool(dbDriver, dbURL,
                dbUser, dbPwd, iniCon, maxCon);
        } catch (SQLException e) {
            logger.warn(e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    // 2.
    /**
     * Metoda vracející SQLCaontainer
     * 
     * 
     * @param tableName Jméno DB tabulky.
     * @return sqlCOntainer
     * @throws java.sql.SQLException
     */
    public static SQLContainer getContainer(String tableName)
        throws SQLException {

		TableQuery q1 = new TableQuery(tableName, connectionPool);
		q1.setVersionColumn("id_tak");
		SQLContainer tableContainer = new SQLContainer(q1);
        
        return tableContainer;
    }
}
