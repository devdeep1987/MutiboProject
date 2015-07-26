package showcase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * Created by Devdeep on 2/23/2015.
 */
@EnableWebSecurity
@Configuration
@Order(1)
@EnableGlobalMethodSecurity(securedEnabled = true)
public class ApplicationSecurity extends WebSecurityConfigurerAdapter {
    @Autowired
    private CustomUserDetailsService users;
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/signup","/about").permitAll()
                .anyRequest().authenticated()
                .and()
                .httpBasic().and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
        
        // @formatter:on
    }

    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(users);
    }
}
