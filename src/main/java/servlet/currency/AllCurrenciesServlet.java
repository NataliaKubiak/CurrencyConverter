package servlet.currency;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Currency;
import model.DTO.CurrencyAdditionDTO;
import model.mapper.CurrencyAdditionMapper;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Optional;

public class AllCurrenciesServlet extends BaseCurrencyServlet {

    //Успех - 200 +
    //Ошибка (например, база данных недоступна) - 500
    //503 ????
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            List<Currency> allCurrenciesList = currencyService.getAll();

            createSuccessfulGetResponse(response, allCurrenciesList);

            //TODO check this Error with your new Error Handling
        } catch (DataAccessException ex) {
            ex.printStackTrace();

            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE); //503
            setErrorMessage(response, "Database is unavailable. Please try again later.");
        }
    }

    //Успех - 201 +
    //Отсутствует нужное поле формы - 400
    //Валюта с таким кодом уже существует - 409
    //Ошибка (например, база данных недоступна) - 500
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {

        //TODO check this Error with your new Error Handling
        if (!X_WWW_FORM_URLENCODED.equals(request.getContentType()) || request.getContentType() == null) {
            response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE); //415
            setErrorMessage(response, "Invalid Content-Type. Expected application/x-www-form-urlencoded.");
            return;
        }

        Optional<CurrencyAdditionDTO> optionalCurrencyAdditionDTO = CurrencyAdditionMapper.mapRequestToDto(request);

        //TODO check this Error with your new Error Handling
        if (optionalCurrencyAdditionDTO.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); //400
            setErrorMessage(response, "Invalid request parameters. One or many parameters are empty.");//Отсутствует нужное поле формы

        } else {
            Currency currency = currencyService.addCurrency(optionalCurrencyAdditionDTO.get());

            createSuccessfulPostResponse(response, currency);
        }
    }
}
