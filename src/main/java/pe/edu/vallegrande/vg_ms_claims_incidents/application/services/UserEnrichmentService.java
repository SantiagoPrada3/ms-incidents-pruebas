package pe.edu.vallegrande.vg_ms_claims_incidents.application.services;

import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.dto.UserDTO;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import java.util.List;

public interface UserEnrichmentService {
    
    /**
     * Obtener usuario por ID
     */
    Mono<UserDTO> getUserById(String userId);
    
    /**
     * Obtener usuario por username
     */
    Mono<UserDTO> getUserByUsername(String username);
    
    /**
     * Obtener usuario con datos de respaldo
     */
    Mono<UserDTO> getUserWithFallback(String userId, String fallbackUsername);
    
    /**
     * Obtener múltiples usuarios
     */
    Mono<List<UserDTO>> getMultipleUsers(List<String> userIds);
    
    /**
     * Verificar disponibilidad del servicio
     */
    Mono<Boolean> isUserServiceAvailable();
    
    /**
     * Test de integración
     */
    Mono<UserDTO> testUserIntegration(String username);
}
