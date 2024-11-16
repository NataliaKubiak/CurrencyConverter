package servise;

import model.Currency;
import repository.CurrencyDAO;
import testStuff.testDAO.Config;

import java.util.List;

public class CurrencyService {

    Config config = new Config();
    CurrencyDAO currencyDAO = new CurrencyDAO(config.jdbcTemplate());

    public List<Currency> getAll() {
        return currencyDAO.all();
    }
}
