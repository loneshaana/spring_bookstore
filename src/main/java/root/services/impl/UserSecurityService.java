package root.services.impl;

import root.domain.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import root.repositories.UserRepository;

@Service
public class UserSecurityService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserSecurityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Other User Detail Service");
       User user =  userRepository.findByUsername(username);
       if(user == null){
           throw new UsernameNotFoundException("User Not Found");
       }
       return user;
    }
}
