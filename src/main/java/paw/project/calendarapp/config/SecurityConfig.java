package paw.project.calendarapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import paw.project.calendarapp.security.WebSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    //Wstrzykiwanie serwisu pobierającego dane użytkownika
    @Qualifier("userService")
    @Autowired
    private UserDetailsService userDetailsService;

    //Bean szyfrujący hasła
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    //Bean sprawdzający dostęp do kalendarza
    @Bean
    public WebSecurity webSecurity(){
        return new WebSecurity();
    }

    //Konfiguracja sposobu wyszukiwania użytkowników podczas uwierzytelniania
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(encoder());
    }

    //Zabezpieczenie żądań
    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.authorizeRequests()
                .antMatchers("/user/register","/user/add").permitAll()
                .antMatchers("/calendar/{id}/**").access("@webSecurity.checkCalendarId(authentication,#id)")
                .antMatchers("/calendar","/notes","/user","/calendar/**","/notes/**","/user/**","/messages","/messages/**")
                    .hasRole("USER")
                .antMatchers("/","/**").permitAll()
                .and()
                    .formLogin()
                        .loginPage("/login")
                        .defaultSuccessUrl("/")
                        .failureUrl("/login?error=true")
                .and()
                    .logout()
                        .logoutSuccessUrl("/")
                .and()
                    .exceptionHandling()
                        .accessDeniedPage("/");
    }

}
