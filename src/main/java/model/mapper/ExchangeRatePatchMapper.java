package model.mapper;

import jakarta.servlet.http.HttpServletRequest;
import model.DTO.ExchangeRatePatchDTO;
import utils.Utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;

public class ExchangeRatePatchMapper {

    public static Optional<ExchangeRatePatchDTO> mapRequestToPatchDto(HttpServletRequest request, String baseCurrency, String targetCurrency) {

        Map<String, String> params = Utils.extractParametersFromRequestBody(request);
        String rate = params.get("rate");

        if (rate != null) {
            double rateValue = getRateValue(rate);

            return Optional.of(new ExchangeRatePatchDTO(baseCurrency, targetCurrency, rateValue));
        }

        return Optional.empty();
    }

    public static Optional<ExchangeRatePatchDTO> mapRequestToPostDto(HttpServletRequest request) {

        if (request.getParameter("baseCurrencyCode") == null
                || request.getParameter("targetCurrencyCode") == null
                || request.getParameter("rate") == null) {
            return Optional.empty();
        }

        double rateValue = getRateValue(request.getParameter("rate"));

        return Optional.of(new ExchangeRatePatchDTO(
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
            ex.printStackTrace();
        }
        return rateValue;
    }
}
