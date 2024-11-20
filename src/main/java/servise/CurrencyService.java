package servise;

import exceptions.BusinessLogicException;
import exceptions.NoDataFoundException;
import model.Currency;
import model.DTO.CurrencyAdditionDTO;
import model.mapper.CurrencyAdditionMapper;
import repository.CurrencyDAO;
import utils.Config;

import java.util.List;

public class CurrencyService {

    Config config = new Config();
    CurrencyDAO currencyDAO = new CurrencyDAO(config.jdbcTemplate());

    public List<Currency> getAll() {
        return currencyDAO.all();
    }

    public Currency addCurrency(CurrencyAdditionDTO currencyAdditionDTO) {
        Currency mappedDtoToCurrency = CurrencyAdditionMapper.mapDtoToObject(currencyAdditionDTO);

        String codeRegex = "[A-Z]{3}";
        if (!mappedDtoToCurrency.getCode().matches(codeRegex)) {
            throw new BusinessLogicException("Invalid currency code.");
        }

        addCurrencyToDB(mappedDtoToCurrency);
        return getCurrencyByCode(mappedDtoToCurrency.getCode());
    }

    private void addCurrencyToDB(Currency newCurrency) {
        currencyDAO.create(newCurrency);
    }

    public Currency getCurrencyByCode(String currencyCode) {
        Currency currency = currencyDAO.getCurrencyByCode(currencyCode);

        if (currency == null) {
            throw new NoDataFoundException("No currencies found with Code: " + currencyCode);
        }
        return currency;
    }
}
