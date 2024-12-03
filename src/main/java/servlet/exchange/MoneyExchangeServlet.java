package servlet.exchange;

import exceptions.ExceptionHandler;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.DTO.MoneyExchangeDTO;
import model.MoneyExchange;
import model.mapper.MoneyExchangeMapper;

import java.util.Optional;

@WebServlet("/exchangeRate/exchange")
public class MoneyExchangeServlet extends BaseExchangeRateServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

        Optional<MoneyExchangeDTO> optionalMoneyExchangeDTO = MoneyExchangeMapper.mapRequestToDto(request);

        if (optionalMoneyExchangeDTO.isEmpty()) {
            ExceptionHandler.handleBadRequest(response,
                    "Invalid request parameters"); //400

        } else {
            MoneyExchange moneyExchange = exchangeService.exchangeMoney(optionalMoneyExchangeDTO.get());
            createSuccessfulGetResponse(response, moneyExchange);
        }
    }
}
