package paw.project.calendarapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import paw.project.calendarapp.interceptor.LocaleInterceptor;

@Configuration
public class MessagesConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(new LocaleInterceptor());
    }

}
