package pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.rest.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.vg_ms_claims_incidents.application.services.IncidentService;
import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.dto.IncidentDTO;
import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.exception.RecursoNoEncontradoException;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * REST Controller INTERNO para comunicación entre microservicios
 * - SIN autenticación JWT (solo para uso interno)
 * - Endpoints simples para otros microservicios del ecosistema JASS
 */
@RestController
@RequestMapping("/internal")
public class InternalRest {

    private static final Logger log = LoggerFactory.getLogger(InternalRest.class);

    private final IncidentService incidentService;

    public InternalRest(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    /**
     * Obtener todos los incidentes de una organización
     * GET /internal/organizations/{organizationId}/incidents
     */
    @GetMapping("/organizations/{organizationId}/incidents")
    public Mono<List<IncidentDTO>> getIncidentsByOrganization(@PathVariable String organizationId) {
        log.info("[INTERNAL] 🔍 Obteniendo incidentes de organización: {}", organizationId);

        return incidentService.findByOrganizationId(organizationId)
                .collectList()
                .doOnNext(incidents -> 
                    log.info("[INTERNAL] ✅ {} incidentes obtenidos de organización: {}", incidents.size(), organizationId));
    }

    /**
     * Obtener información de un incidente específico
     * GET /internal/incidents/{incidentId}
     */
    @GetMapping("/incidents/{incidentId}")
    public Mono<IncidentDTO> getIncidentById(@PathVariable String incidentId) {
        log.info("[INTERNAL] 🔍 Obteniendo información del incidente: {}", incidentId);

        return incidentService.findById(incidentId)
                .switchIfEmpty(Mono.error(new RecursoNoEncontradoException("Incidente no encontrado: " + incidentId)))
                .doOnNext(incident -> 
                    log.info("[INTERNAL] ✅ Incidente obtenido: {}", incident.getId()));
    }

    /**
     * Obtener incidentes por estado
     * GET /internal/incidents/status/{status}
     */
    @GetMapping("/incidents/status/{status}")
    public Mono<List<IncidentDTO>> getIncidentsByStatus(@PathVariable String status) {
        log.info("[INTERNAL] 🔍 Obteniendo incidentes por estado: {}", status);

        return incidentService.findByStatus(status)
                .collectList()
                .doOnNext(incidents -> 
                    log.info("[INTERNAL] ✅ {} incidentes obtenidos con estado: {}", incidents.size(), status));
    }

    /**
     * Obtener incidentes por tipo
     * GET /internal/incidents/type/{typeId}
     */
    @GetMapping("/incidents/type/{typeId}")
    public Mono<List<IncidentDTO>> getIncidentsByType(@PathVariable String typeId) {
        log.info("[INTERNAL] 🔍 Obteniendo incidentes por tipo: {}", typeId);

        return incidentService.findByIncidentTypeId(typeId)
                .collectList()
                .doOnNext(incidents -> 
                    log.info("[INTERNAL] ✅ {} incidentes obtenidos con tipo: {}", incidents.size(), typeId));
    }

    /**
     * Obtener incidentes por gravedad
     * GET /internal/incidents/severity/{severity}
     */
    @GetMapping("/incidents/severity/{severity}")
    public Mono<List<IncidentDTO>> getIncidentsBySeverity(@PathVariable String severity) {
        log.info("[INTERNAL] 🔍 Obteniendo incidentes por gravedad: {}", severity);

        return incidentService.findBySeverity(severity)
                .collectList()
                .doOnNext(incidents -> 
                    log.info("[INTERNAL] ✅ {} incidentes obtenidos con gravedad: {}", incidents.size(), severity));
    }

    /**
     * Obtener incidentes por zona
     * GET /internal/incidents/zone/{zoneId}
     */
    @GetMapping("/incidents/zone/{zoneId}")
    public Mono<List<IncidentDTO>> getIncidentsByZone(@PathVariable String zoneId) {
        log.info("[INTERNAL] 🔍 Obteniendo incidentes por zona: {}", zoneId);

        return incidentService.findByZoneId(zoneId)
                .collectList()
                .doOnNext(incidents -> 
                    log.info("[INTERNAL] ✅ {} incidentes obtenidos con zona: {}", incidents.size(), zoneId));
    }

    /**
     * Obtener incidentes asignados a un usuario
     * GET /internal/incidents/assigned/{userId}
     */
    @GetMapping("/incidents/assigned/{userId}")
    public Mono<List<IncidentDTO>> getIncidentsAssignedToUser(@PathVariable String userId) {
        log.info("[INTERNAL] 🔍 Obteniendo incidentes asignados al usuario: {}", userId);

        return incidentService.findByAssignedToUserId(userId)
                .collectList()
                .doOnNext(incidents -> 
                    log.info("[INTERNAL] ✅ {} incidentes obtenidos asignados al usuario: {}", incidents.size(), userId));
    }

    /**
     * Obtener incidentes resueltos por un usuario
     * GET /internal/incidents/resolved/{userId}
     */
    @GetMapping("/incidents/resolved/{userId}")
    public Mono<List<IncidentDTO>> getIncidentsResolvedByUser(@PathVariable String userId) {
        log.info("[INTERNAL] 🔍 Obteniendo incidentes resueltos por el usuario: {}", userId);

        return incidentService.findByResolvedByUserId(userId)
                .collectList()
                .doOnNext(incidents -> 
                    log.info("[INTERNAL] ✅ {} incidentes obtenidos resueltos por el usuario: {}", incidents.size(), userId));
    }

    /**
     * Obtener incidentes por categoría
     * GET /internal/incidents/category/{category}
     */
    @GetMapping("/incidents/category/{category}")
    public Mono<List<IncidentDTO>> getIncidentsByCategory(@PathVariable String category) {
        log.info("[INTERNAL] 🔍 Obteniendo incidentes por categoría: {}", category);

        return incidentService.findByIncidentCategory(category)
                .collectList()
                .doOnNext(incidents -> 
                    log.info("[INTERNAL] ✅ {} incidentes obtenidos con categoría: {}", incidents.size(), category));
    }
}