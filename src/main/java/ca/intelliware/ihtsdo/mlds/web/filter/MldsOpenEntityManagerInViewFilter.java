package ca.intelliware.ihtsdo.mlds.web.filter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;

public class MldsOpenEntityManagerInViewFilter extends OpenEntityManagerInViewFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path != null && path.contains("/download");
    }
}
