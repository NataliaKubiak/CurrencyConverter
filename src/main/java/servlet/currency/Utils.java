package servlet.currency;

import jakarta.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Utils {

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
        BufferedReader reader = null;
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
