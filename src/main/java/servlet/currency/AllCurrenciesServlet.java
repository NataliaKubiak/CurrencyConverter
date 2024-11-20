package servlet.currency;

import exceptions.BusinessLogicException;
import exceptions.DuplicateDataException;
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
    //Ошибка (например, база данных недоступна) - 500 +
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            List<Currency> allCurrenciesList = currencyService.getAll();
            createSuccessfulGetResponse(response, allCurrenciesList); //200

        } catch (DataAccessException ex) {
            handleDataAccessException(response); //500

        } catch (Exception ex) {
            handleUnexpectedException(response); //500
        }
    }

    //Успех - 201 +
    //Отсутствует нужное поле формы - 400 +
    //Валюта с таким кодом уже существует - 409 +
    //Ошибка (например, база данных недоступна) - 500 +
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {

        if (!X_WWW_FORM_URLENCODED.equals(request.getContentType()) || request.getContentType() == null) {
            handleUnsupportedMediaType(response); //415
            return;
        }

        Optional<CurrencyAdditionDTO> optionalCurrencyAdditionDTO = CurrencyAdditionMapper.mapRequestToDto(request);

        if (optionalCurrencyAdditionDTO.isEmpty()) {
            handleBadRequest(response); //400

        } else {
            try {
                Currency currency = currencyService.addCurrency(optionalCurrencyAdditionDTO.get());
                createSuccessfulPostResponse(response, currency); //201

            } catch (BusinessLogicException ex) {
                handleBadRequest(response); //400

            } catch (DuplicateDataException ex) {
                handleDuplicateDataException(response); //409

            } catch (DataAccessException ex) {
                handleDataAccessException(response); //500

            } catch (Exception ex) {
                handleUnexpectedException(response); //500
            }
        }
    }

    private void handleDataAccessException(HttpServletResponse response) {
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
        setErrorMessage(response, "Currency already exist.");
    }
}
