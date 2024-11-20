package servise;

import exceptions.BusinessLogicException;
import exceptions.NoDataFoundException;
import model.DTO.ExchangeRateDTO;
import model.ExchangeRate;
import repository.ExchangeDAO;
import utils.Config;

import java.util.List;

public class ExchangeService {

    Config config = new Config();
    ExchangeDAO exchangeDAO = new ExchangeDAO(config.jdbcTemplate());

    public List<ExchangeRate> getAll() {
        return exchangeDAO.getAllRates();
    }

    public ExchangeRate getExchangeRate(String baseCode, String targetCode) {
        ExchangeRate rate = exchangeDAO.getRateByCurrencyCodes(baseCode, targetCode);

        if (rate == null) {
            throw new NoDataFoundException("No currencies found with Codes: " + baseCode + ", " + targetCode);
        }
        return rate;
    }

    public ExchangeRate patchExchangeRate(ExchangeRateDTO exchangeRateDTO) {
        String baseCurrencyCode = exchangeRateDTO.getBaseCurrencyCode();
        String targetCurrencyCode = exchangeRateDTO.getTargetCurrencyCode();

        String codeRegex = "[A-Z]{3}";

        if (!baseCurrencyCode.matches(codeRegex)
                || !targetCurrencyCode.matches(codeRegex)) {
            throw new BusinessLogicException("Invalid currency code.");
        }

        int rowsAffected = exchangeDAO.updateExchangeRate(
                exchangeRateDTO.getRate(),
                baseCurrencyCode,
                targetCurrencyCode
        );

        if (rowsAffected == 0) {
            throw new NoDataFoundException("No currencies found with Codes: " + baseCurrencyCode + ", " + targetCurrencyCode);
        }

        return exchangeDAO.getRateByCurrencyCodes(baseCurrencyCode, targetCurrencyCode);
    }

    public ExchangeRate addExchangeRate(ExchangeRateDTO exchangeRateDTO) {
        String baseCurrencyCode = exchangeRateDTO.getBaseCurrencyCode();
        String targetCurrencyCode = exchangeRateDTO.getTargetCurrencyCode();

        String codeRegex = "[A-Z]{3}";

        if (!baseCurrencyCode.matches(codeRegex)
                || !targetCurrencyCode.matches(codeRegex)) {
            throw new BusinessLogicException("Invalid currency code.");
        }

        exchangeDAO.create(exchangeRateDTO);

        return getExchangeRate(exchangeRateDTO.getBaseCurrencyCode(),
                exchangeRateDTO.getTargetCurrencyCode());
    }
}
