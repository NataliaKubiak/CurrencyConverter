package servlet.exchange;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.DTO.ExchangeRateDTO;
import model.ExchangeRate;
import model.mapper.ExchangeRateMapper;
import utils.Utils;

import java.util.Optional;

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
    //Коды валют пары отсутствуют в адресе - 400
    //Обменный курс для пары не найден - 404
    //Ошибка (например, база данных недоступна) - 500
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String rateCodes = Utils.extractCurrencyCodeFromURI(request);

        //TODO check this Error with your new Error Handling
        if(!rateCodes.matches(ENDPOINT_REGEX)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); //400
            setErrorMessage(response, "Invalid currency code in URI. Currency code should consist of 6 letters like: USDEUR, EURRUB, etc.");
            return;
        }

        String baseCode = rateCodes.substring(0, 3);
        String targetCode = rateCodes.substring(3);

        ExchangeRate exchangeRate = exchangeService.getExchangeRate(baseCode, targetCode);

        createSuccessfulGetResponse(response, exchangeRate);
    }

    //PATCH
    //Успех - 200 +
    //Отсутствует нужное поле формы - 400
    //Валютная пара отсутствует в базе данных - 404
    //Ошибка (например, база данных недоступна) - 500
    protected void handlePatch(HttpServletRequest request, HttpServletResponse response) {
        String rateCodes = Utils.extractCurrencyCodeFromURI(request);

        //TODO check this Error with your new Error Handling
        if(!rateCodes.matches(ENDPOINT_REGEX)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); //400
            setErrorMessage(response, "Invalid currency code in URI. Currency code should consist of 6 letters like: USDEUR, EURRUB, etc.");
            return;
        }

        //TODO check this Error with your new Error Handling
        if (!X_WWW_FORM_URLENCODED.equals(request.getContentType()) || request.getContentType() == null) {
            response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE); //415
            setErrorMessage(response, "Invalid Content-Type. Expected application/x-www-form-urlencoded.");
            return;
        }

        String baseCode = rateCodes.substring(0, 3);
        String targetCode = rateCodes.substring(3);

        Optional<ExchangeRateDTO> optionalExchangeRateDTO = ExchangeRateMapper.mapPatchRequestToDto(request, baseCode, targetCode);

        //TODO check this Error with your new Error Handling
        if (optionalExchangeRateDTO.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); //400
            setErrorMessage(response, "Invalid request");

        } else {
            ExchangeRate updatedExchangeRate = exchangeService.patchExchangeRate(optionalExchangeRateDTO.get());

            createSuccessfulGetResponse(response, updatedExchangeRate);
        }
    }
}
