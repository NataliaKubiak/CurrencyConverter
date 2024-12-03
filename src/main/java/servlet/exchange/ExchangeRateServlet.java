package servlet.exchange;

import exceptions.ExceptionHandler;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.DTO.ExchangeRateDTO;
import model.ExchangeRate;
import model.mapper.ExchangeRateMapper;
import utils.Utils;

import java.util.Optional;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends BaseExchangeRateServlet {

    private static final String ENDPOINT_REGEX = "[A-Z]{6}";

    private static final String HTTP_PATCH = "PATCH";
    private static final String HTTP_GET = "GET";

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) {
        if (HTTP_GET.equals(request.getMethod())) {
            doGet(request, response);

        } else if (HTTP_PATCH.equals(request.getMethod())) {
            handlePatch(request, response);
        }
    }

    //Успех - 200 +
    //Коды валют пары отсутствуют в адресе - 400 +
    //Обменный курс для пары не найден - 404 +
    //Ошибка (например, база данных недоступна) - 500 +
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String rateCodes = Utils.extractCurrencyCodeFromURI(request);

        if (!rateCodes.matches(ENDPOINT_REGEX)) {
            ExceptionHandler.handleBadRequest(response,
                    "Invalid currency codes in URL. Currency code should consist of 3 letters like: USD, EUR, etc."); //400
            return;
        }

        String baseCode = rateCodes.substring(0, 3);
        String targetCode = rateCodes.substring(3);

        ExchangeRate exchangeRate = exchangeService.getExchangeRate(baseCode, targetCode);
        createSuccessfulGetResponse(response, exchangeRate); //200

    }

    //PATCH
    //Успех - 200 +
    //Отсутствует нужное поле формы - 400 +
    //Валютная пара отсутствует в базе данных - 404 +
    //Ошибка (например, база данных недоступна) - 500 +
    protected void handlePatch(HttpServletRequest request, HttpServletResponse response) {
        String rateCodes = Utils.extractCurrencyCodeFromURI(request);

        if (!rateCodes.matches(ENDPOINT_REGEX)) {
            ExceptionHandler.handleBadRequest(response,
                    "Invalid currency codes in URL. Currency code should consist of 3 letters like: USD, EUR, etc."); //400
            return;
        }

        if (!X_WWW_FORM_URLENCODED.equals(request.getContentType()) || request.getContentType() == null) {
            ExceptionHandler.handleUnsupportedMediaType(response,
                    "Invalid Content-Type. Expected application/x-www-form-urlencoded."); //415
            return;
        }

        String baseCode = rateCodes.substring(0, 3);
        String targetCode = rateCodes.substring(3);

        Optional<ExchangeRateDTO> optionalExchangeRateDTO = ExchangeRateMapper.mapPatchRequestToDto(request, baseCode, targetCode);

        if (optionalExchangeRateDTO.isEmpty()) {
            ExceptionHandler.handleBadRequest(response,
                    "Invalid request parameters. Request parameter (rate) should be sent"); //400

        } else {
            ExchangeRate updatedExchangeRate = exchangeService.changeExchangeRate(optionalExchangeRateDTO.get());
            createSuccessfulGetResponse(response, updatedExchangeRate);
        }
    }
}
