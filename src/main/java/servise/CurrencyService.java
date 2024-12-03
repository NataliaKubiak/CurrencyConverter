package servise;

import exceptions.NoDataFoundException;
import model.Currency;
import model.DTO.NewCurrencyDTO;
import model.mapper.CurrencyMapper;
import repository.CurrencyDAO;

import java.util.List;

public class CurrencyService extends BaseService {

    private CurrencyDAO currencyDAO = new CurrencyDAO(connectionUtil.jdbcTemplate());

    public List<Currency> getAll() {
        if (true) {
            throw new RuntimeException();
        }
        return currencyDAO.findAll();
    }

    public Currency addCurrency(NewCurrencyDTO newCurrencyDTO) {
        Currency currency = CurrencyMapper.mapDtoToObject(newCurrencyDTO);

        //if no - throw BusinessLogicException
        //if yes - go further
        isCurrencyCodeMatchesPattern(currency.getCode());

        return currencyDAO.create(currency);
    }

    public Currency getCurrencyByCode(String currencyCode) {
        return  currencyDAO.getCurrencyByCode(currencyCode)
                .orElseThrow(() -> new NoDataFoundException("No currencies found with Code: " + currencyCode));
    }
}
