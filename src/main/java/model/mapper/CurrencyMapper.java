package model.mapper;

import jakarta.servlet.http.HttpServletRequest;
import model.Currency;
import model.DTO.NewCurrencyDTO;

import java.util.Optional;

public class CurrencyMapper extends BaseMapper {

    public static Optional<NewCurrencyDTO> mapRequestToDto(HttpServletRequest request) {
        if (request.getParameter("name") == null
                || request.getParameter("code") == null
                || request.getParameter("sign") == null) {
            return Optional.empty();
        }

        return Optional.of(new NewCurrencyDTO(
                request.getParameter("code"),
                request.getParameter("name"),
                request.getParameter("sign")));
    }

    public static Currency mapDtoToObject(NewCurrencyDTO newCurrencyDTO) {
        return new Currency(
                newCurrencyDTO.getCode(),
                newCurrencyDTO.getName(),
                newCurrencyDTO.getSign()
        );
    }
}
