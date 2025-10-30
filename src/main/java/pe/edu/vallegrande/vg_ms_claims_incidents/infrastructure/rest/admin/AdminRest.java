package pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.rest.admin;

import pe.edu.vallegrande.vg_ms_claims_incidents.application.services.IncidentService;
import pe.edu.vallegrande.vg_ms_claims_incidents.application.services.IncidentTypeService;
import pe.edu.vallegrande.vg_ms_claims_incidents.application.services.UserEnrichmentService;
import pe.edu.vallegrande.vg_ms_claims_incidents.application.services.IncidentResolutionService;
import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.client.UserApiClient;
import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.repository.IncidentRepository;
import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.dto.IncidentDTO;
import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.dto.IncidentCreateDTO;
import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.dto.IncidentTypeDTO;
import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.dto.IncidentEnrichedDTO;
import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.dto.IncidentResolutionDTO;
import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.dto.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "API de administración para gestión completa de incidentes y resoluciones")
public class AdminRest {

    private static final Logger log = LoggerFactory.getLogger(AdminRest.class);

    private final IncidentService incidentService;
    private final IncidentTypeService incidentTypeService;
    private final UserEnrichmentService userEnrichmentService;
    private final UserApiClient userApiClient;
    private final IncidentResolutionService incidentResolutionService;

    public AdminRest(IncidentService incidentService, 
                    IncidentTypeService incidentTypeService,
                    UserEnrichmentService userEnrichmentService,
                    UserApiClient userApiClient,
                    IncidentResolutionService incidentResolutionService) {
        this.incidentService = incidentService;
        this.incidentTypeService = incidentTypeService;
        this.userEnrichmentService = userEnrichmentService;
        this.userApiClient = userApiClient;
        this.incidentResolutionService = incidentResolutionService;
    }

    /**
     * Gestionar incidentes de la zona (ADMIN)
     * Endpoint: /api/admin/incidents/manage
     */
    @GetMapping("/incidents/manage")
    @ResponseStatus(HttpStatus.OK)
    public Flux<IncidentDTO> manageIncidents(@RequestParam(required = false) String zoneId,
                                           @RequestParam(required = false) String organizationId,
                                           @RequestParam(required = false) String severity,
                                           @RequestParam(required = false) String status) {
        log.info("Admin - Gestionando incidentes con filtros: zoneId={}, organizationId={}, severity={}, status={}", 
                 zoneId, organizationId, severity, status);
        
        if (zoneId != null) {
            return incidentService.findByZoneId(zoneId);
        } else if (organizationId != null) {
            return incidentService.findByOrganizationId(organizationId);
        } else if (severity != null) {
            return incidentService.findBySeverity(severity);
        } else if (status != null) {
            return incidentService.findByStatus(status);
        } else {
            return incidentService.findAll();
        }
    }

    /**
     * Asignar responsables (ADMIN)
     * Endpoint: /api/admin/incidents/assign
     */
    @PatchMapping("/incidents/assign")
    @ResponseStatus(HttpStatus.OK)
    public Mono<IncidentDTO> assignResponsible(@RequestBody Map<String, String> assignmentData) {
        log.info("Admin - Asignando responsable a incidente");
        
        String incidentId = assignmentData.get("incidentId");
        String assignedToUserId = assignmentData.get("assignedToUserId");
        
        if (incidentId == null || assignedToUserId == null) {
            throw new IllegalArgumentException("incidentId y assignedToUserId son requeridos");
        }
        
        log.info("Asignando usuario {} al incidente {}", assignedToUserId, incidentId);
        
        return incidentService.findById(incidentId)
                .flatMap(incident -> {
                    incident.setAssignedToUserId(assignedToUserId);
                    incident.setStatus("ASSIGNED");
                    return incidentService.update(incidentId, incident);
                });
    }

    /**
     * Resolver incidencias (ADMIN)
     * Endpoint: /api/admin/incidents/resolve
     */
    @PatchMapping("/incidents/resolve")
    @ResponseStatus(HttpStatus.OK)
    public Mono<IncidentDTO> resolveIncident(@RequestBody Map<String, Object> resolutionData) {
        log.info("Admin - Resolviendo incidente");
        
        String incidentId = (String) resolutionData.get("incidentId");
        String resolvedByUserId = (String) resolutionData.get("resolvedByUserId");
        String resolutionNotes = (String) resolutionData.get("resolutionNotes");
        
        if (incidentId == null || resolvedByUserId == null) {
            throw new IllegalArgumentException("incidentId y resolvedByUserId son requeridos");
        }
        
        log.info("Resolviendo incidente {} por usuario {}", incidentId, resolvedByUserId);
        
        return incidentService.findById(incidentId)
                .flatMap(incident -> {
                    incident.setResolvedByUserId(resolvedByUserId);
                    incident.setResolutionNotes(resolutionNotes);
                    incident.setResolved(true);
                    incident.setStatus("RESOLVED");
                    return incidentService.update(incidentId, incident);
                });
    }

