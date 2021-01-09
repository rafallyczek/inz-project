package paw.project.calendarapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import paw.project.calendarapp.service.RequestService;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ThemeController {

    private RequestService requestService;

    @Autowired
    public ThemeController(RequestService requestService){
        this.requestService = requestService;
    }

    @PostMapping("/theme")
    public String theme(HttpServletRequest request) {
        return "redirect:"+requestService.determineRequestAddress(request);
    }

}
