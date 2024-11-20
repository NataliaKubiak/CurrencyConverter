package servlet.exchange;

import jakarta.servlet.ServletConfig;
import servise.ExchangeService;
import servlet.BaseServlet;

public class BaseExchangeRateServlet extends BaseServlet {

    ExchangeService exchangeService;

    @Override
    public void init(ServletConfig config) {
        exchangeService = new ExchangeService();
    }
}
