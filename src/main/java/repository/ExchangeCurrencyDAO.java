package repository;

import model.DTO.ExchangeRatePatchDTO;
import model.ExchangeRate;
import org.springframework.jdbc.core.JdbcTemplate;
import repository.sqlMappers.ExchangeRateRowMapper;

import java.util.List;

public class ExchangeCurrencyDAO {

    private final JdbcTemplate jdbcTemplate;

    public ExchangeCurrencyDAO(JdbcTemplate jdbcTemplate) {
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

        return jdbcTemplate.query(query, new ExchangeRateRowMapper());
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

        return jdbcTemplate.query(query,
                        new Object[]{baseCurrencyCode, targetCurrencyCode},
                        new ExchangeRateRowMapper())
                .stream().findAny().orElse(null);
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

        jdbcTemplate.update(query, rate, baseCode, targetCode);
    }

    public void create(ExchangeRatePatchDTO newRate) {
        String query = """
                INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate)
                VALUES (
                (SELECT id FROM Currencies WHERE code = ?),
                (SELECT id FROM Currencies WHERE code = ?),
                ?)""";

        jdbcTemplate.update(query, newRate.getBaseCurrencyCode(), newRate.getTargetCurrencyCode(), newRate.getRate());
    }
}
