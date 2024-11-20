package servlet.currency;

import jakarta.servlet.ServletConfig;
import servise.CurrencyService;
import servlet.BaseServlet;

public class BaseCurrencyServlet extends BaseServlet {

    protected CurrencyService currencyService;

    @Override
    public void init(ServletConfig config) {
        currencyService = new CurrencyService();
    }
}
