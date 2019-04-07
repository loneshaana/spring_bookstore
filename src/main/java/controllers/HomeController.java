package controllers;

import domain.User;
import domain.security.PasswordResetToken;
import domain.security.Role;
import domain.security.UserRoles;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import services.UserService;
import services.impl.UserSecurityService;
import util.MailConstructor;
import util.SecurityUtility;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping(name = "/")
public class HomeController {
    private final UserService userService;
    private final UserSecurityService userSecurityService;
    private final JavaMailSender mailSender;
    private MailConstructor mailConstructor;

    public HomeController(UserService userService, UserSecurityService userSecurityService, JavaMailSender mailSender, MailConstructor mailConstructor) {
        this.userService = userService;
        this.userSecurityService = userSecurityService;
        this.mailSender = mailSender;
        this.mailConstructor = mailConstructor;
    }

    @RequestMapping("login")
    public String login(){
        return "Login Page";
    }

    @PostMapping("newUser")
    public String newUserPost(HttpServletRequest request, @RequestParam("username") String username, @RequestParam("userEmail") String userEmail) throws Exception{
        System.out.println("Adding a new user");
        User userWithUsername = userService.findByUsername(username);
        User userWithEmail = userService.findByEmail(userEmail);

        if(userWithUsername != null){
            return  "username already exists!";
        }

        if(userWithEmail != null){
            return  "email already exists!";
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(userEmail);

        String pass = SecurityUtility.randomPassword();
        String encryptPass = SecurityUtility.passwordEncoder().encode(pass);
        user.setPassword(encryptPass);

        Role role = new Role();
        role.setFirstName("ROLE_USER");
        Set<UserRoles> userRoles = new HashSet<>();
        userRoles.add(new UserRoles(user,role));

        User createdUser = userService.createUser(user,userRoles);

        String token = UUID.randomUUID().toString(); // create the random Token

        userService.createPasswordResetTokenForUser(user,token);// store the token to the respective user

        String appUrl = "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();

        System.out.println(appUrl);
         // now send the mail

        SimpleMailMessage email = mailConstructor.constructResetTokenEmail(appUrl,request.getLocale(),token,user,pass);

        mailSender.send(email);

        return "User Created";
    }

    @RequestMapping("newUser")
    public String newUser(Locale locale, @RequestParam("token") String token, Model model){
        PasswordResetToken passToken =  userService.getPasswordResetToken(token);
        if(passToken == null){
            System.out.println("Bad Request");
            model.addAttribute("message","Bad Request");
            return null;
        }
        User user = passToken.getUser();
        String username = user.getUsername();
        UserDetails userDetails =  userSecurityService.loadUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails,userDetails.getPassword(),userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return "User Verified";
    }
}