    /**
     * Obtener todos los incidentes (ADMIN)
     */
    @GetMapping("/incidents")
    @ResponseStatus(HttpStatus.OK)
    public Flux<IncidentDTO> getAllIncidents() {
        log.info("Admin - Obteniendo todos los incidentes");
        return incidentService.findAll()
                .doOnSubscribe(subscription -> log.info("Subscribed to incident service"))
                .doOnNext(incident -> log.info("Received incident: {}", incident.getId()))
                .doOnError(error -> log.error("Error in getAllIncidents: ", error))
                .doOnComplete(() -> log.info("Completed getting all incidents"))
                .doFinally(signalType -> log.info("Finally signal: {}", signalType));
    }

    /**
     * Obtener incidente por ID (ADMIN)
     */
    @GetMapping("/incidents/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<IncidentDTO> getIncidentById(@PathVariable String id) {
        log.info("Admin - Obteniendo incidente con ID: {}", id);
        return incidentService.findById(id);
    }

    /**
     * Crear nuevo incidente (ADMIN)
     */
    @Operation(summary = "Crear nuevo incidente", 
               description = "Permite a los administradores crear un nuevo incidente en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Incidente creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/incidents")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<IncidentDTO> createIncident(@RequestBody IncidentCreateDTO incidentCreateDTO) {
        log.info("Admin - Creando nuevo incidente: {}", incidentCreateDTO.getIncidentCode());
        
        validateCreateDTO(incidentCreateDTO);
        IncidentDTO incidentDTO = convertToIncidentDTO(incidentCreateDTO);
        
        return incidentService.save(incidentDTO)
                .doOnSuccess(saved -> log.info("Admin - Incidente creado exitosamente: {}", saved.getIncidentCode()))
                .doOnError(error -> log.error("Admin - Error al crear incidente: {}", error.getMessage()));
    }

    /**
     * Actualizar incidente (ADMIN)
     */
    @PutMapping("/incidents/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<IncidentDTO> updateIncident(@PathVariable String id, @RequestBody IncidentDTO incidentDTO) {
        log.info("Admin - Actualizando incidente con ID: {}", id);
        return incidentService.update(id, incidentDTO);
    }

    /**
     * Eliminar incidente (ADMIN)
     */
    @DeleteMapping("/incidents/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteIncident(@PathVariable String id) {
        log.info("Admin - Eliminando incidente con ID: {}", id);
        return incidentService.deleteById(id);
    }

    /**
     * Restaurar incidente eliminado (ADMIN)
     */
    @PatchMapping("/incidents/{id}/restore")
    @ResponseStatus(HttpStatus.OK)
    public Mono<IncidentDTO> restoreIncident(@PathVariable String id) {
        log.info("Admin - Restaurando incidente con ID: {}", id);
        return incidentService.restoreById(id);
    }

    /**
     * Obtener incidentes por zona (ADMIN)
     */
    @GetMapping("/incidents/zone/{zoneId}")
    @ResponseStatus(HttpStatus.OK)
    public Flux<IncidentDTO> getIncidentsByZone(@PathVariable String zoneId) {
        log.info("Admin - Obteniendo incidentes de la zona: {}", zoneId);
        return incidentService.findByZoneId(zoneId);
    }

    /**
     * Obtener incidentes por severidad (ADMIN)
     */
    @GetMapping("/incidents/severity/{severity}")
    @ResponseStatus(HttpStatus.OK)
    public Flux<IncidentDTO> getIncidentsBySeverity(@PathVariable String severity) {
        log.info("Admin - Obteniendo incidentes por severidad: {}", severity);
        return incidentService.findBySeverity(severity);
    }

    /**
     * Obtener incidentes por estado (ADMIN)
     */
    @GetMapping("/incidents/status/{status}")
    @ResponseStatus(HttpStatus.OK)
    public Flux<IncidentDTO> getIncidentsByStatus(@PathVariable String status) {
        log.info("Admin - Obteniendo incidentes por estado: {}", status);
        return incidentService.findByStatus(status);
    }

    /**
     * Obtener incidentes asignados a un usuario (ADMIN)
     */
    @GetMapping("/incidents/assigned/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Flux<IncidentDTO> getIncidentsByAssignedUser(@PathVariable String userId) {
        log.info("Admin - Obteniendo incidentes asignados al usuario: {}", userId);
        return incidentService.findByAssignedToUserId(userId);
    }

    /**
     * Obtener incidentes por organización (ADMIN)
     */
    @GetMapping("/incidents/organization/{organizationId}")
    @ResponseStatus(HttpStatus.OK)
    public Flux<IncidentDTO> getIncidentsByOrganization(@PathVariable String organizationId) {
        log.info("Admin - Obteniendo incidentes de la organización: {}", organizationId);
        return incidentService.findByOrganizationId(organizationId);
    }

    /**
     * Estadísticas de incidentes (ADMIN)
     */
    @GetMapping("/incidents/stats")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Map<String, Object>> getIncidentStats() {
        log.info("Admin - Obteniendo estadísticas de incidentes");
        
        return incidentService.findAll()
                .collectList()
                .map(incidents -> {
                    Map<String, Object> stats = new HashMap<>();
                    
                    long totalIncidents = incidents.size();
                    long resolvedIncidents = incidents.stream().filter(IncidentDTO::getResolved).count();
                    long pendingIncidents = totalIncidents - resolvedIncidents;
                    
                    Map<String, Long> severityStats = incidents.stream()
                            .collect(java.util.stream.Collectors.groupingBy(
                                    IncidentDTO::getSeverity,
                                    java.util.stream.Collectors.counting()));
                    
                    Map<String, Long> statusStats = incidents.stream()
                            .collect(java.util.stream.Collectors.groupingBy(
                                    IncidentDTO::getStatus,
                                    java.util.stream.Collectors.counting()));
                    
                    stats.put("totalIncidents", totalIncidents);
                    stats.put("resolvedIncidents", resolvedIncidents);
                    stats.put("pendingIncidents", pendingIncidents);
                    stats.put("severityStats", severityStats);
                    stats.put("statusStats", statusStats);
                    stats.put("timestamp", Instant.now().toString());
                    
                    return stats;
                });
    }

