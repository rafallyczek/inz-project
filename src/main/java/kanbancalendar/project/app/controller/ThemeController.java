package kanbancalendar.project.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import kanbancalendar.project.app.service.RequestService;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ThemeController {

    private final RequestService requestService;

    @Autowired
    public ThemeController(RequestService requestService){
        this.requestService = requestService;
    }

    @PostMapping("/theme")
    public String theme(HttpServletRequest request) {
        return "redirect:"+requestService.determineRequestAddress(request);
    }

}
