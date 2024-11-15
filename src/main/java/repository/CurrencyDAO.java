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

}