    /**
     * Endpoint de prueba para Admin
     */
    @GetMapping("/test")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Map<String, String>> testAdminEndpoint() {
        log.info("Admin - Endpoint de prueba llamado");
        Map<String, String> response = new HashMap<>();
        response.put("message", "Admin Incidents API funcionando correctamente");
        response.put("status", "OK");
        response.put("role", "ADMIN");
        return Mono.just(response);
    }

    // ================================
    // ENDPOINTS DE TIPOS DE INCIDENCIAS
    // ================================

    /**
     * Obtener todos los tipos de incidencias (ADMIN)
     */
    @GetMapping("/incident-types")
    @ResponseStatus(HttpStatus.OK)
    public Flux<IncidentTypeDTO> getAllIncidentTypes() {
        log.info("Admin - Obteniendo todos los tipos de incidencias");
        return incidentTypeService.findAll();
    }

    /**
     * Obtener tipo de incidencia por ID (ADMIN)
     */
    @GetMapping("/incident-types/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<IncidentTypeDTO> getIncidentTypeById(@PathVariable String id) {
        log.info("Admin - Obteniendo tipo de incidencia con ID: {}", id);
        return incidentTypeService.findById(id);
    }

    /**
     * Crear nuevo tipo de incidencia (ADMIN)
     */
    @PostMapping("/incident-types")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<IncidentTypeDTO> createIncidentType(@RequestBody IncidentTypeDTO incidentTypeDTO) {
        log.info("Admin - Creando nuevo tipo de incidencia: {}", incidentTypeDTO.toString());
        return incidentTypeService.save(incidentTypeDTO)
                .doOnSuccess(saved -> log.info("Admin - Tipo de incidencia creado exitosamente: {}", saved.toString()))
                .doOnError(error -> log.error("Admin - Error al crear tipo de incidencia: {}", error.getMessage()));
    }

    /**
     * Actualizar tipo de incidencia (ADMIN)
     */
    @PutMapping("/incident-types/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<IncidentTypeDTO> updateIncidentType(@PathVariable String id, @RequestBody IncidentTypeDTO incidentTypeDTO) {
        log.info("Admin - Actualizando tipo de incidencia con ID: {}", id);
        return incidentTypeService.update(id, incidentTypeDTO);
    }

    /**
     * Eliminar tipo de incidencia (ADMIN)
     */
    @DeleteMapping("/incident-types/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteIncidentType(@PathVariable String id) {
        log.info("Admin - Eliminando tipo de incidencia con ID: {}", id);
        return incidentTypeService.deleteById(id);
    }

    /**
     * Restaurar tipo de incidencia (ADMIN)
     */
    @PatchMapping("/incident-types/{id}/restore")
    @ResponseStatus(HttpStatus.OK)
    public Mono<IncidentTypeDTO> restoreIncidentType(@PathVariable String id) {
        log.info("Admin - Restaurando tipo de incidencia con ID: {}", id);
        return incidentTypeService.restoreById(id);
    }

    // ================================
    // ENDPOINTS DE GESTIÓN DE USUARIOS
    // ================================

    /**
     * Obtener administradores de la organización (ADMIN)
     */
    @GetMapping("/users/admins")
    @ResponseStatus(HttpStatus.OK)
    public Mono<List<UserDTO>> getOrganizationAdmins() {
        log.info("Admin - Obteniendo administradores de la organización");
        
        return userApiClient.getOrganizationAdmins()
                .doOnSuccess(admins -> log.info("Administradores obtenidos: {}", admins.size()))
                .onErrorResume(error -> {
                    log.error("Error al obtener administradores: {}", error.getMessage());
                    return Mono.just(List.of());
                });
    }

    /**
     * Obtener clientes de la organización (ADMIN)
     */
    @GetMapping("/users/clients")
    @ResponseStatus(HttpStatus.OK)
    public Mono<List<UserDTO>> getOrganizationClients() {
        log.info("Admin - Obteniendo clientes de la organización");
        
        return userApiClient.getOrganizationClients()
                .doOnSuccess(clients -> log.info("Clientes obtenidos: {}", clients.size()))
                .onErrorResume(error -> {
                    log.error("Error al obtener clientes: {}", error.getMessage());
                    return Mono.just(List.of());
                });
    }

    /**
     * Obtener información de usuario por ID (ADMIN)
     */
    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<UserDTO> getUserById(@PathVariable String userId) {
        log.info("Admin - Obteniendo información de usuario: {}", userId);
        return userEnrichmentService.getUserById(userId);
    }

    /**
     * Obtener información de usuario por username (ADMIN)
     */
    @GetMapping("/users/username/{username}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<UserDTO> getUserByUsername(@PathVariable String username) {
        log.info("Admin - Obteniendo información de usuario por username: {}", username);
        return userEnrichmentService.getUserByUsername(username);
    }

