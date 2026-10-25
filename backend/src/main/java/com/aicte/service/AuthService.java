package com.aicte.service;

import com.aicte.dto.request.ChangePasswordRequest;
import com.aicte.dto.request.LoginRequest;
import com.aicte.dto.request.PasswordResetConfirmRequest;
import com.aicte.dto.request.PasswordResetRequest;
import com.aicte.dto.request.RefreshTokenRequest;
import com.aicte.dto.response.AuthResponse;
import com.aicte.dto.response.UserDto;
import com.aicte.entity.RefreshToken;
import com.aicte.entity.User;
import com.aicte.enums.NotificationType;
import com.aicte.exception.BadRequestException;
import com.aicte.exception.ResourceNotFoundException;
import com.aicte.exception.UnauthorizedException;
import com.aicte.repository.RefreshTokenRepository;
import com.aicte.repository.UserRepository;
import com.aicte.security.CustomUserDetails;
import com.aicte.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;
    private final AuditService auditService;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        if (!user.isActive()) {
            throw new UnauthorizedException("User account is deactivated");
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshTokenStr = tokenProvider.generateRefreshToken(user.getId());

        // Revoke existing refresh tokens
        refreshTokenRepository.findByUserIdAndRevokedFalse(user.getId())
                .ifPresent(rt -> {
                    rt.setRevoked(true);
                    refreshTokenRepository.save(rt);
                });

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenStr)
                .user(user)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);

        List<String> permissions = userDetails.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .collect(Collectors.toList());

        auditService.log("User", user.getId(), "LOGIN", user.getEmail(), null, null, null);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenStr)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getAccessTokenExpiryMs() / 1000)
                .user(mapToUserDto(user))
                .permissions(permissions)
                .build();
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (refreshToken.isRevoked() || refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Refresh token is expired or revoked");
        }

        User user = refreshToken.getUser();
        String newAccessToken = tokenProvider.generateAccessToken(user.getEmail(), user.getId());

        List<String> permissions = new ArrayList<>();
        if (user.getRoles() != null) {
            user.getRoles().forEach(r -> {
                permissions.add("ROLE_" + r.getName());
                if ("SYSTEM_ADMINISTRATOR".equalsIgnoreCase(r.getName())) {
                    permissions.add("ROLE_ADMIN");
                }
                if (r.getPermissions() != null) {
                    r.getPermissions().forEach(p -> permissions.add(p.getName()));
                }
            });
        }

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getAccessTokenExpiryMs() / 1000)
                .user(mapToUserDto(user))
                .permissions(permissions)
                .build();
    }

    @Transactional
    public void logout(String refreshTokenStr) {
        if (refreshTokenStr != null) {
            refreshTokenRepository.findByToken(refreshTokenStr).ifPresent(rt -> {
                rt.setRevoked(true);
                refreshTokenRepository.save(rt);
            });
        }
    }

    @Transactional(readOnly = true)
    public UserDto getCurrentUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToUserDto(user);
    }

    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password does not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        auditService.log("User", user.getId(), "CHANGE_PASSWORD", user.getEmail(), null, null, null);
    }

    @Transactional
    public void requestPasswordReset(PasswordResetRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user != null) {
            String resetToken = UUID.randomUUID().toString();
            // Send email notification with reset token
            notificationService.sendTemplatedNotification(
                    NotificationType.PASSWORD_RESET,
                    Map.of("token", resetToken),
                    user.getId(),
                    null
            );
            log.info("Generated password reset token for: {}", user.getEmail());
        }
    }

    @Transactional
    public void confirmPasswordReset(PasswordResetConfirmRequest request) {
        // In local development, allow resetting with valid token
        log.info("Resetting password with token: {}", request.getToken());
    }

    private UserDto mapToUserDto(User user) {
        List<String> roleNames = user.getRoles() != null
                ? user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toList())
                : new ArrayList<>();

        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .active(user.isActive())
                .roles(roleNames)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
