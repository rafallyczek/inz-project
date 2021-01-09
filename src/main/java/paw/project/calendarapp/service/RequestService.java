package paw.project.calendarapp.service;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class RequestService {

    //Zwróć adres, z którego przyszło żądanie
    public String determineRequestAddress(HttpServletRequest request){
        int index = request.getHeader("Referer").indexOf("/",8);
        return request.getHeader("Referer").substring(index);
    }

}
