package servlet.exchange;

import exceptions.BusinessLogicException;
import exceptions.DuplicateDataException;
import exceptions.NoDataFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.DTO.ExchangeRateDTO;
import model.ExchangeRate;
import model.mapper.ExchangeRateMapper;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Optional;

public class AllExchangeRatesServlet extends BaseExchangeRateServlet {

    //Успех - 200 +
    //Ошибка (например, база данных недоступна) - 500 +
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            List<ExchangeRate> allRates = exchangeService.getAll();
            createSuccessfulGetResponse(response, allRates); //200

        } catch (DataAccessException ex) {
            handleDataAccessException(response); //500
        } catch (Exception ex) {
            handleUnexpectedException(response); //500
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
            handleUnsupportedMediaType(response); //415
            return;
        }

        Optional<ExchangeRateDTO> optionalExchangeRateDTO = ExchangeRateMapper.mapPostRequestToDto(request);

        if (optionalExchangeRateDTO.isEmpty()) {
            handleBadRequest(response); //400

        } else {
            try {
                ExchangeRate exchangeRate = exchangeService.addExchangeRate(optionalExchangeRateDTO.get());
                createSuccessfulPostResponse(response, exchangeRate); //201

            } catch (BusinessLogicException ex) {
                handleBadRequest(response); //400

            } catch (DuplicateDataException ex) {
                handleDuplicateDataException(response); //409

            } catch (NoDataFoundException ex) {
                handleNotFoundException(response,
                        optionalExchangeRateDTO.get().getBaseCurrencyCode(),
                        optionalExchangeRateDTO.get().getTargetCurrencyCode()); //404

            } catch (DataAccessException ex) {
                handleDataAccessException(response); //500

            } catch (Exception ex) {
                handleUnexpectedException(response); //500
            }
        }
    }

    private void handleDataAccessException (HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //500
        setErrorMessage(response, "Database is unavailable. Please try again later.");
    }

    private void handleUnexpectedException(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //500
        setErrorMessage(response, "Unexpected server error. Please contact support.");
    }

    private void handleUnsupportedMediaType(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE); //415
        setErrorMessage(response, "Invalid Content-Type. Expected application/x-www-form-urlencoded.");
    }

    private void handleBadRequest(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST); //400
        setErrorMessage(response, "Invalid request parameters."); //Отсутствует нужное поле формы или некорректные данные
    }

    private void handleDuplicateDataException(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_CONFLICT); // 409
        setErrorMessage(response, "Exchange Rate already exist.");
    }

    private void handleNotFoundException(HttpServletResponse response, String baseCode, String targetCode) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND); //404
        setErrorMessage(response, "No currencies found with Codes: " + baseCode + ", " + targetCode + ".");
    }
}
