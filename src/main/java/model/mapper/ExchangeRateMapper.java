package model.mapper;

import jakarta.servlet.http.HttpServletRequest;
import model.DTO.ExchangeRateDTO;

import java.util.Map;
import java.util.Optional;

public class ExchangeRateMapper extends BaseMapper{

    public static Optional<ExchangeRateDTO> mapPatchRequestToDto(HttpServletRequest request, String baseCurrency, String targetCurrency) {

        Map<String, String> params = extractParametersFromRequestBody(request);
        String rate = params.get("rate");

        if (rate != null) {
            double rateValue = getDoubleValueFromParam(rate, 6);

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

        double rateValue = getDoubleValueFromParam(request.getParameter("rate"), 6);

        return Optional.of(new ExchangeRateDTO(
                request.getParameter("baseCurrencyCode"),
                request.getParameter("targetCurrencyCode"),
                rateValue
        ));
    }
}