    /**
     * Obtener incidentes enriquecidos con información de usuarios (ADMIN)
     */
    @GetMapping("/incidents/enriched")
    @ResponseStatus(HttpStatus.OK)
    public Flux<IncidentEnrichedDTO> getEnrichedIncidents() {
        log.info("Admin - Obteniendo incidentes enriquecidos con información de usuarios");
        
        return incidentService.findAll()
                .doOnNext(incident -> log.debug("Procesando incidente: {}", incident.getIncidentCode()))
                .flatMap(incident -> enrichIncidentWithUserInfo(incident)
                    .doOnSuccess(enriched -> log.debug("Incidente enriquecido exitosamente: {}", incident.getIncidentCode()))
                    .doOnError(error -> log.error("Error enriqueciendo incidente {}: {}", 
                        incident.getIncidentCode(), error.getMessage(), error))
                    .onErrorResume(error -> {
                        log.warn("Fallback: creando incidente enriquecido básico para: {} debido a error: {}", 
                            incident.getIncidentCode(), error.getMessage());
                        return createBasicEnrichedIncident(incident);
                    }))
                .doOnNext(enriched -> log.debug("Incidente enriquecido procesado: {} - Reportado por: {}", 
                    enriched.getIncidentCode(), enriched.getReporterFullName()))
                .doOnError(error -> log.error("Error general en flujo de incidentes enriquecidos: {}", error.getMessage(), error))
                .onErrorResume(error -> {
                    log.error("Error crítico en endpoint enriched incidents: {}", error.getMessage(), error);
                    return Flux.empty(); // Retornar flujo vacío en caso de error crítico
                });
    }

    /**
     * Obtener incidente enriquecido por ID (ADMIN)
     */
    @GetMapping("/incidents/{id}/enriched")
    @ResponseStatus(HttpStatus.OK)
    public Mono<IncidentEnrichedDTO> getEnrichedIncidentById(@PathVariable String id) {
        log.info("Admin - Obteniendo incidente enriquecido por ID: {}", id);
        
        return incidentService.findById(id)
                .flatMap(this::enrichIncidentWithUserInfo);
    }

    /**
     * Verificar salud del sistema incluyendo servicios externos (ADMIN)
     */
    @GetMapping("/system/health")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Map<String, Object>> getSystemHealth() {
        log.info("Admin - Verificando salud del sistema");
        
        return userEnrichmentService.isUserServiceAvailable()
                .map(userServiceUp -> {
                    Map<String, Object> health = new HashMap<>();
                    health.put("status", "UP");
                    health.put("timestamp", Instant.now().toString());
                    
                    Map<String, Object> services = new HashMap<>();
                    services.put("userService", userServiceUp ? "UP" : "DOWN");
                    services.put("incidentService", "UP"); // Asumimos que está UP si llegamos aquí
                    
                    health.put("services", services);
                    health.put("description", "Estado de los servicios del sistema");
                    
                    return health;
                });
    }

    /**
     * Test de integración con usuarios (ADMIN)
     */
    @GetMapping("/test/user-integration/{username}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<UserDTO> testUserIntegration(@PathVariable String username) {
        log.info("Admin - Probando integración con usuarios: {}", username);
        return userEnrichmentService.testUserIntegration(username);
    }

    // Métodos auxiliares
    private void validateCreateDTO(IncidentCreateDTO createDTO) {
        if (createDTO.getIncidentCode() == null || createDTO.getIncidentCode().trim().isEmpty()) {
            throw new IllegalArgumentException("El código del incidente es obligatorio");
        }
        if (createDTO.getOrganizationId() == null || createDTO.getOrganizationId().trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de la organización es obligatorio");
        }
        if (createDTO.getIncidentTypeId() == null || createDTO.getIncidentTypeId().trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de incidente es obligatorio");
        }
        if (createDTO.getTitle() == null || createDTO.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("El título del incidente es obligatorio");
        }
        if (createDTO.getDescription() == null || createDTO.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción es obligatoria");
        }
        if (createDTO.getReportedByUserId() == null || createDTO.getReportedByUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del usuario que reportó es obligatorio");
        }
    }

    private void validateResolutionDTO(IncidentResolutionDTO resolutionDTO) {
        if (resolutionDTO.getIncidentId() == null || resolutionDTO.getIncidentId().trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del incidente es obligatorio");
        }
        if (resolutionDTO.getResolutionType() == null || resolutionDTO.getResolutionType().trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de resolución es obligatorio");
        }
        if (resolutionDTO.getActionsTaken() == null || resolutionDTO.getActionsTaken().trim().isEmpty()) {
            throw new IllegalArgumentException("Las acciones tomadas son obligatorias");
        }
        if (resolutionDTO.getResolvedByUserId() == null || resolutionDTO.getResolvedByUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del usuario que resuelve es obligatorio");
        }
        if (resolutionDTO.getResolutionDate() == null) {
            resolutionDTO.setResolutionDate(new Date());
        }
        if (resolutionDTO.getCreatedAt() == null) {
            resolutionDTO.setCreatedAt(new Date());
        }
        // Validaciones de negocio
        if (resolutionDTO.getLaborHours() != null && resolutionDTO.getLaborHours() < 0) {
            throw new IllegalArgumentException("Las horas de trabajo no pueden ser negativas");
        }
        if (resolutionDTO.getTotalCost() != null && resolutionDTO.getTotalCost() < 0) {
            throw new IllegalArgumentException("El costo total no puede ser negativo");
        }
    }

