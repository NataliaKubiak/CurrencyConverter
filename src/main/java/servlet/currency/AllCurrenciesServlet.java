package servlet.currency;

import exceptions.ExceptionHandler;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Currency;
import model.DTO.NewCurrencyDTO;
import model.mapper.CurrencyMapper;

import java.util.List;
import java.util.Optional;

@WebServlet("/currencies")
public class AllCurrenciesServlet extends BaseCurrencyServlet {

    //Успех - 200 +
    //Ошибка (например, база данных недоступна) - 500 +
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

        List<Currency> allCurrenciesList = currencyService.getAll();
        createSuccessfulGetResponse(response, allCurrenciesList); //200
    }

    //Успех - 201 +
    //Отсутствует нужное поле формы - 400 +
    //Валюта с таким кодом уже существует - 409 +
    //Ошибка (например, база данных недоступна) - 500 +
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {

        if (!X_WWW_FORM_URLENCODED.equals(request.getContentType()) || request.getContentType() == null) {
            ExceptionHandler.handleUnsupportedMediaType(response,
                    "Invalid Content-Type. Expected application/x-www-form-urlencoded."); //415
            return;
        }

        Optional<NewCurrencyDTO> optionalNewCurrencyDTO = CurrencyMapper.mapRequestToDto(request);

        if (optionalNewCurrencyDTO.isEmpty()) {
            ExceptionHandler.handleBadRequest(response,
                    "Invalid request parameters. All request parameters (name, code, sign) should be sent"); //400

        } else {
            Currency currency = currencyService.addCurrency(optionalNewCurrencyDTO.get());
            createSuccessfulPostResponse(response, currency); //201
        }
    }
}

