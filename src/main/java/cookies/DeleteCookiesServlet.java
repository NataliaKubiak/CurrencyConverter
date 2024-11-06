package cookies;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class DeleteCookiesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Cookie cookie = new Cookie("some_id", "");
        cookie.setMaxAge(0); //удаление тут же
//        cookie.setMaxAge(-1); //удаление когда закрывается браузер

        resp.addCookie(cookie);
    }
}
