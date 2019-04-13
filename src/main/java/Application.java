//import root.config.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import root.domain.User;
import root.domain.security.Role;
import root.domain.security.UserRoles;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import root.services.UserService;
import root.services.impl.UserSecurityService;
import root.util.SecurityUtility;

import java.util.*;

@ComponentScan(basePackages = {"root"})
@EntityScan(basePackages = {"root"})
@EnableJpaRepositories(basePackages = {"root"})
@SpringBootApplication
public class Application implements CommandLineRunner {

    public static void main(String[] args){
        SpringApplication.run(Application.class,args);
    }

    private UserService userService;

    public Application(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        User user = new User();
        user.setFirstName("Jhon");
        user.setLastName("Adam");
        user.setUsername("JA");
        user.setPassword(SecurityUtility.passwordEncoder().encode("p"));
        user.setEmail("anwarulhaq2017@outlook.com");
        Set<UserRoles> userRoles = new HashSet<>();
        Role role = new Role();
        role.setId(1L);
        role.setFirstName("USER_ROLE");
        userRoles.add(new UserRoles(user,role));
        try{
            userService.createUser(user,userRoles);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
 class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final Environment environment;

    private final UserSecurityService userSecurityService;

    @Autowired
    public SecurityConfig(Environment environment, UserSecurityService userSecurityService){
        this.environment = environment;
        this.userSecurityService = userSecurityService;
    }

    private BCryptPasswordEncoder passwordEncoder(){
        return  SecurityUtility.passwordEncoder();
    }

    private static final String[] PUBLIC_MATCHERS = {
            "/css/**",
            "/js/**",
            "/image/**",
            "/myAccount",
            "/newUser",
            "/login",
            "/",
            "/forgotPassword"
    };

    @Autowired
    public void configureGlobal1(AuthenticationManagerBuilder auth) throws Exception{
        auth.inMemoryAuthentication();
    }

    @Override
    public void configure(HttpSecurity http) throws Exception{
        http
                .authorizeRequests()
                .antMatchers(PUBLIC_MATCHERS)
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http
                .csrf()
                .disable()
                .cors()
                .disable()
                .formLogin()
                .failureUrl("/login?error")
                .defaultSuccessUrl("/")
                .loginPage("/login")
                .permitAll()
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/?logout")
                .deleteCookies("remember-me")
                .permitAll()
                .and()
                .rememberMe();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception{
        auth.userDetailsService(this.userSecurityService).passwordEncoder(passwordEncoder());
    }

}



