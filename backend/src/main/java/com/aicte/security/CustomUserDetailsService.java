package com.aicte.security;

import com.aicte.entity.User;
import com.aicte.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(identifier)
                .or(() -> {
                    if (identifier == null) return java.util.Optional.empty();
                    String lower = identifier.trim().toLowerCase();
                    if ("admin".equals(lower)) {
                        return userRepository.findByEmail("admin@aicte.gov.in");
                    } else if ("officer".equals(lower) || "scrutiny_officer".equals(lower)) {
                        return userRepository.findByEmail("officer@aicte.gov.in");
                    } else if ("applicant".equals(lower)) {
                        return userRepository.findByEmail("applicant@demo.com");
                    }
                    return java.util.Optional.empty();
                })
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + identifier));
        return new CustomUserDetails(user);
    }
}
