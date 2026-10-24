package com.aicte.service;

import com.aicte.dto.request.CreateUserRequest;
import com.aicte.dto.request.UpdateUserRequest;
import com.aicte.dto.response.UserDto;
import com.aicte.entity.Role;
import com.aicte.entity.User;
import com.aicte.exception.DuplicateResourceException;
import com.aicte.exception.ResourceNotFoundException;
import com.aicte.repository.RoleRepository;
import com.aicte.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    @Transactional
    public UserDto createUser(CreateUserRequest request, String createdBy) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User already exists with email: " + request.getEmail());
        }

        Set<Role> roles = new HashSet<>();
        if (request.getRoles() != null) {
            for (String roleName : request.getRoles()) {
                roleRepository.findByName(roleName).ifPresent(roles::add);
            }
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .active(true)
                .emailVerified(true)
                .roles(roles)
                .build();
        user.setCreatedBy(createdBy);

        User saved = userRepository.save(user);
        auditService.log("User", saved.getId(), "CREATE", createdBy, null, saved.getEmail(), null);
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return mapToDto(user);
    }

    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::mapToDto);
    }

    @Transactional
    public UserDto updateUser(UUID id, UpdateUserRequest request, String updatedBy) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        user.setUpdatedBy(updatedBy);

        User saved = userRepository.save(user);
        auditService.log("User", saved.getId(), "UPDATE", updatedBy, null, saved.getEmail(), null);
        return mapToDto(saved);
    }

    @Transactional
    public void activateUser(UUID id, String performedBy) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        user.setActive(true);
        userRepository.save(user);
        auditService.log("User", user.getId(), "ACTIVATE", performedBy, null, null, null);
    }

    @Transactional
    public void deactivateUser(UUID id, String performedBy) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        user.setActive(false);
        userRepository.save(user);
        auditService.log("User", user.getId(), "DEACTIVATE", performedBy, null, null, null);
    }

    public UserDto mapToDto(User user) {
        List<String> roleNames = user.getRoles() != null
                ? user.getRoles().stream().map(Role::getName).collect(Collectors.toList())
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
