package servlet.currency;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Currency;
import validator.InputValidator;

@WebServlet("/currency/*")
public class GetCurrencyServlet extends BaseCurrencyServlet {

    //Успех - 200 +
    //Код валюты отсутствует в адресе - 400 +
    //Валюта не найдена - 404 +
    //Ошибка (например, база данных недоступна) - 500 +
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String currencyCode = extractCurrencyCodeFromURI(request);

        InputValidator.validateCurrencyCodeMatchesPattern(currencyCode);

        Currency currency = currencyService.getCurrencyByCode(currencyCode);
        createSuccessfulGetResponse(response, currency); //200
    }
}
