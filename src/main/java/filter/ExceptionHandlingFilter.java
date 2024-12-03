package filter;

import exceptions.*;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.dao.DataAccessException;

@WebFilter("/*")
public class ExceptionHandlingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        try {
            filterChain.doFilter(servletRequest, servletResponse);

        } catch (InvalidContentTypeException ex) {
            ExceptionHandler.handleUnsupportedMediaType((HttpServletResponse) servletResponse,
                    ex.getMessage()); //415

        } catch (BusinessLogicException | IllegalArgumentException ex) {
            ExceptionHandler.handleBadRequest((HttpServletResponse) servletResponse,
                    ex.getMessage()); //400

        } catch (NoDataFoundException ex) {
            ExceptionHandler.handleNotFoundException((HttpServletResponse) servletResponse,
                    ex.getMessage()); //404

        } catch (DuplicateDataException ex) {
            ExceptionHandler.handleDuplicateDataException((HttpServletResponse) servletResponse,
                    ex.getMessage()); //409

        } catch (DataAccessException ex) {
            ExceptionHandler.handleDataAccessException((HttpServletResponse) servletResponse); //500

        } catch (Exception ex) {
            ExceptionHandler.handleUnexpectedException((HttpServletResponse) servletResponse); //500
        }
    }
}
