package servise;

import exceptions.InvalidCurrencyCodeException;
import utils.ConnectionUtil;

import java.util.Locale;

public class BaseService {

    protected ConnectionUtil connectionUtil = new ConnectionUtil();

    protected void validateCurrencyCodeMatchesPattern(String code) {
        String codeRegex = "[A-Z]{3}";

        if (!code.toUpperCase(Locale.ROOT).matches(codeRegex)) {
            throw new InvalidCurrencyCodeException("Invalid currency code: " + code);
        }
    }
}
