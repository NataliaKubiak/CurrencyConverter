package model.mapper;

import jakarta.servlet.http.HttpServletRequest;
import model.DTO.ExchangeRatePatchDTO;
import utils.Utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;

public class ExchangeRatePatchMapper {

    public static Optional<ExchangeRatePatchDTO> mapRequestToDto(HttpServletRequest request, String baseCurrency, String targetCurrency) {

        Map<String, String> params = Utils.extractParametersFromRequestBody(request);
        String rate = params.get("rate");

        if(rate != null) {
            try {
                // эти махинации c BigDecimal, чтобы не выводился scientific number
                // и количество знаков после запятой было как в ТЗ
                BigDecimal bigDecimalRate = new BigDecimal(Double.parseDouble(rate));

                if (bigDecimalRate.scale() > 6) {
                    bigDecimalRate = bigDecimalRate.setScale(6, RoundingMode.HALF_UP);
                }

                double rateValue = bigDecimalRate.doubleValue();

                return Optional.of(new ExchangeRatePatchDTO(baseCurrency, targetCurrency, rateValue));
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        }

        return Optional.empty();
    }
}
