package fact.it.users;


import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(HttpMethod.POST,"/**").permitAll()
                .antMatchers(HttpMethod.PUT,"/**").permitAll()
                .antMatchers(HttpMethod.DELETE,"/**").permitAll()
                .antMatchers(HttpMethod.GET,"/**").permitAll()
                .antMatchers("/**").permitAll()
                .anyRequest().authenticated();
        http.csrf().disable();
    }
}