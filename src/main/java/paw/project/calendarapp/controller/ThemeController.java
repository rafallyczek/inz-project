package paw.project.calendarapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ThemeController {

    @PostMapping("/theme")
    public String theme(HttpServletRequest request) {
        int index = request.getHeader("Referer").indexOf("/",8);
        String redirectURL = request.getHeader("Referer").substring(index);
        return "redirect:"+redirectURL;
    }

}
