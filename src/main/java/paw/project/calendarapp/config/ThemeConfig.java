package paw.project.calendarapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.context.support.ResourceBundleThemeSource;
import org.springframework.web.servlet.ThemeResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.theme.CookieThemeResolver;
import org.springframework.web.servlet.theme.ThemeChangeInterceptor;

@Configuration
public class ThemeConfig implements WebMvcConfigurer {

    //Zarejestruj interceptor zmieniający aktualny motyw
    @Override
    public void addInterceptors(InterceptorRegistry registry){
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

    //Bean zwracający ThemeResolver ustawiający wybrany motyw na podstawie danych z cookies
    @Bean
    public ThemeResolver themeResolver(){
        CookieThemeResolver cookieThemeResolver = new CookieThemeResolver();
        cookieThemeResolver.setDefaultThemeName("light");
        //604800s = 1 tydzień (60*60*24*7)
        cookieThemeResolver.setCookieMaxAge(604800);
        return cookieThemeResolver;
    }

}
