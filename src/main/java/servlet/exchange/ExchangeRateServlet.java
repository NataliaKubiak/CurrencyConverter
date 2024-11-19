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
import servise.ExchangeService;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;

public class ExchangeRateServlet extends HttpServlet {

    ExchangeService exchangeService;

    private static final String APPLICATION_JSON = "application/json";
    private static final String ENDPOINT_REGEX = "[A-Z]{6}";

    private static final String HTTP_PATCH = "PATCH";
    private static final String HTTP_GET = "GET";

    @Override
    public void init(ServletConfig config) throws ServletException {
        exchangeService = new ExchangeService();
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (HTTP_GET.equals(request.getMethod())) {
            doGet(request, response);

        } else if (HTTP_PATCH.equals(request.getMethod())) {
             handlePatch(request, response);
        }
    }

    //Успех - 200 +
    //Коды валют пары отсутствуют в адресе - 400 +
    //Обменный курс для пары не найден - 404
    //Ошибка (например, база данных недоступна) - 500
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String rateCodes = request.getPathInfo().substring(1).toUpperCase(Locale.ROOT);

        if(!rateCodes.matches(ENDPOINT_REGEX)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); //400
            setResponseText(response, "Invalid currency code in URI. Currency code should consist of 6 letters like: USDEUR, EURRUB, etc.");
            return;
        }

        String baseCode = rateCodes.substring(0, 3);
        String targetCode = rateCodes.substring(3);

        ExchangeRate exchangeRate = exchangeService.getExchangeRate(baseCode, targetCode);
        String json = objectToJson(exchangeRate);

        response.setContentType(APPLICATION_JSON);
        setResponseText(response, json);
    }

    //PATCH
    //Успех - 200 +
    //Отсутствует нужное поле формы - 400 +
    //Валютная пара отсутствует в базе данных - 404
    //Ошибка (например, база данных недоступна) - 500
    protected void handlePatch(HttpServletRequest request, HttpServletResponse response) {
        String rateCodes = request.getPathInfo().substring(1).toUpperCase(Locale.ROOT);

        if(!rateCodes.matches(ENDPOINT_REGEX)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); //400
            setResponseText(response, "Invalid currency code in URI. Currency code should consist of 6 letters like: USDEUR, EURRUB, etc.");
            return;
        }

        String requiredContentType = "application/x-www-form-urlencoded";

        if (!requiredContentType.equals(request.getContentType()) || request.getContentType() == null) {
            response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE); //415
            setResponseText(response, "Invalid Content-Type. Expected application/x-www-form-urlencoded.");
            return;
        }

        String baseCode = rateCodes.substring(0, 3);
        String targetCode = rateCodes.substring(3);

        Optional<ExchangeRatePatchDTO> optionalExchangeRatePatchDTO = ExchangeRatePatchMapper.mapRequestToDto(request, baseCode, targetCode);

        if (optionalExchangeRatePatchDTO.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); //400
            setResponseText(response, "Invalid request");

        } else {
            ExchangeRate updatedExchangeRate = exchangeService.patchExchangeRate(optionalExchangeRatePatchDTO.get());
            String json = objectToJson(updatedExchangeRate);

            response.setContentType(APPLICATION_JSON);
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
