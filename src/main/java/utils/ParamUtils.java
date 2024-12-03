package utils;

import exceptions.InvalidInputParameterException;
import jakarta.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ParamUtils {

    public static double convertParamToDouble(String paramStringValue) {
        double paramValue;

        try {
            BigDecimal bigDecimalRate = new BigDecimal(Double.parseDouble(paramStringValue));

            if (bigDecimalRate.scale() > 6) {
                bigDecimalRate = bigDecimalRate.setScale(6, RoundingMode.HALF_UP);
            }

            paramValue = bigDecimalRate.doubleValue();
        } catch (NumberFormatException ex) {
            throw new InvalidInputParameterException("Invalid data type. Should be a number.");

        }
        return paramValue;
    }

    public static Map<String, String> extractParametersFromRequestBody(HttpServletRequest request) {
        Map<String, String> bodyParams = new HashMap<>();

        String[] pairs = getStrings(request);

        for (String pair : pairs) {
            String[] keyValue = pair.split("=");

            if (keyValue.length == 2) {
                bodyParams.put(keyValue[0].toLowerCase(Locale.ROOT), keyValue[1]);
            }
        }
        return bodyParams;
    }

    private static String[] getStrings(HttpServletRequest request) {
        StringBuilder requestBody = new StringBuilder();
        String line;
        BufferedReader reader;

        try {
            reader = request.getReader();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Тут читаем строки из тела запроса и складываем их в requestBody
        while (true) {
            try {
                if ((line = reader.readLine()) == null) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            requestBody.append(line);
        }

        // Разбор параметров из тела запроса (формат key1=value1&key2=value2)
        String[] pairs = requestBody.toString().split("&");
        return pairs;
    }
}
