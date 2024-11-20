package model.mapper;

import exceptions.BusinessLogicException;
import jakarta.servlet.http.HttpServletRequest;
import model.DTO.ExchangeRateDTO;
import utils.Utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;

public class ExchangeRateMapper {

    public static Optional<ExchangeRateDTO> mapPatchRequestToDto(HttpServletRequest request, String baseCurrency, String targetCurrency) {

        Map<String, String> params = Utils.extractParametersFromRequestBody(request);
        String rate = params.get("rate");

        if (rate != null) {
            double rateValue = getRateValue(rate);

            return Optional.of(new ExchangeRateDTO(baseCurrency, targetCurrency, rateValue));
        }

        return Optional.empty();
    }

    public static Optional<ExchangeRateDTO> mapPostRequestToDto(HttpServletRequest request) {

        if (request.getParameter("baseCurrencyCode") == null
                || request.getParameter("targetCurrencyCode") == null
                || request.getParameter("rate") == null) {
            return Optional.empty();
        }

        double rateValue = getRateValue(request.getParameter("rate"));

        return Optional.of(new ExchangeRateDTO(
                request.getParameter("baseCurrencyCode"),
                request.getParameter("targetCurrencyCode"),
                rateValue
        ));
    }

    private static double getRateValue(String rate) {
        double rateValue = 0;

        try {
            // эти махинации c BigDecimal, чтобы не выводился scientific number
            // и количество знаков после запятой было как в ТЗ
            BigDecimal bigDecimalRate = new BigDecimal(Double.parseDouble(rate));

            if (bigDecimalRate.scale() > 6) {
                bigDecimalRate = bigDecimalRate.setScale(6, RoundingMode.HALF_UP);
            }

            rateValue = bigDecimalRate.doubleValue();
        } catch (NumberFormatException ex) {
            throw new BusinessLogicException("Invalid rate data type");

        }
        return rateValue;
    }
}
