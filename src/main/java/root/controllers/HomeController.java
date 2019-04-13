package root.controllers;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import root.domain.User;
import org.springframework.web.bind.annotation.*;
import root.domain.security.PasswordResetToken;
import root.services.MailService;
import root.services.UserService;
import root.services.impl.UserSecurityService;
import root.util.SecurityUtility;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Locale;
import java.util.Set;

@RestController
@RequestMapping(name = "/")
public class HomeController {
    private final UserService userService;
    private final UserSecurityService userSecurityService;
    private MailService mailService;


    public HomeController(UserService userService, UserSecurityService userSecurityService,
                          MailService mailService) {
        this.userService = userService;
        this.userSecurityService = userSecurityService;
        this.mailService = mailService;
    }

    private void loadUserToSecurityContext(User user){
        String username = user.getUsername();
        UserDetails userDetails = userSecurityService.loadUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails,userDetails.getPassword(),userDetails.getAuthorities());

        // load this to the Authentication Context
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @GetMapping("/")
    public String hello(Principal p){
        return "Hello "+p.getName();
    }

    @GetMapping("login")
    public String login(@RequestParam("email") String email,@RequestParam("password") String password){
        System.out.println("LOGIN WITH EMAIL AND PASSWORD");
        User user = userService.findByEmail(email);
        boolean encodedPassword = SecurityUtility.passwordEncoder().matches(password,user.getPassword());
        if(encodedPassword){
            this.loadUserToSecurityContext(user);
            return "Logged In Successfully";
        }
        return "Invalid Credentials";
    }

    @GetMapping("/users")
    public Set<User> getAllUsers(){
        return userService.findAll();
    }

    @GetMapping("/forgotPassword")
    public String forgotPassword(HttpServletRequest request,@RequestParam String email){
        User user = userService.findByEmail(email);
        if(user != null){
            // generate the new password
            String pass = SecurityUtility.randomPassword();
            String encodePass = SecurityUtility.passwordEncoder().encode(pass);

            // update his db
            user.setPassword(encodePass);
            userService.save(user);

            // then send him the token
            mailService.sendForgotPasswordMail(request,user,pass);
            return "Password sent to your mail";
        }
        return "user not found";
    }

    @PostMapping("newUser")
    public String newUserPost(HttpServletRequest request, @RequestParam("username") String username, @RequestParam("userEmail") String userEmail) throws Exception{
        User userWithUsername = userService.findByUsername(username);
        User userWithEmail = userService.findByEmail(userEmail);

        if(userWithUsername != null){
            return  "username already exists!";
        }

        if(userWithEmail != null){
            return  "email already exists!";
        }

        String pass = SecurityUtility.randomPassword();
        String passEncode = SecurityUtility.passwordEncoder().encode(pass);

        User user = new User();
        user.setUsername(username);
        user.setEmail(userEmail);
        user.setPassword(passEncode);
        mailService.sendNewUserTokenMailConfirmation(request,user,"USER_ROLE",pass);

        return "User Created";
    }

    @RequestMapping("newUser")
    public String newUser(Locale locale, @RequestParam("token") String token, Model model){
        PasswordResetToken passToken =  userService.getPasswordResetToken(token);

        if(passToken == null){
            model.addAttribute("message","Bad Request");
            return "Token Is Invalid";
        }

        User user = passToken.getUser();
        user.setVerified(true);
        userService.save(user);
        this.loadUserToSecurityContext(user);
        return "User Verified";
    }
}
