package servise;

import exceptions.NoDataFoundException;
import model.Currency;
import model.DTO.NewCurrencyDTO;
import model.mapper.CurrencyMapper;
import repository.CurrencyDAO;
import validator.InputValidator;

import java.util.List;

public class CurrencyService extends BaseService {

    private CurrencyDAO currencyDAO = new CurrencyDAO(connectionUtil.jdbcTemplate());

    public List<Currency> getAll() {
        return currencyDAO.findAll();
    }

    public Currency addCurrency(NewCurrencyDTO newCurrencyDTO) {
        Currency currency = CurrencyMapper.mapCurrencyDtoToCurrency(newCurrencyDTO);

        InputValidator.validateCurrencyCodeMatchesPattern(currency.getCode());

        return currencyDAO.create(currency);
    }

    public Currency getCurrencyByCode(String currencyCode) {
        return  currencyDAO.getCurrencyByCode(currencyCode)
                .orElseThrow(() -> new NoDataFoundException("No currencies found with Code: " + currencyCode));
    }
}
