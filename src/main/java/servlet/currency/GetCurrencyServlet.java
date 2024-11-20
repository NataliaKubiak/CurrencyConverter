package servlet.currency;

import exceptions.NoDataFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Currency;
import org.springframework.dao.DataAccessException;
import utils.Utils;

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
            handleBadRequestException(response); //400
            return;
        }

        try {
            Currency currency = currencyService.getCurrencyByCode(currencyCode);
            createSuccessfulGetResponse(response, currency); //200

        } catch (NoDataFoundException ex) {
            handleNotFoundException(response, currencyCode); //404

        } catch (DataAccessException ex) {
            handleDataAccessException(response); //500

        } catch (Exception ex) {
            handleUnexpectedError(response); //500
        }
    }

    private void handleBadRequestException(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST); //400
        setErrorMessage(response, "Invalid currency code in URI. Currency code should consist of 3 letters like: USD, EUR, etc.");
    }

    private void handleNotFoundException(HttpServletResponse response, String currencyCode) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND); //404
        setErrorMessage(response, "No currencies found with Code: " + currencyCode);
    }

    private void handleDataAccessException(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //500
        setErrorMessage(response, "Database is unavailable. Please try again later.");
    }

    private void handleUnexpectedError(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //500
        setErrorMessage(response, "Unexpected server error. Please contact support.");
    }
}
