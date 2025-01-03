package servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Locale;

public class BaseServlet extends HttpServlet {

    protected void createSuccessfulGetResponse(HttpServletResponse response, Object objectForResponse) {
        String responseJson = objectToJson(objectForResponse);

        response.setStatus(HttpServletResponse.SC_OK); //200
        setResponseText(response, responseJson);
    }

    protected void createSuccessfulPostResponse(HttpServletResponse response, Object objectForResponse) {
        String responseJson = objectToJson(objectForResponse);

        response.setStatus(HttpServletResponse.SC_CREATED); //201
        setResponseText(response, responseJson);
    }

    protected String extractCurrencyCodeFromURI(HttpServletRequest request) {
        return request.getPathInfo().substring(1).toUpperCase(Locale.ROOT);
    }

    private String objectToJson(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        String json;

        try {
            json = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json;
    }

    private void setResponseText(HttpServletResponse response, String responseText) {
        try {
            response.getWriter().write(responseText);
        } catch (IOException ex) {
            ex.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //500
        }
    }
}
