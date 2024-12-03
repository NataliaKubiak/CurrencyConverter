package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;

@WebFilter("/*")
public class ResponseFilter implements Filter {

    private static final String APPLICATION_JSON = "application/json";
    private static final String UTF_8 = "UTF-8";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        servletResponse.setContentType(APPLICATION_JSON);
        servletResponse.setCharacterEncoding(UTF_8);

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
