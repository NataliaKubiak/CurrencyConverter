package servlet.exchange;

import exceptions.BusinessLogicException;
import exceptions.NoDataFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.DTO.ExchangeRateDTO;
import model.ExchangeRate;
import model.mapper.ExchangeRateMapper;
import org.springframework.dao.DataAccessException;
import utils.Utils;

import java.util.Optional;

public class ExchangeRateServlet extends BaseExchangeRateServlet {

    private static final String ENDPOINT_REGEX = "[A-Z]{6}";

    private static final String HTTP_PATCH = "PATCH";
    private static final String HTTP_GET = "GET";

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) {
        if (HTTP_GET.equals(request.getMethod())) {
            doGet(request, response);

        } else if (HTTP_PATCH.equals(request.getMethod())) {
             handlePatch(request, response);
        }
    }

    //Успех - 200 +
    //Коды валют пары отсутствуют в адресе - 400 +
    //Обменный курс для пары не найден - 404 +
    //Ошибка (например, база данных недоступна) - 500 +
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String rateCodes = Utils.extractCurrencyCodeFromURI(request);

        if(!rateCodes.matches(ENDPOINT_REGEX)) {
            handleBadRequestException(response); //400
            return;
        }

        String baseCode = rateCodes.substring(0, 3);
        String targetCode = rateCodes.substring(3);

        try {
            ExchangeRate exchangeRate = exchangeService.getExchangeRate(baseCode, targetCode);
            createSuccessfulGetResponse(response, exchangeRate); //200

        } catch (NoDataFoundException ex) {
            handleNotFoundException(response, baseCode, targetCode); //404

        } catch (DataAccessException ex) {
            handleDataAccessException(response); //500

        } catch (Exception ex) {
            handleUnexpectedError(response); //500
        }
    }

    //PATCH
    //Успех - 200 +
    //Отсутствует нужное поле формы - 400 +
    //Валютная пара отсутствует в базе данных - 404 +
    //Ошибка (например, база данных недоступна) - 500 +
    protected void handlePatch(HttpServletRequest request, HttpServletResponse response) {
        String rateCodes = Utils.extractCurrencyCodeFromURI(request);

        if(!rateCodes.matches(ENDPOINT_REGEX)) {
            handleBadRequestException(response); //400
            return;
        }

        if (!X_WWW_FORM_URLENCODED.equals(request.getContentType()) || request.getContentType() == null) {
            handleUnsupportedMediaType(response); //415
            return;
        }

        String baseCode = rateCodes.substring(0, 3);
        String targetCode = rateCodes.substring(3);

        try {
            Optional<ExchangeRateDTO> optionalExchangeRateDTO = ExchangeRateMapper.mapPatchRequestToDto(request, baseCode, targetCode);

            if (optionalExchangeRateDTO.isEmpty()) {
                handleBadRequestException(response); //400

            } else {
                try {
                    ExchangeRate updatedExchangeRate = exchangeService.patchExchangeRate(optionalExchangeRateDTO.get());
                    createSuccessfulGetResponse(response, updatedExchangeRate);

                } catch (BusinessLogicException ex) {
                    handleBadRequestException(response); //400

                } catch (NoDataFoundException ex) {
                    handleNotFoundException(response, baseCode, targetCode); //404

                } catch (DataAccessException ex) {
                    handleDataAccessException(response); //500

                } catch (Exception ex) {
                    handleUnexpectedError(response); //500
                }
            }

        } catch (BusinessLogicException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); //400
            setErrorMessage(response, "Parameter 'rate' should be double");
        }
    }

    private void handleBadRequestException(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST); //400
        setErrorMessage(response, "Invalid request parameters.");
    }

    private void handleNotFoundException(HttpServletResponse response, String baseCode, String targetCode) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND); //404
        setErrorMessage(response, "No Exchange Rate for this pair of currencies were found or No currencies found with Codes: " + baseCode + ", " + targetCode + ".");
    }

    private void handleDataAccessException(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //500
        setErrorMessage(response, "Database is unavailable. Please try again later.");
    }

    private void handleUnexpectedError(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //500
        setErrorMessage(response, "Unexpected server error. Please contact support.");
    }

    private void handleUnsupportedMediaType(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE); //415
        setErrorMessage(response, "Invalid Content-Type. Expected application/x-www-form-urlencoded.");
    }
}
