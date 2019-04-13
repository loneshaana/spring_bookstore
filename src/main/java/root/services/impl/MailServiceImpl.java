package root.services.impl;

import root.domain.User;
import root.domain.security.Role;
import root.domain.security.UserRoles;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import root.services.MailService;
import root.services.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class MailServiceImpl implements MailService {
    private final UserService userService;
    private Environment environment;

    private JavaMailSender mailSender;

    public MailServiceImpl(UserService userService,JavaMailSender mailSender,Environment environment) {
        this.userService = userService;
        this.mailSender = mailSender;
        this.environment = environment;
    }

    private void constructForgotPasswordEmail(String contextPath, Locale locale,String token,User user,String pass){
        String url = contextPath + "/newUser?token="+token;
        String message = "\nPlease click on this link to change your password,your new password is  \n"+pass;
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(user.getEmail());
        simpleMailMessage.setSubject("Forgot Password");
        simpleMailMessage.setText(url+message);
        simpleMailMessage.setFrom(environment.getProperty("support.email"));
        this.send(simpleMailMessage);
    }

    private void constructNewUserMailConfirmation(String contextPath, Locale locale,String token,User user,String pass){
        String url = contextPath + "/newUser?token="+token;
        String message = "\nPlease click on this link to verify your email and edit your personal information,your password is \n"+pass;
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(user.getEmail());
        simpleMailMessage.setSubject("BookStore New User");
        simpleMailMessage.setText(url+message);
        simpleMailMessage.setFrom(environment.getProperty("support.email"));
        this.send(simpleMailMessage);
    }

    @Override
    public void sendForgotPasswordMail(HttpServletRequest request, User user, String password) {
        String token = UUID.randomUUID().toString();
        String appUrl = "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
        userService.createPasswordResetTokenForUser(user,token);
        this.constructForgotPasswordEmail(appUrl,request.getLocale(),token,user,password);
    }

    @Override
    public void send(SimpleMailMessage mail) {
        mailSender.send(mail);
    }

    @Override
    public void sendNewUserTokenMailConfirmation(HttpServletRequest request, User user, String roleName, String password) throws Exception {
        Role role = new Role();
        role.setFirstName(roleName);
        Set<UserRoles> userRoles = new HashSet<>();
        userRoles.add(new UserRoles(user,role));

        userService.createUser(user,userRoles);
        String token = UUID.randomUUID().toString(); // create the random Token

        userService.createPasswordResetTokenForUser(user,token);
        String appUrl = "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
        this.constructNewUserMailConfirmation(appUrl,request.getLocale(),token,user,password);

    }
}
