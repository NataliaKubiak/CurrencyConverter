package servlet.currency;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Currency;
import model.DTO.NewCurrencyDTO;
import validator.Validator;

import java.util.List;

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

        Validator.validateContentType(request.getContentType());

        String nameValue = request.getParameter("name");
        String codeValue = request.getParameter("code");
        String signValue = request.getParameter("sign");

        Validator.validateStringParameterPresent("name", nameValue);
        Validator.validateStringParameterPresent("code", codeValue);
        Validator.validateStringParameterPresent("sign", signValue);

        NewCurrencyDTO newCurrencyDTO = new NewCurrencyDTO(nameValue, codeValue, signValue);

            Currency currency = currencyService.addCurrency(newCurrencyDTO);
            createSuccessfulPostResponse(response, currency); //201
    }
}

