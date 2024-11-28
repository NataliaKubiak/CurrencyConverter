package servlet.exchange;

import exceptions.BusinessLogicException;
import exceptions.ExceptionHandler;
import exceptions.NoDataFoundException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.DTO.MoneyExchangeDTO;
import model.MoneyExchange;
import model.mapper.MoneyExchangeMapper;
import org.springframework.dao.DataAccessException;

import java.util.Optional;

@WebServlet("/exchangeRate/exchange")
public class MoneyExchangeServlet extends BaseExchangeRateServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String from = request.getParameter("from");
        String to = request.getParameter("to");
        String amount = request.getParameter("amount");

        try {
            Optional<MoneyExchangeDTO> optionalMoneyExchangeDTO = MoneyExchangeMapper.mapRequestToDto(request);

            if (optionalMoneyExchangeDTO.isEmpty()) {
                ExceptionHandler.handleBadRequest(response,
                        "Invalid request parameters"); //400
            } else {
                try {
                    MoneyExchange moneyExchange = exchangeService.exchangeMoney(optionalMoneyExchangeDTO.get());
                    createSuccessfulGetResponse(response, moneyExchange);

                } catch (BusinessLogicException ex) {
                    ExceptionHandler.handleBadRequest(response,
                            "Invalid URL parameters."); //400

                } catch (NoDataFoundException ex) {
                    ExceptionHandler.handleNotFoundException(response,
                            "Unsuccessful exchange operation. No Exchange Rate for this pair of currencies were found or No currencies found with Codes: "
                            + from + ", " + to + "."); //404

                } catch (DataAccessException ex) {
                    ExceptionHandler.handleDataAccessException(response); //500

                } catch (Exception ex) {
                    ExceptionHandler.handleUnexpectedException(response); //500
                }
            }
        } catch (IllegalArgumentException ex) {
            ExceptionHandler.handleBadRequest(response,
                    "Invalid parameter value. Parameter 'amount' must be greater than zero"); //400
        }
    }
}
