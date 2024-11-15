package testStuff.cookies;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class GetCookiesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Cookie[] cookies = req.getCookies();

        PrintWriter pw = resp.getWriter();
        pw.println("<html>");

        for (Cookie cookie : cookies) {
            pw.println("<h1>" + cookie.getName() + ": " + cookie.getValue() + "</h1>");
        }
        pw.println("</html>");
    }
}
