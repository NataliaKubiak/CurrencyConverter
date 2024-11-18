package repository.sqlMappers;

import model.Currency;
import model.ExchangeRate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ExchangeRateRowMapper implements RowMapper<ExchangeRate> {
    @Override
    public ExchangeRate mapRow(ResultSet rs, int rowNum) throws SQLException {

        Currency baseCurrency = new Currency(
                rs.getInt("base_currency_id"), //id
                rs.getString("base_currency_code"), //code
                rs.getString("base_currency_name"), //name
                rs.getString("base_currency_sign") //sign
        );

        Currency targetCurrency = new Currency(
                rs.getInt("target_currency_id"), //id
                rs.getString("target_currency_code"), //code
                rs.getString("target_currency_name"), //name
                rs.getString("target_currency_sign") //sign
        );

        return new ExchangeRate(
                rs.getInt("exchange_rate_id"),
                baseCurrency,
                targetCurrency,
                rs.getDouble("exchange_rate")
        );
    }
}
