package pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.service;

import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vg_ms_claims_incidents.application.services.UserEnrichmentService;
import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.client.UserApiClient;
import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class UserEnrichmentServiceImpl implements UserEnrichmentService {

    private static final Logger log = LoggerFactory.getLogger(UserEnrichmentServiceImpl.class);

    private final UserApiClient userApiClient;

    public UserEnrichmentServiceImpl(UserApiClient userApiClient) {
        this.userApiClient = userApiClient;
    }

    @Override
    public Mono<UserDTO> getUserById(String userId) {
        log.info("Servicio: Obteniendo usuario por ID: {}", userId);
        return userApiClient.getUserById(userId);
    }

    @Override
    public Mono<UserDTO> getUserByUsername(String username) {
        log.info("Servicio: Obteniendo usuario por username: {}", username);
        return userApiClient.getUserByUsername(username);
    }

    @Override
    public Mono<UserDTO> getUserWithFallback(String userId, String fallbackUsername) {
        log.info("Servicio: Obteniendo usuario con fallback: {} ({})", userId, fallbackUsername);
        return userApiClient.getUserByIdWithFallback(userId, fallbackUsername);
    }

    @Override
    public Mono<List<UserDTO>> getMultipleUsers(List<String> userIds) {
        log.info("Servicio: Obteniendo múltiples usuarios: {}", userIds);
        return userApiClient.getUsersByIds(userIds);
    }

    @Override
    public Mono<Boolean> isUserServiceAvailable() {
        log.info("Servicio: Verificando disponibilidad del servicio de usuarios");
        return userApiClient.checkHealth();
    }

    @Override
    public Mono<UserDTO> testUserIntegration(String username) {
        log.info("Servicio: Probando integración con username: {}", username);
        return userApiClient.testIntegration(username);
    }
}
