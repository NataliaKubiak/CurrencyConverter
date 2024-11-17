package servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Currency;
import model.DTO.CurrencyAdditionDTO;
import model.mapper.CurrencyAdditionMapper;
import org.springframework.dao.DataAccessException;
import servise.CurrencyService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class AllCurrenciesServlet extends HttpServlet {

    private CurrencyService currencyService;

    private static final String APPLICATION_JSON = "application/json";

    @Override
    public void init(ServletConfig config) {
        currencyService = new CurrencyService();
    }

    //Успех - 200 +
    //Ошибка (например, база данных недоступна) - 500 + (в методе setResponseText)
    //503 + БД недоступна
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            List<Currency> allCurrenciesList = currencyService.getAll();
            response.setContentType(APPLICATION_JSON);
            String allCurrenciesJson = objectToJson(allCurrenciesList);

            setResponseText(response, allCurrenciesJson);

        } catch (DataAccessException ex) {
            ex.printStackTrace();

            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE); //503
            setResponseText(response, "Database is unavailable. Please try again later.");
        }
    }

    //Успех - 201 +
    //Отсутствует нужное поле формы - 400 +
    //TODO написать логику на этот кейс
    //Валюта с таким кодом уже существует - 409
    //Ошибка (например, база данных недоступна) - 500 + (в методе setResponseText)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String requiredContentType = "application/x-www-form-urlencoded";

        if (!requiredContentType.equals(request.getContentType()) || request.getContentType() == null) {
            response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE); //415
            setResponseText(response, "Invalid Content-Type. Expected application/x-www-form-urlencoded.");
            return;
        }

        Optional<CurrencyAdditionDTO> optionalCurrencyAdditionDTO = CurrencyAdditionMapper.mapRequestToDto(request);

        if (optionalCurrencyAdditionDTO.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); //400
            setResponseText(response, "Invalid request parameters. One or many parameters are empty."); //Отсутствует нужное поле формы

        } else {
            Currency currency = currencyService.addCurrency(optionalCurrencyAdditionDTO.get());
            String json = objectToJson(currency);

            response.setStatus(HttpServletResponse.SC_CREATED); //201
            setResponseText(response, json);
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
