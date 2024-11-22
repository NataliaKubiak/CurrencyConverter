package repository;

import exceptions.DuplicateDataException;
import exceptions.NoDataFoundException;
import model.DTO.ExchangeRateDTO;
import model.ExchangeRate;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.sqlite.SQLiteException;
import repository.sqlMappers.ExchangeRateRowMapper;

import java.util.List;

public class ExchangeDAO {

    private final JdbcTemplate jdbcTemplate;

    public ExchangeDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ExchangeRate> getAllRates() {
        String query = """
                SELECT
                    er.id AS exchange_rate_id,
                    bc.id AS base_currency_id,
                    bc.name AS base_currency_name,
                    bc.code AS base_currency_code,
                    bc.sign AS base_currency_sign,
                    tc.id AS target_currency_id,
                    tc.name AS target_currency_name,
                    tc.code AS target_currency_code,
                    tc.sign AS target_currency_sign,
                    er.rate AS exchange_rate
                FROM
                    ExchangeRates er
                JOIN
                    Currencies bc ON er.BaseCurrencyId = bc.id
                JOIN
                    Currencies tc ON er.TargetCurrencyId = tc.id""";

        try {
            return jdbcTemplate.query(query, new ExchangeRateRowMapper());

        } catch (DataAccessException ex) {
            throw new RuntimeException("Database operation failed: " + ex.getMessage(), ex);
        }
    }

    public ExchangeRate getRateByCurrencyCodes(String baseCurrencyCode, String targetCurrencyCode) {
        String query = """
                SELECT
                    er.id AS exchange_rate_id,
                    bc.id AS base_currency_id,
                    bc.name AS base_currency_name,
                    bc.code AS base_currency_code,
                    bc.sign AS base_currency_sign,
                    tc.id AS target_currency_id,
                    tc.name AS target_currency_name,
                    tc.code AS target_currency_code,
                    tc.sign AS target_currency_sign,
                    er.rate AS exchange_rate
                FROM
                    ExchangeRates er
                JOIN
                    Currencies bc ON er.BaseCurrencyId = bc.id
                JOIN
                    Currencies tc ON er.TargetCurrencyId = tc.id
                WHERE
                    base_currency_code = ? AND target_currency_code = ?""";

        try {
            return jdbcTemplate.query(query,
                            new Object[]{baseCurrencyCode, targetCurrencyCode},
                            new ExchangeRateRowMapper())
                    .stream().findAny()
                    .orElseThrow(() -> new NoDataFoundException("No currencies found with Codes: " + baseCurrencyCode + ", " + targetCurrencyCode));

        } catch (DataAccessException ex) {
            throw new RuntimeException("Database operation failed: " + ex.getMessage(), ex);
        }
    }

    public void updateExchangeRate(double rate, String baseCode, String targetCode) {
        String query = """
                UPDATE ExchangeRates
                SET Rate = ?
                WHERE BaseCurrencyId = (
                    SELECT id FROM Currencies WHERE code = ?
                )
                AND TargetCurrencyId = (
                    SELECT id FROM Currencies WHERE code = ?
                )""";

        try {
            int rowsAffected = jdbcTemplate.update(query, rate, baseCode, targetCode);

            if (rowsAffected == 0) {
                throw new NoDataFoundException("No currencies found with Codes: " + baseCode + ", " + targetCode);
            }

        } catch (DataAccessException ex) {
            throw new RuntimeException("Database operation failed: " + ex.getMessage(), ex);
        }
    }

    public void create(ExchangeRateDTO newRate) {
        String baseCode = newRate.getBaseCurrencyCode();
        String targetCode = newRate.getTargetCurrencyCode();

        String query = """
                INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate)
                VALUES (
                (SELECT id FROM Currencies WHERE code = ?),
                (SELECT id FROM Currencies WHERE code = ?),
                ?)""";

        try {
            jdbcTemplate.update(query, baseCode, targetCode, newRate.getRate());
        } catch (DataAccessException ex) {
            if (ex.getCause() instanceof SQLiteException
                    && ex.getMessage().contains("UNIQUE constraint failed")) {
                throw new DuplicateDataException("Currency pare with codes: Base Currency Code = " + baseCode
                        + " Target Currency Code = " + targetCode + " already exists.", ex);
            }

            if (ex.getMessage().contains("NULL")) {
                throw new NoDataFoundException("No currencies found with Codes: " + baseCode + ", " + targetCode);
            }

            throw new RuntimeException("Database operation failed: " + ex.getMessage(), ex);
        }
    }
}
