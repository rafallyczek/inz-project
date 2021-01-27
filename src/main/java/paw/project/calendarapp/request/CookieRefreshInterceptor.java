package paw.project.calendarapp.request;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieRefreshInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Cookie[] cookies = request.getCookies();

        if(cookies!=null){
            for(Cookie cookie : cookies){
                if(cookie.getName().contains("THEME")){
                    //259200s = 3 dni (60*60*24*3)
                    cookie.setMaxAge(259200);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }
        }

        return super.preHandle(request, response, handler);
    }

}
