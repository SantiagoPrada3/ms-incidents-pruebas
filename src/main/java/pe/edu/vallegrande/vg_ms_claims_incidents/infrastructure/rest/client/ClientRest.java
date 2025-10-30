package pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.rest.client;

import pe.edu.vallegrande.vg_ms_claims_incidents.application.services.IncidentService;
import pe.edu.vallegrande.vg_ms_claims_incidents.application.services.IncidentTypeService;
import pe.edu.vallegrande.vg_ms_claims_incidents.application.services.UserEnrichmentService;
import pe.edu.vallegrande.vg_ms_claims_incidents.application.services.IncidentResolutionService;
import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.client.UserApiClient;
import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.dto.IncidentDTO;
import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.dto.IncidentCreateDTO;
import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.dto.IncidentTypeDTO;
import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.dto.IncidentResolutionDTO;
import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.dto.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/client")
public class ClientRest {

    private final IncidentService incidentService;
    private final IncidentTypeService incidentTypeService;
    private final UserEnrichmentService userEnrichmentService;
    private final UserApiClient userApiClient;
    private final IncidentResolutionService incidentResolutionService;

    public ClientRest(IncidentService incidentService, 
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
     * Crear incidentes (CLIENT)
     * Endpoint: /api/client/incidents/create
     */
    @PostMapping("/incidents/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<IncidentDTO> createIncident(@RequestBody IncidentCreateDTO incidentCreateDTO) {
        log.info("Client - Creando nuevo incidente: {}", incidentCreateDTO.getIncidentCode());
        
        // Validación específica para clientes
        validateClientCreateDTO(incidentCreateDTO);
        IncidentDTO incidentDTO = convertToIncidentDTO(incidentCreateDTO);
        
        // Para clientes, establecer estado inicial como REPORTED
        incidentDTO.setStatus("REPORTED");
        incidentDTO.setResolved(false);
        incidentDTO.setRecordStatus("ACTIVE");
        
        return incidentService.save(incidentDTO)
                .doOnSuccess(saved -> log.info("Client - Incidente creado exitosamente: {}", saved.getIncidentCode()))
                .doOnError(error -> log.error("Client - Error al crear incidente: {}", error.getMessage()));
    }

    /**
     * Mis incidentes (CLIENT)
     * Endpoint: /api/client/incidents/my-incidents
     */
    @GetMapping("/incidents/my-incidents")
    @ResponseStatus(HttpStatus.OK)
    public Flux<IncidentDTO> getMyIncidents(@RequestParam String userId) {
        log.info("Client - Obteniendo incidentes del usuario: {}", userId);
        return incidentService.findByOrganizationId(userId)
                .switchIfEmpty(Flux.empty())
                .doOnNext(incident -> log.debug("Incidente encontrado: {}", incident.getIncidentCode()));
    }

    /**
     * Seguimiento de incidente (CLIENT)
     * Endpoint: /api/client/incidents/track/{id}
     */
    @GetMapping("/incidents/track/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Map<String, Object>> trackIncident(@PathVariable String id) {
        log.info("Client - Seguimiento de incidente: {}", id);
        
        return incidentService.findById(id)
                .map(incident -> {
                    Map<String, Object> trackingInfo = new HashMap<>();
                    trackingInfo.put("incidentId", incident.getId());
                    trackingInfo.put("incidentCode", incident.getIncidentCode());
                    trackingInfo.put("title", incident.getTitle());
                    trackingInfo.put("status", incident.getStatus());
                    trackingInfo.put("severity", incident.getSeverity());
                    trackingInfo.put("resolved", incident.getResolved());
                    trackingInfo.put("incidentDate", incident.getIncidentDate());
                    trackingInfo.put("assignedToUserId", incident.getAssignedToUserId());
                    trackingInfo.put("resolutionNotes", incident.getResolutionNotes());
                    trackingInfo.put("lastUpdated", Instant.now().toString());
                    
                    // Información de progreso
                    Map<String, Object> progress = new HashMap<>();
                    switch (incident.getStatus()) {
                        case "REPORTED":
                            progress.put("step", 1);
                            progress.put("stepName", "Reportado");
                            progress.put("description", "Su incidente ha sido recibido y está siendo revisado");
                            break;
                        case "ASSIGNED":
                            progress.put("step", 2);
                            progress.put("stepName", "Asignado");
                            progress.put("description", "Su incidente ha sido asignado a un técnico");
                            break;
                        case "IN_PROGRESS":
                            progress.put("step", 3);
                            progress.put("stepName", "En Progreso");
                            progress.put("description", "El técnico está trabajando en resolver su incidente");
                            break;
                        case "RESOLVED":
                            progress.put("step", 4);
                            progress.put("stepName", "Resuelto");
                            progress.put("description", "Su incidente ha sido resuelto");
                            break;
                        default:
                            progress.put("step", 1);
                            progress.put("stepName", "En Revisión");
                            progress.put("description", "Su incidente está siendo procesado");
                    }
                    trackingInfo.put("progress", progress);
                    
                    return trackingInfo;
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Incidente no encontrado")));
    }

    /**
     * Obtener incidente por ID (CLIENT) - Solo para incidentes propios
     */
    @GetMapping("/incidents/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<IncidentDTO> getIncidentById(@PathVariable String id, 
                                           @RequestParam(required = false) String userId) {
        log.info("Client - Obteniendo incidente con ID: {} para usuario: {}", id, userId);
        
        return incidentService.findById(id)
                .filter(incident -> userId == null || userId.equals(incident.getReportedByUserId()))
                .switchIfEmpty(Mono.error(new RuntimeException("Incidente no encontrado o sin permisos")));
    }

    /**
     * Obtener incidentes por zona (CLIENT) - Solo incidentes públicos
     */
    @GetMapping("/incidents/zone/{zoneId}")
    @ResponseStatus(HttpStatus.OK)
    public Flux<IncidentDTO> getIncidentsByZone(@PathVariable String zoneId) {
        log.info("Client - Obteniendo incidentes públicos de la zona: {}", zoneId);
        return incidentService.findByZoneId(zoneId)
                .filter(incident -> "ACTIVE".equals(incident.getRecordStatus()));
    }

    /**
     * Obtener incidentes por tipo (CLIENT)
     */
    @GetMapping("/incidents/type/{incidentTypeId}")
    @ResponseStatus(HttpStatus.OK)
    public Flux<IncidentDTO> getIncidentsByType(@PathVariable String incidentTypeId) {
        log.info("Client - Obteniendo incidentes por tipo: {}", incidentTypeId);
        return incidentService.findByIncidentTypeId(incidentTypeId)
                .filter(incident -> "ACTIVE".equals(incident.getRecordStatus()));
    }

    /**
     * Obtener incidentes por categoría (CLIENT)
     */
    @GetMapping("/incidents/category/{category}")
    @ResponseStatus(HttpStatus.OK)
    public Flux<IncidentDTO> getIncidentsByCategory(@PathVariable String category) {
        log.info("Client - Obteniendo incidentes por categoría: {}", category);
        return incidentService.findByIncidentCategory(category)
                .filter(incident -> "ACTIVE".equals(incident.getRecordStatus()));
    }

    /**
     * Actualizar información limitada del incidente (CLIENT)
     * Solo permite actualizar ciertos campos
     */
    @PatchMapping("/incidents/{id}/update")
    @ResponseStatus(HttpStatus.OK)
    public Mono<IncidentDTO> updateIncidentLimited(@PathVariable String id, 
                                                 @RequestBody Map<String, Object> updates,
                                                 @RequestParam String userId) {
        log.info("Client - Actualizando incidente {} por usuario {}", id, userId);
        
        return incidentService.findById(id)
                .filter(incident -> userId.equals(incident.getReportedByUserId()))
                .flatMap(incident -> {
                    // Solo permitir actualizar ciertos campos para clientes
                    if (updates.containsKey("description")) {
                        incident.setDescription((String) updates.get("description"));
                    }
                    if (updates.containsKey("title")) {
                        incident.setTitle((String) updates.get("title"));
                    }
                    // No permitir cambiar estado, severidad, etc.
                    
                    return incidentService.update(id, incident);
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Incidente no encontrado o sin permisos para editar")));
    }

    /**
     * Buscar incidentes por texto (CLIENT)
     */
    @GetMapping("/incidents/search")
    @ResponseStatus(HttpStatus.OK)
    public Flux<IncidentDTO> searchIncidents(@RequestParam String query,
                                           @RequestParam(required = false) String userId) {
        log.info("Client - Buscando incidentes con query: {} para usuario: {}", query, userId);
        
        return incidentService.findAll()
                .filter(incident -> {
                    // Filtrar solo incidentes activos y del usuario si se especifica
                    if (!"ACTIVE".equals(incident.getRecordStatus())) {
                        return false;
                    }
                    if (userId != null && !userId.equals(incident.getReportedByUserId())) {
                        return false;
                    }
                    
                    // Buscar en título, descripción o código
                    String searchText = query.toLowerCase();
                    return incident.getTitle().toLowerCase().contains(searchText) ||
                           incident.getDescription().toLowerCase().contains(searchText) ||
                           incident.getIncidentCode().toLowerCase().contains(searchText);
                });
    }

    /**
     * Estadísticas básicas para el cliente
     */
    @GetMapping("/incidents/stats/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Map<String, Object>> getUserIncidentStats(@PathVariable String userId) {
        log.info("Client - Obteniendo estadísticas de incidentes para usuario: {}", userId);
        
        return incidentService.findByOrganizationId(userId)
                .collectList()
                .map(incidents -> {
                    Map<String, Object> stats = new HashMap<>();
                    
                    long totalIncidents = incidents.size();
                    long resolvedIncidents = incidents.stream().filter(IncidentDTO::getResolved).count();
                    long pendingIncidents = totalIncidents - resolvedIncidents;
                    
                    stats.put("totalIncidents", totalIncidents);
                    stats.put("resolvedIncidents", resolvedIncidents);
                    stats.put("pendingIncidents", pendingIncidents);
                    stats.put("timestamp", Instant.now().toString());
                    
                    return stats;
                });
    }

    /**
     * Endpoint de prueba para Client
     */
    @GetMapping("/test")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Map<String, String>> testClientEndpoint() {
        log.info("Client - Endpoint de prueba llamado");
        Map<String, String> response = new HashMap<>();
        response.put("message", "Client Incidents API funcionando correctamente");
        response.put("status", "OK");
        response.put("role", "CLIENT");
        return Mono.just(response);
    }

    /**
     * Validar conectividad para el cliente
     */
    @GetMapping("/ping")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Map<String, String>> ping() {
        log.info("Client - Endpoint ping llamado");
        Map<String, String> response = new HashMap<>();
        response.put("message", "Cliente - Backend funcionando correctamente");
        response.put("timestamp", Instant.now().toString());
        response.put("status", "OK");
        return Mono.just(response);
    }

    // ================================
    // ENDPOINTS DE TIPOS DE INCIDENCIAS (CLIENT - SOLO LECTURA)
    // ================================

    /**
     * Obtener todos los tipos de incidencias disponibles (CLIENT)
     * Solo tipos activos y disponibles para reportar
     */
    @GetMapping("/incident-types")
    @ResponseStatus(HttpStatus.OK)
    public Flux<IncidentTypeDTO> getAvailableIncidentTypes() {
        log.info("Client - Obteniendo tipos de incidencias disponibles");
        return incidentTypeService.findAll()
                .filter(type -> "ACTIVE".equals(type.getStatus()))
                .doOnNext(type -> log.debug("Tipo de incidencia disponible: {}", type.getTypeName()));
    }

    /**
     * Obtener tipo de incidencia por ID (CLIENT)
     * Solo para consulta al crear incidentes
     */
    @GetMapping("/incident-types/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<IncidentTypeDTO> getIncidentTypeById(@PathVariable String id) {
        log.info("Client - Obteniendo tipo de incidencia con ID: {}", id);
        return incidentTypeService.findById(id)
                .filter(type -> "ACTIVE".equals(type.getStatus()))
                .switchIfEmpty(Mono.error(new RuntimeException("Tipo de incidencia no disponible")));
    }

    /**
     * Obtener tipos de incidencias por nivel de prioridad (CLIENT)
     */
    @GetMapping("/incident-types/priority/{priorityLevel}")
    @ResponseStatus(HttpStatus.OK)
    public Flux<IncidentTypeDTO> getIncidentTypesByPriority(@PathVariable String priorityLevel) {
        log.info("Client - Obteniendo tipos de incidencias con prioridad: {}", priorityLevel);
        return incidentTypeService.findAll()
                .filter(type -> "ACTIVE".equals(type.getStatus()) && 
                               priorityLevel.equals(type.getPriorityLevel()));
    }

    /**
     * Buscar tipos de incidencias por nombre (CLIENT)
     */
    @GetMapping("/incident-types/search")
    @ResponseStatus(HttpStatus.OK)
    public Flux<IncidentTypeDTO> searchIncidentTypes(@RequestParam String query) {
        log.info("Client - Buscando tipos de incidencias con query: {}", query);
        return incidentTypeService.findAll()
                .filter(type -> "ACTIVE".equals(type.getStatus()))
                .filter(type -> {
                    String searchText = query.toLowerCase();
                    return type.getTypeName().toLowerCase().contains(searchText) ||
                           (type.getDescription() != null && type.getDescription().toLowerCase().contains(searchText));
                });
    }

    // ================================
    // ENDPOINTS DE USUARIOS (CLIENT - LIMITADO)
    // ================================

    /**
     * Obtener información básica del propio usuario (CLIENT)
     */
    @GetMapping("/user/profile/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<UserDTO> getUserProfile(@PathVariable String userId) {
        log.info("Client - Obteniendo perfil de usuario: {}", userId);
        return userEnrichmentService.getUserById(userId)
                .doOnSuccess(user -> log.info("Perfil obtenido para: {}", user.getFullName()));
    }

    /**
     * Verificar disponibilidad del servicio (CLIENT)
     */
    @GetMapping("/system/status")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Map<String, Object>> getSystemStatus() {
        log.info("Client - Verificando estado del sistema");
        
        return userEnrichmentService.isUserServiceAvailable()
                .map(userServiceUp -> {
                    Map<String, Object> status = new HashMap<>();
                    status.put("status", "AVAILABLE");
                    status.put("timestamp", Instant.now().toString());
                    status.put("userServiceAvailable", userServiceUp);
                    status.put("incidentServiceAvailable", true);
                    
                    return status;
                });
    }

    // ================================
    // ENDPOINTS DE RESOLUCIONES (CLIENT - SOLO LECTURA)
    // ================================

    /**
     * Obtener resolución de incidente (CLIENT) - Solo lectura
     * Solo para incidentes propios del usuario
     */
    @GetMapping("/incidents/{id}/resolution")
    @ResponseStatus(HttpStatus.OK)
    public Mono<IncidentResolutionDTO> getIncidentResolution(@PathVariable String id,
                                                            @RequestParam String userId) {
        log.info("Client - Obteniendo resolución para incidente: {} por usuario: {}", id, userId);
        
        // Primero verificar que el incidente pertenece al usuario
        return incidentService.findById(id)
                .filter(incident -> userId.equals(incident.getReportedByUserId()))
                .switchIfEmpty(Mono.error(new RuntimeException("Incidente no encontrado o sin permisos")))
                .flatMap(incident -> incidentResolutionService.findByIncidentId(id)
                        .next() // Obtener la primera (y probablemente única) resolución
                        .switchIfEmpty(Mono.error(new RuntimeException("No hay resolución disponible para este incidente")))
                );
    }

    /**
     * Verificar si un incidente tiene resolución (CLIENT)
     */
    @GetMapping("/incidents/{id}/has-resolution")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Map<String, Object>> checkIncidentResolution(@PathVariable String id,
                                                            @RequestParam String userId) {
        log.info("Client - Verificando resolución para incidente: {} por usuario: {}", id, userId);
        
        return incidentService.findById(id)
                .filter(incident -> userId.equals(incident.getReportedByUserId()))
                .switchIfEmpty(Mono.error(new RuntimeException("Incidente no encontrado o sin permisos")))
                .flatMap(incident -> 
                    incidentResolutionService.findByIncidentId(id)
                            .hasElements()
                            .map(hasResolution -> {
                                Map<String, Object> result = new HashMap<>();
                                result.put("incidentCode", id);
                                result.put("hasResolution", hasResolution);
                                result.put("incidentStatus", incident.getStatus());
                                result.put("isResolved", incident.getResolved());
                                return result;
                            })
                );
    }

    // Métodos auxiliares
    private void validateClientCreateDTO(IncidentCreateDTO createDTO) {
        if (createDTO.getIncidentCode() == null || createDTO.getIncidentCode().trim().isEmpty()) {
            throw new IllegalArgumentException("El código del incidente es obligatorio");
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
        if (createDTO.getIncidentTypeId() == null || createDTO.getIncidentTypeId().trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de incidente es obligatorio");
        }
        
        // Validaciones adicionales para clientes
        if (createDTO.getSeverity() == null) {
            createDTO.setSeverity("MEDIUM"); // Valor por defecto
        }
        if (createDTO.getIncidentCategory() == null) {
            createDTO.setIncidentCategory("GENERAL"); // Valor por defecto
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
        incidentDTO.setResolved(createDTO.getResolved() != null ? createDTO.getResolved() : false);
        incidentDTO.setResolutionNotes(createDTO.getResolutionNotes());
        incidentDTO.setRecordStatus(createDTO.getRecordStatus() != null ? createDTO.getRecordStatus() : "ACTIVE");
        return incidentDTO;
    }
}
