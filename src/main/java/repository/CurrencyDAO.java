package repository;

import exceptions.DuplicateDataException;
import model.Currency;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.sqlite.SQLiteException;

import java.util.List;

public class CurrencyDAO {

    private final JdbcTemplate jdbcTemplate;

    public CurrencyDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Currency> all() {
        try {
            return jdbcTemplate.query("SELECT * FROM Currencies", new BeanPropertyRowMapper<>(Currency.class));

        } catch (DataAccessException ex) {
            throw new RuntimeException("Database operation failed: " + ex.getMessage(), ex);
        }
    }

    public void create(Currency currency) {
        try {
            jdbcTemplate.update("INSERT INTO Currencies (code, name, sign) VALUES (?, ?, ?)",
                    currency.getCode(), currency.getName(), currency.getSign());

        } catch (DataAccessException ex) {
            if (ex.getCause() instanceof SQLiteException
                    && ex.getMessage().contains("UNIQUE constraint failed")) {
                throw new DuplicateDataException("Currency with code " + currency.getCode() + " already exists.", ex);
            }
            throw new RuntimeException("Database operation failed: " + ex.getMessage(), ex);
        }
    }

    public Currency getCurrencyByCode(String currencyCode) {
        try {
            return jdbcTemplate.query("SELECT * FROM Currencies WHERE code = ?",
                            new Object[]{currencyCode}, new BeanPropertyRowMapper<>(Currency.class))
                    .stream().findAny().orElse(null);

        } catch (DataAccessException ex) {
            throw new RuntimeException("Database operation failed: " + ex.getMessage(), ex);
        }
    }
}
