package paw.project.calendarapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import paw.project.calendarapp.request.LocaleInterceptor;

@Configuration
public class MessagesConfig implements WebMvcConfigurer {

    @Bean
    LocaleInterceptor localeInterceptor(){
        return new LocaleInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(localeInterceptor());
    }

}
