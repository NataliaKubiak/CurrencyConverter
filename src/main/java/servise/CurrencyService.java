package servise;

import model.Currency;
import model.DTO.CurrencyAdditionDTO;
import model.mapper.CurrencyAdditionMapper;
import repository.CurrencyDAO;

import java.util.List;

public class CurrencyService extends BaseService {

    private CurrencyDAO currencyDAO = new CurrencyDAO(connectionUtil.jdbcTemplate());

    public List<Currency> getAll() {
        return currencyDAO.all();
    }

    public Currency addCurrency(CurrencyAdditionDTO currencyAdditionDTO) {
        Currency mappedDtoToCurrency = CurrencyAdditionMapper.mapDtoToObject(currencyAdditionDTO);

        //if no - throw BusinessLogicException
        //if yes - go further
        isCurrencyCodeMatchesPattern(mappedDtoToCurrency.getCode());

        addCurrencyToDB(mappedDtoToCurrency);
        return getCurrencyByCode(mappedDtoToCurrency.getCode());
    }

    private void addCurrencyToDB(Currency newCurrency) {
        currencyDAO.create(newCurrency);
    }

    public Currency getCurrencyByCode(String currencyCode) {
        return  currencyDAO.getCurrencyByCode(currencyCode);
    }
}
