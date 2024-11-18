package servlet.currency;

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
import java.util.Locale;

public class GetCurrencyServlet extends HttpServlet {

    private CurrencyService currencyService;

    private static final String APPLICATION_JSON = "application/json";
    private static final String ENDPOINT_REGEX = "[A-Z]{3}";

    @Override
    public void init(ServletConfig config) {
        currencyService = new CurrencyService();
    }

    //Успех - 200 +
    //Код валюты отсутствует в адресе - 400 +
    //Валюта не найдена - 404
    //Ошибка (например, база данных недоступна) - 500
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String currencyCode = request.getPathInfo().substring(1).toUpperCase(Locale.ROOT);

        if (!currencyCode.matches(ENDPOINT_REGEX)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); //400
            setResponseText(response, "Invalid currency code in URI. Currency code should consist of 3 letters like: USD, EUR, etc.");
            return;
        }

        Currency currency = currencyService.getCurrencyByCode(currencyCode);
        String json = objectToJson(currency);

        response.setContentType(APPLICATION_JSON);
        setResponseText(response, json);
    }

    private void setResponseText(HttpServletResponse response, String responseText) {
        try {
            response.getWriter().println(responseText);
        } catch (IOException ex) {
            ex.printStackTrace();;
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //500
            try {
                response.getWriter().println("An internal server error occurred. Please try again later.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String objectToJson(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        String json;

        try {
            json = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json;
    }
}
