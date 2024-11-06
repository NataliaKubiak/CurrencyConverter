package cookies;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class SetCookiesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Cookie cookie1 = new Cookie("some_id", "123");
        Cookie cookie2 = new Cookie("some_name", "Natalie");
        cookie1.setMaxAge(24 * 60 * 60);
        cookie2.setMaxAge(24 * 60 * 60);

        resp.addCookie(cookie1);
        resp.addCookie(cookie2);
    }
}
