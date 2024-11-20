package servlet.currency;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Currency;
import utils.Utils;

public class GetCurrencyServlet extends BaseCurrencyServlet {

    private static final String ENDPOINT_REGEX = "[A-Z]{3}";

    //Успех - 200 +
    //Код валюты отсутствует в адресе - 400
    //Валюта не найдена - 404
    //Ошибка (например, база данных недоступна) - 500
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String currencyCode = Utils.extractCurrencyCodeFromURI(request);

        //TODO check this Error with your new Error Handling
        if (!currencyCode.matches(ENDPOINT_REGEX)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); //400

            setErrorMessage(response, "Invalid currency code in URI. Currency code should consist of 3 letters like: USD, EUR, etc.");
            return;
        }

        Currency currency = currencyService.getCurrencyByCode(currencyCode);

        createSuccessfulGetResponse(response, currency);
    }
}
