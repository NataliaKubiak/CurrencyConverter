package servise;

import model.Currency;
import model.DTO.CurrencyAdditionDTO;
import model.mapper.CurrencyAdditionMapper;
import repository.CurrencyDAO;
import testStuff.testDAO.Config;

import java.util.List;

public class CurrencyService {

    Config config = new Config();
    CurrencyDAO currencyDAO = new CurrencyDAO(config.jdbcTemplate());

    public List<Currency> getAll() {
        return currencyDAO.all();
    }

    public Currency addCurrency(CurrencyAdditionDTO currencyAdditionDTO) {
        Currency mappedDtoToCurrency = CurrencyAdditionMapper.mapDtoToObject(currencyAdditionDTO);

        addCurrencyToDB(mappedDtoToCurrency);
        return getCurrencyObjWithId(mappedDtoToCurrency.getCode());
    }

    private void addCurrencyToDB(Currency newCurrency) {
        currencyDAO.create(newCurrency);
    }

    private Currency getCurrencyObjWithId(String currencyCode) {
        return currencyDAO.getCurrencyByCode(currencyCode);
    }
}
