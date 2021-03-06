package root.services.impl;

import root.domain.User;
import root.domain.security.PasswordResetToken;
import root.domain.security.UserRoles;
import root.excetions.UserAlreadyExistsException;
import org.springframework.stereotype.Service;
import root.repositories.PasswordResetTokenRepository;
import root.repositories.RoleRepository;
import root.repositories.UserRepository;
import root.services.UserService;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    public UserServiceImpl(PasswordResetTokenRepository passwordResetTokenRepository,UserRepository userRepository,RoleRepository roleRepository) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public PasswordResetToken getPasswordResetToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        final PasswordResetToken myToken = new PasswordResetToken(token,user);
        passwordResetTokenRepository.save(myToken);
    }

    @Override
    public User findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    @Override
    public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    @Override
    public Set<User> findAll(){
        Set<User> users = new HashSet<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    @Override
    public User createUser(User user, Set<UserRoles> userRoles) throws Exception {
        User localUser = userRepository.findByUsername(user.getUsername());
        if(localUser != null){
            throw new UserAlreadyExistsException("User Already Exists");
        }else{
            for(UserRoles ur : userRoles){
                roleRepository.save(ur.getRole());
            }
            user.getUserRoles().addAll(userRoles);
            localUser = userRepository.save(user);
        }
        return localUser;
    }

    @Override
    public User save(User user){
        return userRepository.save(user);
    }
}
