package com.aymanibrahim.hospital.security;

import com.aymanibrahim.hospital.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        com.aymanibrahim.hospital.entity.User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email: " + email));

        Collection<SimpleGrantedAuthority> authorities = loadAuthoritiesForUser(user);

        log.debug("Loaded user: {}, authorities: {}", email,
                authorities.stream().map(SimpleGrantedAuthority::getAuthority).collect(Collectors.toList()));

        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    @Transactional(readOnly = true)
    private Collection<SimpleGrantedAuthority> loadAuthoritiesForUser(
            com.aymanibrahim.hospital.entity.User user) {

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            log.warn("User {} has no roles assigned", user.getEmail());
            return java.util.Collections.emptyList();
        }

        return user.getRoles()
                .stream()
                .map(role -> {
                    String roleName = role.getName().name();
                    log.trace("Adding authority for user {}: {}", user.getEmail(), roleName);
                    return new SimpleGrantedAuthority(roleName);
                })
                .collect(Collectors.toSet());
    }
}
