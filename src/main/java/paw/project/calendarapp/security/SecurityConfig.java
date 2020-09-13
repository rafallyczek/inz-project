package paw.project.calendarapp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    //Wstrzykiwanie serwisu pobierającego dane użytkownika
    @Qualifier("userService")
    @Autowired
    private UserDetailsService userDetailsService;

    //Metoda zwracająca bean szyfrujący hasła
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    //Konfiguracja sposobu wyszukiwania użytkowników podczas uwierzytelniania
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(encoder());
    }

//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web.ignoring().antMatchers("/styles/themes/**");
//    }

    //Zabezpieczenie żądań
    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.authorizeRequests()
                .antMatchers("/calendar","/notes/list","/calendar/list")
                    .hasRole("USER")
                .antMatchers("/","/**").permitAll()
                .and()
                    .formLogin()
                        .loginPage("/login")
                        .defaultSuccessUrl("/calendar")
                        .failureUrl("/login?error=true")
                .and()
                    .logout()
                        .logoutSuccessUrl("/");
    }

}
