package validator;

import exceptions.InvalidContentTypeException;
import exceptions.InvalidInputParameterException;

public class Validator {

    private static final String X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    private static final String CODE_REGEX = "[A-Z]{3}";
    private static final String PAIR_REGEX = "[A-Z]{6}";

    public static void validateContentType(String contentType) {
        if (contentType == null || !contentType.contains(X_WWW_FORM_URLENCODED)) {
            throw new InvalidContentTypeException("Invalid Content-Type. Expected application/x-www-form-urlencoded.");
        }
    }

    public static void validateCurrencyCodeMatchesPattern(String currencyCode) {
        if (!currencyCode.matches(CODE_REGEX)) {
            throw new InvalidInputParameterException("Invalid currency code. The code must consist of 3 letters, but received: " + currencyCode);
        }
    }

    public static void validateCurrenciesPairMatchesPattern(String currencyCodes) {
        if (!currencyCodes.matches(PAIR_REGEX)) {
            throw new InvalidInputParameterException("Invalid currency codes. The code pair must consist of 6 letters, but received: " + currencyCodes);
        }
    }

    public static void validateStringParameterPresent(String paramName, String paramValue) {
        if (paramValue == null || paramValue.isEmpty()) {
            throw new InvalidInputParameterException("Required parameter '" + paramName + "' is missing in request.");
        }
    }

    public static void validatePositive(String name, double number) {
        if (number <= 0) {
            throw new InvalidInputParameterException(name + " must be greater than zero.");
        }
    }
}