    private IncidentDTO convertToIncidentDTO(IncidentCreateDTO createDTO) {
        IncidentDTO incidentDTO = new IncidentDTO();
        incidentDTO.setOrganizationId(createDTO.getOrganizationId());
        incidentDTO.setIncidentCode(createDTO.getIncidentCode());
        incidentDTO.setIncidentTypeId(createDTO.getIncidentTypeId());
        incidentDTO.setIncidentCategory(createDTO.getIncidentCategory());
        incidentDTO.setZoneId(createDTO.getZoneId());
        incidentDTO.setIncidentDate(createDTO.getIncidentDate());
        incidentDTO.setTitle(createDTO.getTitle());
        incidentDTO.setDescription(createDTO.getDescription());
        incidentDTO.setSeverity(createDTO.getSeverity());
        incidentDTO.setStatus(createDTO.getStatus());
        incidentDTO.setAffectedBoxesCount(createDTO.getAffectedBoxesCount());
        incidentDTO.setReportedByUserId(createDTO.getReportedByUserId());
        incidentDTO.setAssignedToUserId(createDTO.getAssignedToUserId());
        incidentDTO.setResolvedByUserId(createDTO.getResolvedByUserId());
        incidentDTO.setResolved(createDTO.getResolved());
        incidentDTO.setResolutionNotes(createDTO.getResolutionNotes());
        incidentDTO.setRecordStatus(createDTO.getRecordStatus());
        return incidentDTO;
    }

    /**
     * Enriquecer incidente con información completa de usuarios
     */
    private Mono<IncidentEnrichedDTO> enrichIncidentWithUserInfo(IncidentDTO incident) {
        try {
            log.debug("Enriqueciendo incidente: {} con IDs - Reporter: {}, Assigned: {}, Resolver: {}", 
                incident.getIncidentCode(), 
                incident.getReportedByUserId(), 
                incident.getAssignedToUserId(), 
                incident.getResolvedByUserId());
                
            IncidentEnrichedDTO enriched = new IncidentEnrichedDTO();
            
            // Copiar todos los campos del incidente original
            enriched.setId(incident.getId());
            enriched.setOrganizationId(incident.getOrganizationId());
            enriched.setIncidentCode(incident.getIncidentCode());
            enriched.setIncidentTypeId(incident.getIncidentTypeId());
            enriched.setIncidentCategory(incident.getIncidentCategory());
            enriched.setZoneId(incident.getZoneId());
            enriched.setIncidentDate(incident.getIncidentDate());
            enriched.setTitle(incident.getTitle());
            enriched.setDescription(incident.getDescription());
            enriched.setSeverity(incident.getSeverity());
            enriched.setStatus(incident.getStatus());
            enriched.setAffectedBoxesCount(incident.getAffectedBoxesCount());
            enriched.setReportedByUserId(incident.getReportedByUserId());
            enriched.setAssignedToUserId(incident.getAssignedToUserId());
            enriched.setResolvedByUserId(incident.getResolvedByUserId());
            enriched.setResolved(incident.getResolved());
            enriched.setResolutionNotes(incident.getResolutionNotes());
            enriched.setRecordStatus(incident.getRecordStatus());

            // Obtener información del reportante con manejo de errores
            Mono<UserDTO> reporterMono = incident.getReportedByUserId() != null 
                ? userEnrichmentService.getUserWithFallback(incident.getReportedByUserId(), "Reportante_" + incident.getReportedByUserId())
                    .doOnError(error -> log.warn("Error obteniendo reportante {}: {}", incident.getReportedByUserId(), error.getMessage()))
                    .onErrorReturn(createFallbackUserDTO(incident.getReportedByUserId(), "Reportante"))
                : Mono.just(createFallbackUserDTO(null, "Reportante"));

            // Obtener información del asignado con manejo de errores
            Mono<UserDTO> assignedMono = incident.getAssignedToUserId() != null 
                ? userEnrichmentService.getUserWithFallback(incident.getAssignedToUserId(), "Asignado_" + incident.getAssignedToUserId())
                    .doOnError(error -> log.warn("Error obteniendo asignado {}: {}", incident.getAssignedToUserId(), error.getMessage()))
                    .onErrorReturn(createFallbackUserDTO(incident.getAssignedToUserId(), "Asignado"))
                : Mono.just(createFallbackUserDTO(null, "Asignado"));

            // Obtener información del resolvedor con manejo de errores
            Mono<UserDTO> resolverMono = incident.getResolvedByUserId() != null 
                ? userEnrichmentService.getUserWithFallback(incident.getResolvedByUserId(), "Resolvedor_" + incident.getResolvedByUserId())
                    .doOnError(error -> log.warn("Error obteniendo resolvedor {}: {}", incident.getResolvedByUserId(), error.getMessage()))
                    .onErrorReturn(createFallbackUserDTO(incident.getResolvedByUserId(), "Resolvedor"))
                : Mono.just(createFallbackUserDTO(null, "Resolvedor"));

            return Mono.zip(reporterMono, assignedMono, resolverMono)
                .map(tuple -> {
                    UserDTO reporter = tuple.getT1();
                    UserDTO assigned = tuple.getT2();
                    UserDTO resolver = tuple.getT3();
                    
                    // IMPORTANTE: Asignar los usuarios obtenidos a los campos del objeto enriched usando reflection
                    try {
                        java.lang.reflect.Field reporterField = enriched.getClass().getDeclaredField("reporterInfo");
                        reporterField.setAccessible(true);
                        reporterField.set(enriched, reporter);
                        
                        java.lang.reflect.Field assignedField = enriched.getClass().getDeclaredField("assignedUserInfo");
                        assignedField.setAccessible(true);
                        assignedField.set(enriched, assigned);
                        
                        java.lang.reflect.Field resolverField = enriched.getClass().getDeclaredField("resolverInfo");
                        resolverField.setAccessible(true);
                        resolverField.set(enriched, resolver);
                        
                        log.debug("Usuarios enriquecidos asignados para incidente {} - Reporter: {}, Assigned: {}, Resolver: {}", 
                            incident.getIncidentCode(), 
                            reporter != null ? reporter.getFullName() : "null",
                            assigned != null ? assigned.getFullName() : "null", 
                            resolver != null ? resolver.getFullName() : "null");
                            
                    } catch (Exception e) {
                        log.error("Error asignando usuarios enriquecidos usando reflection: {}", e.getMessage());
                    }
                    
                    return enriched;
                })
                .doOnSuccess(result -> log.debug("Incidente {} enriquecido exitosamente", incident.getIncidentCode()))
                .doOnError(error -> log.error("Error final en enriquecimiento de incidente {}: {}", incident.getIncidentCode(), error.getMessage()));
                
        } catch (Exception e) {
            log.error("Error sincrónico en enrichIncidentWithUserInfo para incidente {}: {}", incident.getIncidentCode(), e.getMessage(), e);
            return createBasicEnrichedIncident(incident);
        }
    }

