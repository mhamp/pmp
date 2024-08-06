package com.xontext.pmp.service.auth;

import com.xontext.pmp.model.User;
import com.xontext.pmp.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        if (!user.isPresent()) {
            throw new UsernameNotFoundException("This user was not found with email: " + email);
        } else {
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.get().getEmail())
                    .password(user.get().getPassword())
                    .disabled(!user.get().isActive())
                    .build();
        }
    }


//    @Bean
//    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder){
//        Collection<UserDetails> users = new ArrayList<>();
//        UserDetails userDetails = User.builder()
//                .username("user")
//                .password(passwordEncoder.encode("pass"))
//                .roles("USER")
//                .build();
//        UserDetails userDetails2 = User.builder()
//                .username("admin")
//                .password(passwordEncoder.encode("pass"))
//                .roles("ADMIN")
//                .build();
//        users.add(userDetails);
//        users.add(userDetails2);
//        return new InMemoryUserDetailsManager(users);
//    }
}
