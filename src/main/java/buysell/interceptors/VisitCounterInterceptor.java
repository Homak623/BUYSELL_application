package buysell.interceptors;

import buysell.services.VisitCounterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class VisitCounterInterceptor implements HandlerInterceptor {
    @Autowired
    private VisitCounterService visitCounterService;

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) {

        String url = request.getRequestURI();
        visitCounterService.incrementCount(url);
    }
}
