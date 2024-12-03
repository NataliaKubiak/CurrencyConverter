package model.mapper;

import model.Currency;
import model.DTO.NewCurrencyDTO;

public class CurrencyMapper extends BaseMapper {

    public static Currency mapCurrencyDtoToCurrency(NewCurrencyDTO newCurrencyDTO) {
        return new Currency(
                newCurrencyDTO.getCode(),
                newCurrencyDTO.getName(),
                newCurrencyDTO.getSign()
        );
    }
}
