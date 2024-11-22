package model.mapper;

import jakarta.servlet.http.HttpServletRequest;
import model.Currency;
import model.DTO.CurrencyAdditionDTO;

import java.util.Optional;

public class CurrencyAdditionMapper extends BaseMapper {

    public static Optional<CurrencyAdditionDTO> mapRequestToDto(HttpServletRequest request) {
        if (request.getParameter("name") == null
                || request.getParameter("code") == null
                || request.getParameter("sign") == null) {
            return Optional.empty();
        }

        return Optional.of(new CurrencyAdditionDTO(
                request.getParameter("code"),
                request.getParameter("name"),
                request.getParameter("sign")));
    }

    public static Currency mapDtoToObject(CurrencyAdditionDTO currencyAdditionDTO) {
        return new Currency(
                currencyAdditionDTO.getCode(),
                currencyAdditionDTO.getName(),
                currencyAdditionDTO.getSign()
        );
    }
}
