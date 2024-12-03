package servlet.exchange;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.DTO.ExchangeRateDTO;
import model.ExchangeRate;
import utils.ParamUtils;
import validator.Validator;

import java.util.List;

@WebServlet("/exchangeRates")
public class AllExchangeRatesServlet extends BaseExchangeRateServlet {

    //Успех - 200 +
    //Ошибка (например, база данных недоступна) - 500 +
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

        List<ExchangeRate> allRates = exchangeService.getAll();
        createSuccessfulGetResponse(response, allRates); //200
    }

    //Успех - 201 +
    //Отсутствует нужное поле формы - 400 +
    //Валютная пара с таким кодом уже существует - 409 +
    //Одна (или обе) валюта из валютной пары не существует в БД - 404
    //Ошибка (например, база данных недоступна) - 500 +
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        Validator.validateContentType(request.getContentType());

        String baseCodeValue = request.getParameter("baseCurrencyCode");
        String targetCodeValue = request.getParameter("targetCurrencyCode");
        String rateValue = request.getParameter("rate");

        Validator.validateStringParameterPresent("baseCurrencyCode", baseCodeValue);
        Validator.validateStringParameterPresent("targetCurrencyCode", targetCodeValue);
        Validator.validateStringParameterPresent("rate", rateValue);

        double rate = ParamUtils.convertParamToDouble(rateValue);
        Validator.validatePositive("Rate", rate);

        ExchangeRateDTO exchangeRateDTO = new ExchangeRateDTO(baseCodeValue, targetCodeValue, rate);

        ExchangeRate exchangeRate = exchangeService.addExchangeRate(exchangeRateDTO);
        createSuccessfulPostResponse(response, exchangeRate); //201
    }
}
