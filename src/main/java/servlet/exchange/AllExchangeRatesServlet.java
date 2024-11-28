package servlet.exchange;

import exceptions.BusinessLogicException;
import exceptions.DuplicateDataException;
import exceptions.ExceptionHandler;
import exceptions.NoDataFoundException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.DTO.ExchangeRateDTO;
import model.ExchangeRate;
import model.mapper.ExchangeRateMapper;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Optional;

@WebServlet("/exchangeRates")
public class AllExchangeRatesServlet extends BaseExchangeRateServlet {

    //Успех - 200 +
    //Ошибка (например, база данных недоступна) - 500 +
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            List<ExchangeRate> allRates = exchangeService.getAll();
            createSuccessfulGetResponse(response, allRates); //200

        } catch (DataAccessException ex) {
            ExceptionHandler.handleDataAccessException(response); //500
        } catch (Exception ex) {
            ExceptionHandler.handleUnexpectedException(response); //500
        }
    }

    //Успех - 201 +
    //Отсутствует нужное поле формы - 400 +
    //Валютная пара с таким кодом уже существует - 409 +
    //Одна (или обе) валюта из валютной пары не существует в БД - 404
    //Ошибка (например, база данных недоступна) - 500 +
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {

        if (!X_WWW_FORM_URLENCODED.equals(request.getContentType()) || request.getContentType() == null) {
            ExceptionHandler.handleUnsupportedMediaType(response,
                    "Invalid Content-Type. Expected application/x-www-form-urlencoded."); //415
            return;
        }

        try {
            Optional<ExchangeRateDTO> optionalExchangeRateDTO = ExchangeRateMapper.mapPostRequestToDto(request);

            if (optionalExchangeRateDTO.isEmpty()) {
                ExceptionHandler.handleBadRequest(response,
                        "Invalid request parameters. All request parameters (Base Currency Code, Target Currency Code, Rate) should be sent"); //400

            } else {
                try {
                    ExchangeRate exchangeRate = exchangeService.addExchangeRate(optionalExchangeRateDTO.get());
                    createSuccessfulPostResponse(response, exchangeRate); //201

                } catch (BusinessLogicException ex) {
                    ExceptionHandler.handleBadRequest(response,
                            "Invalid URL parameters. Currency code should consist of 3 letters like: USD, EUR, etc."); //400

                } catch (DuplicateDataException ex) {
                    ExceptionHandler.handleDuplicateDataException(response, "Exchange Rate already exist."); //409

                } catch (NoDataFoundException ex) {
                    ExceptionHandler.handleNotFoundException(response,
                            "Can't create Exchange Rate. No currencies found with Codes: "
                                    + optionalExchangeRateDTO.get().getBaseCurrencyCode() + ", "
                                    + optionalExchangeRateDTO.get().getTargetCurrencyCode()); //404

                } catch (DataAccessException ex) {
                    ExceptionHandler.handleDataAccessException(response); //500

                } catch (Exception ex) {
                    ExceptionHandler.handleUnexpectedException(response); //500
                }
            }
        } catch (IllegalArgumentException ex) {
            ExceptionHandler.handleBadRequest(response,
                    "Invalid parameter value or type. Parameter 'rate' must be a decimal number greater than zero"); //400
        }
    }
}
