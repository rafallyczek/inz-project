package paw.project.calendarapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.context.support.ResourceBundleThemeSource;
import org.springframework.web.servlet.ThemeResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.theme.CookieThemeResolver;
import org.springframework.web.servlet.theme.ThemeChangeInterceptor;
import paw.project.calendarapp.cookies.CookieRefresh;

@Configuration
public class ThemeConfig implements WebMvcConfigurer {

    //Zarejestruj interceptor zmieniający aktualny motyw oraz interceptor odświeżający czas życia cookie
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(new CookieRefresh());
        registry.addInterceptor(themeChangeInterceptor());
    }

    //Bean zwracający Interceptor zmieniający aktualny motyw na podstawie parametrów żądań
    @Bean
    public ThemeChangeInterceptor themeChangeInterceptor(){
        ThemeChangeInterceptor themeChangeInterceptor = new ThemeChangeInterceptor();
        themeChangeInterceptor.setParamName("theme");
        return themeChangeInterceptor;
    }

    //Bean zwracający ThemeSource zarządzający plikami motywów (properties)
    @Bean
    public ResourceBundleThemeSource resourceBundleThemeSource(){
        return new ResourceBundleThemeSource();
    }

    //Bean zwracający ThemeResolver ustawiający wybrany motyw na podstawie danych z cookie
    @Bean
    public ThemeResolver themeResolver(){
        CookieThemeResolver cookieThemeResolver = new CookieThemeResolver();
        cookieThemeResolver.setDefaultThemeName("light");
        //259200s = 3 dni (60*60*24*3)
        cookieThemeResolver.setCookieMaxAge(259200);
        return cookieThemeResolver;
    }

}