    // ================================
    // ENDPOINTS DE GESTIÓN DE RESOLUCIONES DE INCIDENTES
    // ================================

    /**
     * Obtener todas las resoluciones de incidentes (ADMIN)
     */
    @Operation(summary = "Obtener todas las resoluciones", 
               description = "Permite a los administradores obtener todas las resoluciones de incidentes del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resoluciones obtenidas exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/incident-resolutions")
    @ResponseStatus(HttpStatus.OK)
    public Flux<IncidentResolutionDTO> getAllIncidentResolutions() {
        log.info("Admin - Obteniendo todas las resoluciones de incidentes");
        return incidentResolutionService.findAll()
                .doOnNext(resolution -> log.debug("Resolución encontrada: {}", resolution.getId()))
                .doOnError(error -> log.error("Error al obtener resoluciones: {}", error.getMessage()))
                .doOnComplete(() -> log.info("Completada la obtención de todas las resoluciones"));
    }

    /**
     * Obtener resolución por ID (ADMIN)
     */
    @Operation(summary = "Obtener resolución por ID", 
               description = "Permite obtener una resolución específica por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resolución encontrada"),
        @ApiResponse(responseCode = "404", description = "Resolución no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/incident-resolutions/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<IncidentResolutionDTO> getIncidentResolutionById(
            @Parameter(description = "ID de la resolución", required = true) @PathVariable String id) {
        log.info("Admin - Obteniendo resolución con ID: {}", id);
        return incidentResolutionService.findById(id)
                .doOnSuccess(resolution -> log.info("Resolución encontrada: {}", resolution.getId()))
                .doOnError(error -> log.error("Error al obtener resolución {}: {}", id, error.getMessage()));
    }

    /**
     * Crear nueva resolución de incidente (ADMIN)
     */
    @Operation(summary = "Crear nueva resolución", 
               description = "Permite a los administradores crear una nueva resolución para un incidente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Resolución creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/incident-resolutions")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<IncidentResolutionDTO> createIncidentResolution(
            @RequestBody IncidentResolutionDTO resolutionDTO) {
        log.info("Admin - Creando nueva resolución para incidente: {}", resolutionDTO.getIncidentId());
        
        validateResolutionDTO(resolutionDTO);
        
        return incidentResolutionService.save(resolutionDTO)
                .doOnSuccess(saved -> log.info("Admin - Resolución creada exitosamente: {} para incidente: {}", 
                    saved.getId(), saved.getIncidentId()))
                .doOnError(error -> log.error("Admin - Error al crear resolución: {}", error.getMessage()));
    }

