package model.mapper;

import jakarta.servlet.http.HttpServletRequest;
import model.DTO.ExchangeRatePatchDTO;

import java.util.Optional;

public class ExchangeRatePatchMapper {

    public static Optional<ExchangeRatePatchDTO> mapRequestToDto(HttpServletRequest request, String baseCurrency, String targetCurrency) {
        if(request.getParameter("rate") == null) {
            return Optional.empty();
        }

        try {
            double rate = Double.parseDouble(request.getParameter("rate"));

            return Optional.of(new ExchangeRatePatchDTO(baseCurrency, targetCurrency, rate));
            //TODO вот тут понять как передать ошибку в сервлет и обработать её
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }
        return Optional.empty();
    }
}
