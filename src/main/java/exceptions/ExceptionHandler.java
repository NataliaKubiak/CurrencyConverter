package exceptions;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ExceptionHandler {

    protected static final String APPLICATION_JSON = "application/json";
    private static final String UTF_8 = "UTF-8";
    private static final String ERROR_JSON = "{\"message\": \"%s\"}";

    //Status: 500
    public static void handleDataAccessException(HttpServletResponse response) {
        setErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Database is unavailable. Please try again later.");
    }

    //Status: 500
    public static void handleUnexpectedException(HttpServletResponse response) {
        setErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Unexpected server error. Please contact support.");
    }

    //Status: 415
    public static void handleUnsupportedMediaType(HttpServletResponse response, String message) {
        setErrorResponse(response, HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
                message != null ? message : "Invalid Content-Type.");
    }

    //Status: 400
    public static void handleBadRequest(HttpServletResponse response, String message) {
        setErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                message != null ? message : "Invalid request parameters.");
    }

    //Status: 409
    public static void handleDuplicateDataException(HttpServletResponse response, String message) {
        setErrorResponse(response, HttpServletResponse.SC_CONFLICT, message);
    }

    //Status: 404
    public static void handleNotFoundException(HttpServletResponse response, String message) {
        setErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, message);
    }

    private static void setErrorResponse(HttpServletResponse response, int status, String errorMessage) {
        response.setStatus(status);
        response.setContentType(APPLICATION_JSON);
        response.setCharacterEncoding(UTF_8);

        try {
            response.getWriter().write(String.format(ERROR_JSON, errorMessage));
        } catch (IOException ex) {
            ex.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //500
        }
    }
}
