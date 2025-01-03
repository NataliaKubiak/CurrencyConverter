package repository;

import exceptions.DuplicateDataException;
import model.Currency;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.sqlite.SQLiteException;
import utils.ConnectionManager;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

public class CurrencyDAO {

    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_ALL_SQL = """
            SELECT * FROM Currencies
            """;
    private static final String CREATE_SQL = """
            INSERT INTO Currencies (code, name, sign)
            VALUES (?, ?, ?)
            """;
    private static final String SELECT_BY_CODE_SQL = """
            SELECT * FROM Currencies
            WHERE code = ?
            """;

    public CurrencyDAO() {
        this.jdbcTemplate = new JdbcTemplate(ConnectionManager.getDataSource());
    }

    public List<Currency> findAll() {
        try {
            return jdbcTemplate.query(SELECT_ALL_SQL, new BeanPropertyRowMapper<>(Currency.class));

        } catch (DataAccessException ex) {
            throw new RuntimeException("Database operation failed: " + ex.getMessage(), ex);
        }
    }

    public Currency create(Currency currency) {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(CREATE_SQL, new String[]{"id"});
                ps.setString(1, currency.getCode());
                ps.setString(2, currency.getName());
                ps.setString(3, currency.getSign());
                return ps;
            }, keyHolder);

            Number id = keyHolder.getKey();
            if (id != null) {
                currency.setId(id.intValue());
            }

            return currency;

        } catch (DataAccessException ex) {
            if (ex.getCause() instanceof SQLiteException
                    && ex.getMessage().contains("UNIQUE constraint failed")) {
                throw new DuplicateDataException("Currency with code " + currency.getCode() + " already exists.", ex);
            }
            throw new RuntimeException("Database operation failed: " + ex.getMessage(), ex);
        }
    }

    public Optional<Currency> getCurrencyByCode(String currencyCode) {
        try {
            return jdbcTemplate.query(SELECT_BY_CODE_SQL,
                            new Object[]{currencyCode}, new BeanPropertyRowMapper<>(Currency.class))
                    .stream().findAny();

        } catch (DataAccessException ex) {
            throw new RuntimeException("Database operation failed: " + ex.getMessage(), ex);
        }
    }
}
