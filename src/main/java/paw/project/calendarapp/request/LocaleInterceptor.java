package paw.project.calendarapp.request;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import paw.project.calendarapp.model.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Map;

public class LocaleInterceptor extends HandlerInterceptorAdapter {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        Locale locale = request.getLocale();
        if (modelAndView != null) {
            Map<String,Object> modelMap = modelAndView.getModel();
            Calendar calendar = (Calendar) modelMap.get("calendar");
            if(calendar!=null){
                calendar.setLocale(locale);
                modelAndView.addObject("calendar",calendar);
            }
        }

        super.postHandle(request, response, handler, modelAndView);
    }
}
