package servlet.exchange;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.DTO.MoneyExchangeDTO;
import model.MoneyExchange;
import utils.ParamUtils;
import validator.Validator;

@WebServlet("/exchangeRate/exchange")
public class MoneyExchangeServlet extends BaseExchangeRateServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String baseCurrencyCode = request.getParameter("from");
        String targetCurrencyCode = request.getParameter("to");
        String amount = request.getParameter("amount");

        Validator.validateStringParameterPresent("from", baseCurrencyCode);
        Validator.validateStringParameterPresent("to", targetCurrencyCode);
        Validator.validateStringParameterPresent("amount", amount);

        double amountValue = ParamUtils.convertParamToDouble(amount);
        Validator.validatePositive("Amount", amountValue);

        MoneyExchangeDTO moneyExchangeDTO = new MoneyExchangeDTO(baseCurrencyCode, targetCurrencyCode, amountValue);

        MoneyExchange moneyExchange = exchangeService.exchangeMoney(moneyExchangeDTO);
        createSuccessfulGetResponse(response, moneyExchange);

    }
}
