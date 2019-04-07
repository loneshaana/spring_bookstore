package services;

import domain.User;
import domain.security.PasswordResetToken;
import domain.security.UserRoles;

import java.util.Set;

public interface UserService {
    PasswordResetToken getPasswordResetToken(final String token);
    void createPasswordResetTokenForUser(final User user,final String token);

    User findByUsername(String username);

    User findByEmail(String email);

    User createUser(User user, Set<UserRoles> userRoles) throws Exception;
}
