package validator;

import exceptions.InvalidContentTypeException;
import exceptions.InvalidCurrencyCodeException;

public class InputValidator {

    private static final String X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    private static final String CODE_REGEX = "[A-Z]{3}";
    private static final String PAIR_REGEX = "[A-Z]{6}";

    public static void validateContentType(String contentType) {
        if (!X_WWW_FORM_URLENCODED.equals(contentType) || contentType == null) {
            throw new InvalidContentTypeException("Invalid Content-Type. Expected application/x-www-form-urlencoded.");
        }
    }

    public static void validateCurrencyCode(String currencyCode) {
        if (!currencyCode.matches(CODE_REGEX)) {
            throw new InvalidCurrencyCodeException("Invalid currency code in URL. The code must consist of 3 letters, but received: " + currencyCode);
        }
    }

    public static void validateCurrenciesPair(String currencyCodes) {
        if (!currencyCodes.matches(PAIR_REGEX)) {
            throw new InvalidCurrencyCodeException("Invalid currency codes in URL. The code pair must consist of 6 letters, but received: " + currencyCodes);
        }
    }
}
