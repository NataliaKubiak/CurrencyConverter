package servise;

import model.DTO.ExchangeRatePatchDTO;
import model.ExchangeRate;
import repository.ExchangeCurrencyDAO;
import testStuff.testDAO.Config;

import java.util.List;

public class ExchangeService {

    Config config = new Config();
    ExchangeCurrencyDAO exchangeCurrencyDAO = new ExchangeCurrencyDAO(config.jdbcTemplate());

    public List<ExchangeRate> getAll() {
        return exchangeCurrencyDAO.getAllRates();
    }

    public ExchangeRate getExchangeRate(String baseCode, String targetCode) {
        return exchangeCurrencyDAO.getRateByCurrencyCodes(baseCode, targetCode);
    }

    public ExchangeRate patchExchangeRate(ExchangeRatePatchDTO exchangeRatePatchDTO) {
        String baseCurrencyCode = exchangeRatePatchDTO.getBaseCurrencyCode();
        String targetCurrencyCode = exchangeRatePatchDTO.getTargetCurrencyCode();

        exchangeCurrencyDAO.updateExchangeRate(
                exchangeRatePatchDTO.getRate(),
                baseCurrencyCode,
                targetCurrencyCode
        );

        return exchangeCurrencyDAO.getRateByCurrencyCodes(baseCurrencyCode, targetCurrencyCode);
    }

    public ExchangeRate addExchangeRate(ExchangeRatePatchDTO exchangeRatePatchDTO) {
        exchangeCurrencyDAO.create(exchangeRatePatchDTO);

        return getExchangeRate(exchangeRatePatchDTO.getBaseCurrencyCode(),
                exchangeRatePatchDTO.getTargetCurrencyCode());
    }
}
