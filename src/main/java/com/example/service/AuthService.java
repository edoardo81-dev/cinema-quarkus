package com.example.service;

import com.example.dto.LoginRequestDTO;
import com.example.dto.LoginResponseDTO;
import com.example.exception.ConflictException;
import com.example.model.AdminUser;
import com.example.repository.AdminUserRepository;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Duration;
import java.util.Set;

@ApplicationScoped
public class AuthService {

    private final AdminUserRepository adminUserRepository;

    @Inject
    public AuthService(AdminUserRepository adminUserRepository) {
        this.adminUserRepository = adminUserRepository;
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {
        AdminUser adminUser = adminUserRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new ConflictException("Credenziali non valide"));

        if (!adminUser.getPassword().equals(dto.getPassword())) {
            throw new ConflictException("Credenziali non valide");
        }

        String token = Jwt.issuer("cinema-quarkus")
                .upn(adminUser.getUsername())
                .groups(Set.of(adminUser.getRole()))
                .expiresIn(Duration.ofHours(8))
                .sign();

        return new LoginResponseDTO(
                token,
                adminUser.getUsername(),
                adminUser.getRole()
        );
    }
}