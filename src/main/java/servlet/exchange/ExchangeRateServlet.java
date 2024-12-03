package servlet.exchange;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.DTO.ExchangeRateDTO;
import model.ExchangeRate;
import utils.ParamUtils;
import validator.Validator;

import java.util.Map;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends BaseExchangeRateServlet {

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
        String rateCodes = extractCurrencyCodeFromURI(request);

        Validator.validateCurrenciesPairMatchesPattern(rateCodes);

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
        String rateCodes = extractCurrencyCodeFromURI(request);

        Validator.validateContentType(request.getContentType());
        Validator.validateCurrenciesPairMatchesPattern(rateCodes);

        String baseCode = rateCodes.substring(0, 3);
        String targetCode = rateCodes.substring(3);

        Map<String, String> params = ParamUtils.extractParametersFromRequestBody(request);
        String rateValue = params.get("rate");
        Validator.validateStringParameterPresent("rate", rateValue);

        double rate = ParamUtils.convertParamToDouble(rateValue);
        Validator.validatePositive("Rate", rate);

        ExchangeRateDTO exchangeRateDTO = new ExchangeRateDTO(baseCode, targetCode, rate);

        ExchangeRate updatedExchangeRate = exchangeService.changeExchangeRate(exchangeRateDTO);
        createSuccessfulGetResponse(response, updatedExchangeRate);
    }
}
