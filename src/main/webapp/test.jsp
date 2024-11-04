<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Test JSP Page</title>
</head>
<body>
    <h1>Welcome to the Test JSP Page!</h1>
    <p>This is a simple JSP page to test Tomcat setup.</p>

    <%
        // JSP scriptlet: это блок Java-кода, который выполняется на сервере
        String message = "Hello, JSP!";
        out.println("<p>Message from JSP scriptlet: " + message + "</p>");
    %>

    <p>Current date and time: <%= new java.util.Date() %></p>
</body>
</html>