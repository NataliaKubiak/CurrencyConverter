package utils;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    private static final String SQLITE_DRIVER = "org.sqlite.JDBC";
    private static String SQLITE_PATH;
    private static String USERNAME;
    private static String PASSWORD;

    public DataSource dataSource() {
        loadProperties();
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName(SQLITE_DRIVER);
        dataSource.setUrl(SQLITE_PATH);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);

        return dataSource;
    }

    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    private void loadProperties() {
        Properties properties = new Properties();

        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("config.properties not found in resources");
            }

            properties.load(input);
            SQLITE_PATH = properties.getProperty("DB.PATH");
            USERNAME = properties.getProperty("DB.USERNAME");
            PASSWORD = properties.getProperty("DB.PASSWORD");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
