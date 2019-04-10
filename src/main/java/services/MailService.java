package services;

import domain.User;
import org.springframework.mail.SimpleMailMessage;

import javax.servlet.http.HttpServletRequest;

public interface MailService {

    void sendForgotPasswordMail(HttpServletRequest request,User user,String password);
    void send(SimpleMailMessage mail);

    void sendNewUserTokenMailConfirmation(HttpServletRequest request,User user,String roleName,String password) throws Exception;
}
