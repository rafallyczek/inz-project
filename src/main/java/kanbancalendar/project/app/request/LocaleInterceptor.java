package kanbancalendar.project.app.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import kanbancalendar.project.app.component.Calendar;
import kanbancalendar.project.app.component.LocaleHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public class LocaleInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private LocaleHolder localeHolder;
    @Autowired
    private Calendar calendar;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        Locale locale = request.getLocale();
        localeHolder.setLocale(locale);
        calendar.resetLocale();

        super.postHandle(request, response, handler, modelAndView);

    }

}
