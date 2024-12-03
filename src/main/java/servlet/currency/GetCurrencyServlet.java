package servlet.currency;

import exceptions.ExceptionHandler;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Currency;
import utils.Utils;

@WebServlet("/currency/*")
public class GetCurrencyServlet extends BaseCurrencyServlet {

    private static final String ENDPOINT_REGEX = "[A-Z]{3}";

    //Успех - 200 +
    //Код валюты отсутствует в адресе - 400 +
    //Валюта не найдена - 404 +
    //Ошибка (например, база данных недоступна) - 500 +
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String currencyCode = Utils.extractCurrencyCodeFromURI(request);

        if (!currencyCode.matches(ENDPOINT_REGEX)) {
            ExceptionHandler.handleBadRequest(response,
                    "Invalid currency code in URL. Currency code should consist of 3 letters like: USD, EUR, etc."); //400
            return;
        }

        Currency currency = currencyService.getCurrencyByCode(currencyCode);
        createSuccessfulGetResponse(response, currency); //200
    }
}
