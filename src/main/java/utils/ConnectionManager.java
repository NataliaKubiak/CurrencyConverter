package utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConnectionManager {

    private static final String SQLITE_DRIVER = "org.sqlite.JDBC";
    private static String SQLITE_PATH;
    private static String USERNAME;
    private static String PASSWORD;

    private static final int MAX_POOL_SIZE = 10;

    public DataSource getDataSource() {
        loadProperties();

        try {
            Class.forName(SQLITE_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC driver not found", e);
        }

        var hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl(SQLITE_PATH);
        hikariConfig.setUsername(USERNAME);
        hikariConfig.setPassword(PASSWORD);

        hikariConfig.setMaximumPoolSize(MAX_POOL_SIZE);

        return new HikariDataSource(hikariConfig);
    }

    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(getDataSource());
    }

    private void loadProperties() {
        Properties properties = new Properties();

        try (InputStream input = ConnectionManager.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("application.properties not found in resources");
            }

            properties.load(input);
            SQLITE_PATH = properties.getProperty("db.path");
            USERNAME = properties.getProperty("db.username");
            PASSWORD = properties.getProperty("db.password");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
