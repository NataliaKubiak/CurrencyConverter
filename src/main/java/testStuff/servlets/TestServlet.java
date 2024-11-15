package testStuff.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

public class TestServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Integer count = (Integer) session.getAttribute("count");

        if (count == null) {
            session.setAttribute("count", 1);
            count = 1;
        } else {
            session.setAttribute("count", count + 1);
        }

//        String name = req.getParameter("name");
//
//        resp.setContentType("text/html");

        PrintWriter pw = resp.getWriter();
        pw.println("<html>");
        pw.println("<h1> Hellooooo!!! You were here " + count + " times! </h1>");
        pw.println("</html>");
    }
}
