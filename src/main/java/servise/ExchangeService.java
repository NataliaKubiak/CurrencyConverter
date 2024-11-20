package servise;

import model.DTO.ExchangeRateDTO;
import model.ExchangeRate;
import repository.ExchangeDAO;
import testStuff.testDAO.Config;

import java.util.List;

public class ExchangeService {

    Config config = new Config();
    ExchangeDAO exchangeDAO = new ExchangeDAO(config.jdbcTemplate());

    public List<ExchangeRate> getAll() {
        return exchangeDAO.getAllRates();
    }

    public ExchangeRate getExchangeRate(String baseCode, String targetCode) {
        return exchangeDAO.getRateByCurrencyCodes(baseCode, targetCode);
    }

    public ExchangeRate patchExchangeRate(ExchangeRateDTO exchangeRateDTO) {
        String baseCurrencyCode = exchangeRateDTO.getBaseCurrencyCode();
        String targetCurrencyCode = exchangeRateDTO.getTargetCurrencyCode();

        exchangeDAO.updateExchangeRate(
                exchangeRateDTO.getRate(),
                baseCurrencyCode,
                targetCurrencyCode
        );

        return exchangeDAO.getRateByCurrencyCodes(baseCurrencyCode, targetCurrencyCode);
    }

    public ExchangeRate addExchangeRate(ExchangeRateDTO exchangeRateDTO) {
        exchangeDAO.create(exchangeRateDTO);

        return getExchangeRate(exchangeRateDTO.getBaseCurrencyCode(),
                exchangeRateDTO.getTargetCurrencyCode());
    }
}
