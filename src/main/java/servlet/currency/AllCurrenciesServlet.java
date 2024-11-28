package servlet.currency;

import exceptions.BusinessLogicException;
import exceptions.DuplicateDataException;
import exceptions.ExceptionHandler;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Currency;
import model.DTO.CurrencyAdditionDTO;
import model.mapper.CurrencyAdditionMapper;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Optional;

@WebServlet("/currencies")
public class AllCurrenciesServlet extends BaseCurrencyServlet {

    //Успех - 200 +
    //Ошибка (например, база данных недоступна) - 500 +
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            List<Currency> allCurrenciesList = currencyService.getAll();
            createSuccessfulGetResponse(response, allCurrenciesList); //200

        } catch (DataAccessException ex) {
            ExceptionHandler.handleDataAccessException(response); //500

        } catch (Exception ex) {
            ExceptionHandler.handleUnexpectedException(response); //500
        }
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

        Optional<CurrencyAdditionDTO> optionalCurrencyAdditionDTO = CurrencyAdditionMapper.mapRequestToDto(request);

        if (optionalCurrencyAdditionDTO.isEmpty()) {
            ExceptionHandler.handleBadRequest(response,
                    "Invalid request parameters. All request parameters (name, code, sign) should be sent"); //400

        } else {
            try {
                Currency currency = currencyService.addCurrency(optionalCurrencyAdditionDTO.get());
                createSuccessfulPostResponse(response, currency); //201

            } catch (BusinessLogicException ex) {
                ExceptionHandler.handleBadRequest(response,
                        "Invalid URL parameters. Currency code should consist of 3 letters"); //400

            } catch (DuplicateDataException ex) {
                ExceptionHandler.handleDuplicateDataException(response, "Currency already exist."); //409

            } catch (DataAccessException ex) {
                ExceptionHandler.handleDataAccessException(response); //500

            } catch (Exception ex) {
                ExceptionHandler.handleUnexpectedException(response); //500
            }
        }
    }
}
