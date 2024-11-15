package testStuff.testDAO;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

public class Config {

    private static final String SQLITE_DRIVER = "org.sqlite.JDBC";
    private static final String SQLITE_PATH = "jdbc:sqlite:db/mydatabase.db";

    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName(SQLITE_DRIVER);
        dataSource.setUrl(SQLITE_PATH);
        dataSource.setUsername("");
        dataSource.setPassword("");

        return dataSource;
    }

    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }
}
