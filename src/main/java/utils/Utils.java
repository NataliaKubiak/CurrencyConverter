package utils;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

public class Utils {

    public static String extractCurrencyCodeFromURI(HttpServletRequest request) {
        return request.getPathInfo().substring(1).toUpperCase(Locale.ROOT);
    }
}
