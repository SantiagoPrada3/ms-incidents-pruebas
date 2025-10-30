package pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.repository;

import pe.edu.vallegrande.vg_ms_claims_incidents.domain.models.Incident;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface IncidentRepository extends ReactiveMongoRepository<Incident, String> {
    Flux<Incident> findByOrganizationId(String organizationId);

    Flux<Incident> findByIncidentTypeId(String incidentTypeId);

    Flux<Incident> findBySeverity(String severity);

    Flux<Incident> findByResolved(Boolean resolved);

    Flux<Incident> findByStatus(String status);

    Flux<Incident> findByRecordStatus(String recordStatus);

    Flux<Incident> findByIncidentCategory(String incidentCategory);

    Flux<Incident> findByZoneId(String zoneId);

    Flux<Incident> findByAssignedToUserId(String assignedToUserId);

    Flux<Incident> findByResolvedByUserId(String resolvedByUserId);
}