package testStuff.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

public class LibraryServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter pw = resp.getWriter();

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/java_ee_db",
                    "nataliakubiak", "");

            //с помощью этого объекта мы можем создавать запросы к указанной БД
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT title FROM books");

            while (rs.next()) {
                pw.println((rs.getString("title")));
            }
            statement.close();

        } catch (SQLException e) { //все ошибки в JDBC выдают такой ексепшн
            throw new RuntimeException(e);
        }
    }
}
