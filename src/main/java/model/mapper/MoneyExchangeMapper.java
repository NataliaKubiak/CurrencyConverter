package model.mapper;

import jakarta.servlet.http.HttpServletRequest;
import model.DTO.MoneyExchangeDTO;

import java.util.Optional;

public class MoneyExchangeMapper extends BaseMapper {

    public static Optional<MoneyExchangeDTO> mapRequestToDto(HttpServletRequest request) {
        if (request.getParameter("from") == null
                || request.getParameter("to") == null
                || request.getParameter("amount") == null) {
            return Optional.empty();
        }

        double amount = getDoubleValueFromParam(request.getParameter("amount"), null);

        return Optional.of(new MoneyExchangeDTO(
                request.getParameter("from"),
                request.getParameter("to"),
                amount
        ));
    }
}
