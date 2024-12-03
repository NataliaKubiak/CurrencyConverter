package validator;

import exceptions.InvalidContentTypeException;

public class InputValidator {

    private static final String X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";

    public static void validateContentType(String contentType) {

        if (!X_WWW_FORM_URLENCODED.equals(contentType) || contentType == null) {
            throw new InvalidContentTypeException("Invalid Content-Type. Expected application/x-www-form-urlencoded.");
        }
    }
}
