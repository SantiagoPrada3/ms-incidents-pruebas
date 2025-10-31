package pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vg_ms_claims_incidents.domain.models.Incident;
import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.dto.IncidentDTO;
import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.repository.IncidentRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class IncidentServiceImpl implements pe.edu.vallegrande.vg_ms_claims_incidents.application.services.IncidentService {
    
    private static final Logger log = LoggerFactory.getLogger(IncidentServiceImpl.class);

    private final IncidentRepository incidentRepository;

    public IncidentServiceImpl(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    @Override
    public Flux<IncidentDTO> findAll() {
        log.info("Buscando todos los incidentes");
        return incidentRepository.findAll()
                .map(this::convertToDTO)
                .doOnComplete(() -> log.info("Búsqueda de incidentes completada"));
    }

    @Override
    public Mono<IncidentDTO> findById(String id) {
        log.info("Buscando incidente con ID: {}", id);
        return incidentRepository.findById(id)
                .map(this::convertToDTO)
                .doOnSuccess(incident -> {
                    if (incident != null) {
                        log.info("Incidente encontrado: {}", incident.getIncidentCode());
                    } else {
                        log.info("Incidente con ID {} no encontrado", id);
                    }
                })
                .doOnError(error -> log.error("Error al buscar incidente con ID {}: {}", id, error.getMessage()));
    }

    @Override
    public Mono<IncidentDTO> save(IncidentDTO incidentDTO) {
        log.info("Guardando nuevo incidente: {}", incidentDTO.getIncidentCode());

        try {
            // Establecer valores por defecto inteligentes
            setDefaultValues(incidentDTO);

            // Validar campos requeridos
            validateRequiredFields(incidentDTO);

            Incident incident = convertToEntity(incidentDTO);
            log.debug("Incidente convertido a entidad: {}", incident.getIncidentCode());

            return incidentRepository.save(incident)
                    .map(this::convertToDTO)
                    .doOnSuccess(saved -> log.info("Incidente guardado exitosamente: {}", saved.getIncidentCode()))
                    .doOnError(error -> log.error("Error al guardar incidente: {}", error.getMessage(), error));
        } catch (Exception e) {
            log.error("Error en el método save: {}", e.getMessage(), e);
            return Mono.error(e);
        }
    }

    @Override
    public Mono<IncidentDTO> update(String id, IncidentDTO incidentDTO) {
        log.info("Actualizando incidente con ID: {}", id);
        return incidentRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Incidente con ID " + id + " no encontrado")))
                .flatMap(existingIncident -> {
                    log.debug("Incidente existente encontrado: {}", existingIncident.getIncidentCode());

                    // Preservar campos que no deben cambiar
                    String originalResolvedByUserId = existingIncident.getResolvedByUserId();

                    BeanUtils.copyProperties(incidentDTO, existingIncident, "id", "resolvedByUserId");

                    // Solo actualizar resolvedByUserId si se está resolviendo
                    if (Boolean.TRUE.equals(incidentDTO.getResolved())
                            && !Boolean.TRUE.equals(existingIncident.getResolved())) {
                        existingIncident.setResolvedByUserId(incidentDTO.getResolvedByUserId());
                    }

                    return incidentRepository.save(existingIncident);
                })
                .map(this::convertToDTO)
                .doOnSuccess(updated -> log.info("Incidente actualizado exitosamente: {}", updated.getIncidentCode()))
                .doOnError(error -> log.error("Error al actualizar incidente con ID {}: {}", id, error.getMessage()));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        log.info("Eliminando incidente con ID: {}", id);
        return incidentRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Incidente con ID " + id + " no encontrado")))
                .flatMap(incident -> {
                    log.debug("Marcando incidente como INACTIVE: {}", incident.getIncidentCode());
                    incident.setRecordStatus("INACTIVE");
                    return incidentRepository.save(incident);
                })
                .then()
                .doOnSuccess(v -> log.info("Incidente eliminado exitosamente: {}", id))
                .doOnError(error -> log.error("Error al eliminar incidente con ID {}: {}", id, error.getMessage()));
    }

    @Override
    public Mono<IncidentDTO> restoreById(String id) {
        log.info("Restaurando incidente con ID: {}", id);
        return incidentRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Incidente con ID " + id + " no encontrado")))
                .flatMap(incident -> {
                    log.debug("Marcando incidente como ACTIVE: {}", incident.getIncidentCode());
                    incident.setRecordStatus("ACTIVE");
                    return incidentRepository.save(incident);
                })
                .map(this::convertToDTO)
                .doOnSuccess(restored -> log.info("Incidente restaurado exitosamente: {}", restored.getIncidentCode()))
                .doOnError(error -> log.error("Error al restaurar incidente con ID {}: {}", id, error.getMessage()));
    }

    @Override
    public Flux<IncidentDTO> findByOrganizationId(String organizationId) {
        log.info("Buscando incidentes por organización: {}", organizationId);
        return incidentRepository.findByOrganizationId(organizationId)
                .map(this::convertToDTO)
                .doOnComplete(() -> log.info("Búsqueda por organización completada"));
    }

    @Override
    public Flux<IncidentDTO> findByIncidentTypeId(String incidentTypeId) {
        log.info("Buscando incidentes por tipoId: {}", incidentTypeId);
        return incidentRepository.findByIncidentTypeId(incidentTypeId)
                .map(this::convertToDTO)
                .doOnComplete(() -> log.info("Búsqueda por tipoId completada"));
    }

    @Override
    public Flux<IncidentDTO> findBySeverity(String severity) {
        log.info("Buscando incidentes por severidad: {}", severity);
        return incidentRepository.findBySeverity(severity)
                .map(this::convertToDTO)
                .doOnComplete(() -> log.info("Búsqueda por severidad completada"));
    }

    @Override
    public Flux<IncidentDTO> findByResolvedStatus(Boolean resolved) {
        log.info("Buscando incidentes por estado de resolución: {}", resolved);
        return incidentRepository.findByResolved(resolved)
                .map(this::convertToDTO)
                .doOnComplete(() -> log.info("Búsqueda por estado de resolución completada"));
    }

    @Override
    public Flux<IncidentDTO> findByStatus(String status) {
        log.info("Buscando incidentes por estado: {}", status);
        return incidentRepository.findByStatus(status)
                .map(this::convertToDTO)
                .doOnComplete(() -> log.info("Búsqueda por estado completada"));
    }

    @Override
    public Flux<IncidentDTO> findByRecordStatus(String recordStatus) {
        log.info("Buscando incidentes por recordStatus: {}", recordStatus);
        return incidentRepository.findByRecordStatus(recordStatus)
                .map(this::convertToDTO)
                .doOnComplete(() -> log.info("Búsqueda por recordStatus completada"));
    }

    @Override
    public Flux<IncidentDTO> findByIncidentCategory(String incidentCategory) {
        log.info("Buscando incidentes por categoría: {}", incidentCategory);
        return incidentRepository.findByIncidentCategory(incidentCategory)
                .map(this::convertToDTO)
                .doOnComplete(() -> log.info("Búsqueda por categoría completada"));
    }

    @Override
    public Flux<IncidentDTO> findByZoneId(String zoneId) {
        log.info("Buscando incidentes por zona: {}", zoneId);
        return incidentRepository.findByZoneId(zoneId)
                .map(this::convertToDTO)
                .doOnComplete(() -> log.info("Búsqueda por zona completada"));
    }

    @Override
    public Flux<IncidentDTO> findByAssignedToUserId(String assignedToUserId) {
        log.info("Buscando incidentes asignados al usuario: {}", assignedToUserId);
        return incidentRepository.findByAssignedToUserId(assignedToUserId)
                .map(this::convertToDTO)
                .doOnComplete(() -> log.info("Búsqueda por usuario asignado completada"));
    }

    @Override
    public Flux<IncidentDTO> findByResolvedByUserId(String resolvedByUserId) {
        log.info("Buscando incidentes resueltos por usuario: {}", resolvedByUserId);
        return incidentRepository.findByResolvedByUserId(resolvedByUserId)
                .map(this::convertToDTO)
                .doOnComplete(() -> log.info("Búsqueda por usuario que resolvió completada"));
    }

    @Override
    public Mono<IncidentDTO> resolveIncident(String id, IncidentDTO incidentDTO) {
        log.info("Resolviendo incidente con ID: {}", id);
        return incidentRepository.findById(id)
                .flatMap(existingIncident -> {
                    log.debug("Incidente encontrado para resolver: {}", existingIncident.getIncidentCode());
                    existingIncident.setResolved(true);
                    existingIncident.setStatus("RESOLVED");
                    existingIncident.setResolvedByUserId(incidentDTO.getResolvedByUserId());

                    if (incidentDTO.getResolutionNotes() != null) {
                        existingIncident.setResolutionNotes(incidentDTO.getResolutionNotes());
                    }
                    if (incidentDTO.getDescription() != null) {
                        existingIncident.setDescription(incidentDTO.getDescription());
                    }

                    return incidentRepository.save(existingIncident);
                })
                .map(this::convertToDTO)
                .doOnSuccess(resolved -> log.info("Incidente resuelto exitosamente: {}", resolved.getIncidentCode()))
                .doOnError(error -> log.error("Error al resolver incidente con ID {}: {}", id, error.getMessage()));
    }

    @Override
    public Mono<IncidentDTO> completeIncident(String id, IncidentDTO incidentDTO) {
        log.info("Completando incidente con ID: {}", id);
        return incidentRepository.findById(id)
                .flatMap(existingIncident -> {
                    log.debug("Incidente encontrado para completar: {}", existingIncident.getIncidentCode());

                    // Completar campos faltantes
                    if (existingIncident.getIncidentCode() == null && incidentDTO.getIncidentCode() != null) {
                        existingIncident.setIncidentCode(incidentDTO.getIncidentCode());
                        log.debug("Completando incidentCode: {}", incidentDTO.getIncidentCode());
                    }

                    if (existingIncident.getOrganizationId() == null && incidentDTO.getOrganizationId() != null) {
                        existingIncident.setOrganizationId(incidentDTO.getOrganizationId());
                        log.debug("Completando organizationId: {}", incidentDTO.getOrganizationId());
                    }

                    if (existingIncident.getIncidentTypeId() == null && incidentDTO.getIncidentTypeId() != null) {
                        existingIncident.setIncidentTypeId(incidentDTO.getIncidentTypeId());
                        log.debug("Completando incidentTypeId: {}", incidentDTO.getIncidentTypeId());
                    }

                    if (existingIncident.getIncidentCategory() == null && incidentDTO.getIncidentCategory() != null) {
                        existingIncident.setIncidentCategory(incidentDTO.getIncidentCategory());
                        log.debug("Completando incidentCategory: {}", incidentDTO.getIncidentCategory());
                    }

                    if (existingIncident.getTitle() == null && incidentDTO.getTitle() != null) {
                        existingIncident.setTitle(incidentDTO.getTitle());
                        log.debug("Completando title: {}", incidentDTO.getTitle());
                    }

                    if (existingIncident.getAffectedBoxesCount() == null
                            && incidentDTO.getAffectedBoxesCount() != null) {
                        existingIncident.setAffectedBoxesCount(incidentDTO.getAffectedBoxesCount());
                        log.debug("Completando affectedBoxesCount: {}", incidentDTO.getAffectedBoxesCount());
                    }

                    if (existingIncident.getAssignedToUserId() == null && incidentDTO.getAssignedToUserId() != null) {
                        existingIncident.setAssignedToUserId(incidentDTO.getAssignedToUserId());
                        log.debug("Completando assignedToUserId: {}", incidentDTO.getAssignedToUserId());
                    }

                    if (existingIncident.getStatus() == null) {
                        existingIncident.setStatus("REPORTED");
                        log.debug("Completando status: REPORTED");
                    }

                    if (existingIncident.getRecordStatus() == null) {
                        existingIncident.setRecordStatus("ACTIVE");
                        log.debug("Completando recordStatus: ACTIVE");
                    }

                    if (existingIncident.getResolved() == null) {
                        existingIncident.setResolved(false);
                        log.debug("Completando resolved: false");
                    }

                    // Si está resuelto pero no tiene resolvedByUserId
                    if (Boolean.TRUE.equals(existingIncident.getResolved())
                            && existingIncident.getResolvedByUserId() == null) {
                        existingIncident.setResolvedByUserId(existingIncident.getReportedByUserId());
                        log.debug("Completando resolvedByUserId con reportedByUserId");
                    }

                    return incidentRepository.save(existingIncident);
                })
                .map(this::convertToDTO)
                .doOnSuccess(
                        completed -> log.info("Incidente completado exitosamente: {}", completed.getIncidentCode()))
                .doOnError(error -> log.error("Error al completar incidente con ID {}: {}", id, error.getMessage()));
    }

    private void setDefaultValues(IncidentDTO incidentDTO) {
        // Valores por defecto básicos
        if (incidentDTO.getResolved() == null) {
            incidentDTO.setResolved(false);
            log.debug("Estableciendo resolved = false por defecto");
        }
        if (incidentDTO.getStatus() == null) {
            incidentDTO.setStatus("REPORTED");
            log.debug("Estableciendo status = REPORTED por defecto");
        }
        if (incidentDTO.getRecordStatus() == null) {
            incidentDTO.setRecordStatus("ACTIVE");
            log.debug("Estableciendo recordStatus = ACTIVE por defecto");
        }

        // Si está resuelto pero no tiene resolvedByUserId, usar reportedByUserId
        if (Boolean.TRUE.equals(incidentDTO.getResolved()) && incidentDTO.getResolvedByUserId() == null) {
            incidentDTO.setResolvedByUserId(incidentDTO.getReportedByUserId());
            log.debug("Estableciendo resolvedByUserId = reportedByUserId para incidente resuelto");
        }
    }

    private void validateRequiredFields(IncidentDTO incidentDTO) {
        if (incidentDTO.getIncidentCode() == null || incidentDTO.getIncidentCode().trim().isEmpty()) {
            throw new IllegalArgumentException("El código del incidente es obligatorio");
        }
        if (incidentDTO.getOrganizationId() == null || incidentDTO.getOrganizationId().trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de la organización es obligatorio");
        }
        if (incidentDTO.getIncidentTypeId() == null || incidentDTO.getIncidentTypeId().trim().isEmpty()) {
            throw new IllegalArgumentException("El tipoId de incidente es obligatorio");
        }
        if (incidentDTO.getIncidentCategory() == null || incidentDTO.getIncidentCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("La categoría del incidente es obligatoria");
        }
        if (incidentDTO.getTitle() == null || incidentDTO.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("El título del incidente es obligatorio");
        }
        if (incidentDTO.getDescription() == null || incidentDTO.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción es obligatoria");
        }
        if (incidentDTO.getReportedByUserId() == null || incidentDTO.getReportedByUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del usuario que reportó es obligatorio");
        }
    }

    private IncidentDTO convertToDTO(Incident incident) {
        try {
            if (incident == null) {
                log.error("Error: Incident es null en convertToDTO");
                throw new IllegalArgumentException("El incidente no puede ser null");
            }

            IncidentDTO incidentDTO = new IncidentDTO();
            BeanUtils.copyProperties(incident, incidentDTO);
            return incidentDTO;
        } catch (Exception e) {
            log.error("Error al convertir Incident a DTO: {}", e.getMessage(), e);
            throw e;
        }
    }

    private Incident convertToEntity(IncidentDTO incidentDTO) {
        try {
            if (incidentDTO == null) {
                log.error("Error: IncidentDTO es null en convertToEntity");
                throw new IllegalArgumentException("El IncidentDTO no puede ser null");
            }

            Incident incident = new Incident();
            BeanUtils.copyProperties(incidentDTO, incident);
            return incident;
        } catch (Exception e) {
            log.error("Error al convertir DTO a Incident: {}", e.getMessage(), e);
            throw e;
        }
    }
}