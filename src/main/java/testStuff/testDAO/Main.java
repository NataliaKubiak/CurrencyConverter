package testStuff.testDAO;

import repository.CurrencyDAO;

public class Main {
    public static void main(String[] args) {
        Config config = new Config();
        CurrencyDAO currencyDAO = new CurrencyDAO(config.jdbcTemplate());

        System.out.println(currencyDAO.all());
    }
}