    /**
     * Actualizar resolución existente (ADMIN)
     */
    @Operation(summary = "Actualizar resolución", 
               description = "Permite actualizar una resolución existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resolución actualizada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Resolución no encontrada"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/incident-resolutions/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<IncidentResolutionDTO> updateIncidentResolution(
            @Parameter(description = "ID de la resolución", required = true) @PathVariable String id,
            @RequestBody IncidentResolutionDTO resolutionDTO) {
        log.info("Admin - Actualizando resolución con ID: {}", id);
        
        validateResolutionDTO(resolutionDTO);
        
        return incidentResolutionService.update(id, resolutionDTO)
                .doOnSuccess(updated -> log.info("Admin - Resolución actualizada exitosamente: {}", updated.getId()))
                .doOnError(error -> log.error("Admin - Error al actualizar resolución {}: {}", id, error.getMessage()));
    }

    /**
     * Eliminar resolución (ADMIN)
     */
    @Operation(summary = "Eliminar resolución", 
               description = "Permite eliminar una resolución del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Resolución eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Resolución no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/incident-resolutions/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteIncidentResolution(
            @Parameter(description = "ID de la resolución", required = true) @PathVariable String id) {
        log.info("Admin - Eliminando resolución con ID: {}", id);
        return incidentResolutionService.deleteById(id)
                .doOnSuccess(v -> log.info("Admin - Resolución eliminada exitosamente: {}", id))
                .doOnError(error -> log.error("Admin - Error al eliminar resolución {}: {}", id, error.getMessage()));
    }

    /**
     * Obtener resoluciones por ID de incidente (ADMIN)
     */
    @Operation(summary = "Obtener resoluciones por incidente", 
               description = "Permite obtener todas las resoluciones asociadas a un incidente específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resoluciones obtenidas exitosamente"),
        @ApiResponse(responseCode = "404", description = "Incidente no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/incident-resolutions/incident/{incidentId}")
    @ResponseStatus(HttpStatus.OK)
    public Flux<IncidentResolutionDTO> getResolutionsByIncidentId(
            @Parameter(description = "ID del incidente", required = true) @PathVariable String incidentId) {
        log.info("Admin - Obteniendo resoluciones para incidente: {}", incidentId);
        return incidentResolutionService.findByIncidentId(incidentId)
                .doOnNext(resolution -> log.debug("Resolución encontrada para incidente {}: {}", 
                    incidentId, resolution.getId()))
                .doOnError(error -> log.error("Error al obtener resoluciones para incidente {}: {}", 
                    incidentId, error.getMessage()))
                .doOnComplete(() -> log.info("Completada obtención de resoluciones para incidente: {}", incidentId));
    }

    /**
     * Resolver incidente con resolución detallada (ADMIN)
     */
    @Operation(summary = "Resolver incidente con detalles", 
               description = "Permite resolver un incidente creando una resolución detallada y actualizando el estado del incidente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Incidente resuelto exitosamente"),
        @ApiResponse(responseCode = "404", description = "Incidente no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/incident-resolutions/resolve-incident/{incidentId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<IncidentResolutionDTO> resolveIncidentWithDetails(
            @Parameter(description = "ID del incidente a resolver", required = true) @PathVariable String incidentId,
            @RequestBody IncidentResolutionDTO resolutionDTO) {
        log.info("Admin - Resolviendo incidente {} con resolución detallada", incidentId);
        
        // Establecer el ID del incidente en la resolución
        resolutionDTO.setIncidentId(incidentId);
        validateResolutionDTO(resolutionDTO);
        
        // Crear la resolución y actualizar el incidente
        return incidentResolutionService.save(resolutionDTO)
                .flatMap(savedResolution -> {
                    // Actualizar el estado del incidente a resuelto
                    return incidentService.findById(incidentId)
                            .flatMap(incident -> {
                                incident.setResolved(true);
                                incident.setStatus("RESOLVED");
                                incident.setResolvedByUserId(resolutionDTO.getResolvedByUserId());
                                incident.setResolutionNotes(resolutionDTO.getResolutionNotes());
                                return incidentService.update(incidentId, incident);
                            })
                            .then(Mono.just(savedResolution));
                })
                .doOnSuccess(resolution -> log.info("Admin - Incidente {} resuelto exitosamente con resolución: {}", 
                    incidentId, resolution.getId()))
                .doOnError(error -> log.error("Admin - Error al resolver incidente {}: {}", 
                    incidentId, error.getMessage()));
    }

    /**
     * Obtener estadísticas de resoluciones (ADMIN)
     */
    @Operation(summary = "Obtener estadísticas de resoluciones", 
               description = "Proporciona estadísticas detalladas sobre las resoluciones de incidentes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/incident-resolutions/stats")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Map<String, Object>> getResolutionStats() {
        log.info("Admin - Obteniendo estadísticas de resoluciones");
        
        return incidentResolutionService.findAll()
                .collectList()
                .map(resolutions -> {
                    Map<String, Object> stats = new HashMap<>();
                    
                    long totalResolutions = resolutions.size();
                    
                    // Estadísticas por tipo de resolución
                    Map<String, Long> resolutionTypeStats = resolutions.stream()
                            .collect(java.util.stream.Collectors.groupingBy(
                                    resolution -> resolution.getResolutionType() != null ? 
                                        resolution.getResolutionType() : "UNKNOWN",
                                    java.util.stream.Collectors.counting()));
                    
                    // Costo total
                    double totalCost = resolutions.stream()
                            .mapToDouble(resolution -> resolution.getTotalCost() != null ? 
                                resolution.getTotalCost() : 0.0)
                            .sum();
                    
                    // Horas de trabajo total
                    int totalLaborHours = resolutions.stream()
                            .mapToInt(resolution -> resolution.getLaborHours() != null ? 
                                resolution.getLaborHours() : 0)
                            .sum();
                    
                    // Resoluciones que requieren seguimiento
                    long followUpRequired = resolutions.stream()
                            .filter(resolution -> Boolean.TRUE.equals(resolution.getFollowUpRequired()))
                            .count();
                    
                    // Resoluciones con control de calidad aprobado
                    long qualityApproved = resolutions.stream()
                            .filter(resolution -> Boolean.TRUE.equals(resolution.getQualityCheck()))
                            .count();
                    
                    stats.put("totalResolutions", totalResolutions);
                    stats.put("resolutionTypeStats", resolutionTypeStats);
                    stats.put("totalCost", totalCost);
                    stats.put("totalLaborHours", totalLaborHours);
                    stats.put("followUpRequired", followUpRequired);
                    stats.put("qualityApproved", qualityApproved);
                    stats.put("averageCostPerResolution", totalResolutions > 0 ? totalCost / totalResolutions : 0);
                    stats.put("averageHoursPerResolution", totalResolutions > 0 ? (double) totalLaborHours / totalResolutions : 0);
                    stats.put("timestamp", Instant.now().toString());
                    
                    return stats;
                })
                .doOnSuccess(stats -> log.info("Admin - Estadísticas de resoluciones generadas exitosamente"))
                .doOnError(error -> log.error("Admin - Error al generar estadísticas de resoluciones: {}", error.getMessage()));
    }

    /**
     * Crear un incidente enriquecido básico cuando falla el enriquecimiento completo
     */
    private Mono<IncidentEnrichedDTO> createBasicEnrichedIncident(IncidentDTO incident) {
        IncidentEnrichedDTO enriched = new IncidentEnrichedDTO();
        
        // Copiar todos los campos del incidente original
        enriched.setId(incident.getId());
        enriched.setOrganizationId(incident.getOrganizationId());
        enriched.setIncidentCode(incident.getIncidentCode());
        enriched.setIncidentTypeId(incident.getIncidentTypeId());
        enriched.setIncidentCategory(incident.getIncidentCategory());
        enriched.setZoneId(incident.getZoneId());
        enriched.setIncidentDate(incident.getIncidentDate());
        enriched.setTitle(incident.getTitle());
        enriched.setDescription(incident.getDescription());
        enriched.setSeverity(incident.getSeverity());
        enriched.setStatus(incident.getStatus());
        enriched.setAffectedBoxesCount(incident.getAffectedBoxesCount());
        enriched.setReportedByUserId(incident.getReportedByUserId());
        enriched.setAssignedToUserId(incident.getAssignedToUserId());
        enriched.setResolvedByUserId(incident.getResolvedByUserId());
        enriched.setResolved(incident.getResolved());
        enriched.setResolutionNotes(incident.getResolutionNotes());
        enriched.setRecordStatus(incident.getRecordStatus());
        
        // Establecer información de usuario básica si los IDs están disponibles
        log.debug("Creando incidente enriquecido básico para: {}", incident.getIncidentCode());
        
        return Mono.just(enriched);
    }

    /**
     * Crear un UserDTO de fallback con datos básicos
     */
    private UserDTO createFallbackUserDTO(String userId, String role) {
        UserDTO fallbackUser = new UserDTO();
        
        try {
            // Usar reflection para establecer campos básicos
            setFieldValue(fallbackUser, "userId", userId != null ? userId : "unknown");
            setFieldValue(fallbackUser, "userCode", "UNKNOWN");
            setFieldValue(fallbackUser, "username", "unknown_user");
            setFieldValue(fallbackUser, "status", "UNKNOWN");
            setFieldValue(fallbackUser, "roles", List.of("UNKNOWN"));
            
            // Crear información personal básica
            UserDTO.PersonalInfo personalInfo = new UserDTO.PersonalInfo();
            if (role.equals("Reportante")) {
                setFieldValue(personalInfo, "firstName", "Usuario");
                setFieldValue(personalInfo, "lastName", "desconocido");
            } else if (role.equals("Asignado")) {
                setFieldValue(personalInfo, "firstName", "No");
                setFieldValue(personalInfo, "lastName", "asignado");
            } else if (role.equals("Resolvedor")) {
                setFieldValue(personalInfo, "firstName", "No");
                setFieldValue(personalInfo, "lastName", "resuelto");
            } else {
                setFieldValue(personalInfo, "firstName", "Usuario");
                setFieldValue(personalInfo, "lastName", "desconocido");
            }
            setFieldValue(personalInfo, "documentType", "N/A");
            setFieldValue(personalInfo, "documentNumber", "N/A");
            setFieldValue(fallbackUser, "personalInfo", personalInfo);
            
            // Crear información de contacto básica
            UserDTO.Contact contact = new UserDTO.Contact();
            setFieldValue(fallbackUser, "contact", contact);
            
            // Crear dirección básica
            UserDTO.Address address = new UserDTO.Address();
            setFieldValue(fallbackUser, "address", address);
            
        } catch (Exception e) {
            log.warn("Error creando usuario de respaldo: {}", e.getMessage());
        }
        
        log.debug("Creando usuario de respaldo: {} - {} con nombre: {}", 
            role, userId, fallbackUser.getFullName());
        
        return fallbackUser;
    }
    
    /**
     * Método auxiliar para establecer valores de campo usando reflection
     */
    private void setFieldValue(Object obj, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            log.debug("No se pudo establecer campo {} en {}: {}", fieldName, obj.getClass().getSimpleName(), e.getMessage());
        }
    }
}
