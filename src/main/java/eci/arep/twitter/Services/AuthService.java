package eci.arep.twitter.Services;

import eci.arep.twitter.Repository.UserRepository;
import eci.arep.twitter.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String username, String rawPassword){
        if(userRepository.existsByUsername(username)){
            throw new IllegalArgumentException("username already exists");
        }
        User u = User.builder()
                .username(username)
                .passwordHash(passwordEncoder.encode(rawPassword)) // 🔹 BCrypt hash
                .role("USER")
                .build();
        return userRepository.save(u);
    }

    public Optional<User> authenticate(String username, String rawPassword){
        return userRepository.findByUsername(username)
                .filter(u -> passwordEncoder.matches(rawPassword, u.getPasswordHash()));
    }
}
