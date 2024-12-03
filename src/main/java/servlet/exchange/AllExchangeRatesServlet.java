package servlet.exchange;

import exceptions.ExceptionHandler;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.DTO.ExchangeRateDTO;
import model.ExchangeRate;
import model.mapper.ExchangeRateMapper;
import validator.Validator;

import java.util.List;
import java.util.Optional;

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

        Optional<ExchangeRateDTO> optionalExchangeRateDTO = ExchangeRateMapper.mapPostRequestToDto(request);

        if (optionalExchangeRateDTO.isEmpty()) {
            ExceptionHandler.handleBadRequest(response,
                    "Invalid request parameters. All request parameters (Base Currency Code, Target Currency Code, Rate) should be sent"); //400
        } else {
            ExchangeRate exchangeRate = exchangeService.addExchangeRate(optionalExchangeRateDTO.get());
            createSuccessfulPostResponse(response, exchangeRate); //201
        }
    }
}
