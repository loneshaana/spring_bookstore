import domain.User;
import domain.security.Role;
import domain.security.UserRoles;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import services.UserService;
import util.SecurityUtility;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
@ComponentScan(basePackages = {"controllers","services","repositories","config","util"})
@EntityScan(basePackages = {"domain"})
@EnableJpaRepositories(basePackages = {"repositories"})

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
