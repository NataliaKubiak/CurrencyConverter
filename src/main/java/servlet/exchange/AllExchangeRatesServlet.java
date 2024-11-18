package servlet.exchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ExchangeRate;
import org.springframework.dao.DataAccessException;
import servise.ExchangeService;

import java.io.IOException;
import java.util.List;

public class AllExchangeRatesServlet extends HttpServlet {

    private ExchangeService exchangeService;

    private static final String APPLICATION_JSON = "application/json";

    @Override
    public void init(ServletConfig config) {
        exchangeService = new ExchangeService();
    }

    //Успех - 200
    //Ошибка (например, база данных недоступна) - 500
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<ExchangeRate> allRates = exchangeService.getAll();
            response.setContentType(APPLICATION_JSON);
            String allRatesJson = objectToJson(allRates);

            setResponseText(response, allRatesJson);

        } catch (DataAccessException ex) {
            ex.printStackTrace();

            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE); //503
            setResponseText(response, "Database is unavailable. Please try again later.");
        }
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
