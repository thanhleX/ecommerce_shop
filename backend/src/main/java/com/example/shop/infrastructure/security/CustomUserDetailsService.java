package com.example.shop.infrastructure.security;

import com.example.shop.domain.entity.User;
import com.example.shop.domain.exception.ErrorCode;
import com.example.shop.domain.exception.ResourceNotFoundException;
import com.example.shop.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        List<GrantedAuthority> authorities = new java.util.ArrayList<>();

        // Add roles and permissions as authorities
        if (user.getRoles() != null) {
            user.getRoles().forEach(role -> {
                // Keep the ROLE_ prefix for backwards compatibility
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
                
                // Add permissions
                if (role.getPermissions() != null) {
                    role.getPermissions().forEach(permission -> {
                        authorities.add(new SimpleGrantedAuthority(permission.getName()));
                    });
                }
            });
        }

        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getIsActive(),
                true,
                true,
                true,
                authorities);
    }
}
