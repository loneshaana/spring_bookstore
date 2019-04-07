package util;

import domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MailConstructor {
    private Environment environment;

    public MailConstructor(Environment environment) {
        this.environment = environment;
    }

    public SimpleMailMessage constructResetTokenEmail(String contextPath, Locale locale, String token, User user, String password){
        String url = contextPath + "/newUser?token="+token;
        String message = "\nPlease click on this link to verify your email and edit your personal information,your password is \n"+password;
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(user.getEmail());
        simpleMailMessage.setSubject("BookStore New User");
        simpleMailMessage.setText(url+message);
        simpleMailMessage.setFrom(environment.getProperty("support.email"));
        return  simpleMailMessage;
    }
}
