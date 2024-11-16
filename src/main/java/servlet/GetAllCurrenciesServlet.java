package servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Currency;
import servise.CurrencyService;

import java.io.IOException;
import java.util.List;

public class GetAllCurrenciesServlet extends HttpServlet {

    private CurrencyService currencyService;

    private static final String APPLICATION_JSON = "application/json";

    @Override
    public void init(ServletConfig config) throws ServletException {
        currencyService = new CurrencyService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<Currency> allCurrenciesList = currencyService.getAll();
        response.setContentType(APPLICATION_JSON);
        String allCurrenciesJson = listToJson(allCurrenciesList);

        response.getWriter().println(allCurrenciesJson);
    }

    private String listToJson(List<Currency> currencyList) {
        ObjectMapper objectMapper = new ObjectMapper();
        String json;

        try {
            json = objectMapper.writeValueAsString(currencyList);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json;
    }
}