package servlet.exchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.DTO.ExchangeRatePatchDTO;
import model.ExchangeRate;
import model.mapper.ExchangeRatePatchMapper;
import org.springframework.dao.DataAccessException;
import servise.ExchangeService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String requiredContentType = "application/x-www-form-urlencoded";

        if (!requiredContentType.equals(request.getContentType()) || request.getContentType() == null) {
            response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE); //415
            setResponseText(response, "Invalid Content-Type. Expected application/x-www-form-urlencoded.");
            return;
        }

        Optional<ExchangeRatePatchDTO> optionalExchangeRatePatchDTO = ExchangeRatePatchMapper.mapRequestToPostDto(request);

        if (optionalExchangeRatePatchDTO.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); //400
            setResponseText(response, "Invalid request parameters. One or many parameters are empty."); //Отсутствует нужное поле формы

        } else {
            ExchangeRate exchangeRate = exchangeService.addExchangeRate(optionalExchangeRatePatchDTO.get());
            String json = objectToJson(exchangeRate);

            response.setContentType(APPLICATION_JSON);
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
