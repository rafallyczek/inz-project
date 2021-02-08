package paw.project.calendarapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import paw.project.calendarapp.service.RequestService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class CookieController {

    private final RequestService requestService;

    @Autowired
    public CookieController(RequestService requestService){
        this.requestService = requestService;
    }

    //Zapisz Cookie cookieConsent
    @GetMapping("/setCookie")
    public String setCookie(HttpServletRequest request, HttpServletResponse response){
        Cookie cookie = new Cookie("cookieConsent","true");
        //2592000s = 30 dni (60*60*24*3)
        cookie.setMaxAge(60*60*24*30);
        cookie.setPath("/");
        response.addCookie(cookie);
        return "redirect:"+requestService.determineRequestAddress(request);
    }

}
