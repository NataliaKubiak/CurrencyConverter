package repository;

import model.Currency;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class CurrencyDAO {

    private final JdbcTemplate jdbcTemplate;

    public CurrencyDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Currency> all() {
        return jdbcTemplate.query("SELECT * FROM Currencies", new BeanPropertyRowMapper<>(Currency.class));
    }

    public void create(Currency currency) {
        jdbcTemplate.update("INSERT INTO Currencies (code, name, sign) VALUES (?, ?, ?)",
                currency.getCode(), currency.getName(), currency.getSign());
    }

    public Currency getCurrencyByCode(String currencyCode) {
        return jdbcTemplate.query("SELECT * FROM Currencies WHERE code = ?",
                new Object[]{currencyCode}, new BeanPropertyRowMapper<>(Currency.class))
                .stream().findAny().orElse(null);
    }
}
