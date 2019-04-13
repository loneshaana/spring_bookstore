package root.services;

import root.domain.User;
import root.domain.security.PasswordResetToken;
import root.domain.security.UserRoles;

import java.util.Set;

public interface UserService {
    PasswordResetToken getPasswordResetToken(final String token);
    void createPasswordResetTokenForUser(final User user,final String token);

    User findByUsername(String username);

    User findByEmail(String email);

    User createUser(User user, Set<UserRoles> userRoles) throws Exception;

    Set<User> findAll();

    User save(User user);
}
