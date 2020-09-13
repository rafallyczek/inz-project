package paw.project.calendarapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ThemeController {

    @PostMapping("/theme")
    public String home() {
        return "redirect:/";
    }

}
