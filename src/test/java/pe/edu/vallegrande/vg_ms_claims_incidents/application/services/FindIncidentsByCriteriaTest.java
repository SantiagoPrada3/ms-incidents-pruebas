package pe.edu.vallegrande.vg_ms_claims_incidents.application.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pe.edu.vallegrande.vg_ms_claims_incidents.domain.models.Incident;
import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.dto.IncidentDTO;
import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.repository.IncidentRepository;
import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.service.IncidentServiceImpl;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class FindIncidentsByCriteriaTest {

    @Mock
    private IncidentRepository incidentRepository;

    @InjectMocks
    private IncidentServiceImpl incidentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByOrganizationId_Success() {
        // Given
        String organizationId = "ORG-001";
        Incident incident1 = new Incident();
        incident1.setId("1");
        incident1.setIncidentCode("INC-001");
        incident1.setOrganizationId(organizationId);
        
        Incident incident2 = new Incident();
        incident2.setId("2");
        incident2.setIncidentCode("INC-002");
        incident2.setOrganizationId(organizationId);

        when(incidentRepository.findByOrganizationId(organizationId))
                .thenReturn(Flux.just(incident1, incident2));

        // When
        Flux<IncidentDTO> result = incidentService.findByOrganizationId(organizationId);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(dto -> dto.getOrganizationId().equals(organizationId))
                .expectNextMatches(dto -> dto.getOrganizationId().equals(organizationId))
                .expectComplete()
                .verify();

        verify(incidentRepository, times(1)).findByOrganizationId(organizationId);
        System.out.println("✅ Verificación exitosa: Búsqueda por organización funciona correctamente");
    }

    @Test
    void testFindByIncidentTypeId_Success() {
        // Given
        String incidentTypeId = "TYPE-001";
        Incident incident = new Incident();
        incident.setId("1");
        incident.setIncidentCode("INC-001");
        incident.setIncidentTypeId(incidentTypeId);

        when(incidentRepository.findByIncidentTypeId(incidentTypeId))
                .thenReturn(Flux.just(incident));

        // When
        Flux<IncidentDTO> result = incidentService.findByIncidentTypeId(incidentTypeId);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(dto -> dto.getIncidentTypeId().equals(incidentTypeId))
                .expectComplete()
                .verify();

        verify(incidentRepository, times(1)).findByIncidentTypeId(incidentTypeId);
        System.out.println("✅ Verificación exitosa: Búsqueda por tipo de incidente funciona correctamente");
    }

    @Test
    void testFindByResolvedStatus_Success() {
        // Given
        Boolean resolved = true;
        Incident incident = new Incident();
        incident.setId("1");
        incident.setIncidentCode("INC-001");
        incident.setResolved(resolved);

        when(incidentRepository.findByResolved(resolved))
                .thenReturn(Flux.just(incident));

        // When
        Flux<IncidentDTO> result = incidentService.findByResolvedStatus(resolved);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(dto -> resolved.equals(dto.getResolved()))
                .expectComplete()
                .verify();

        verify(incidentRepository, times(1)).findByResolved(resolved);
        System.out.println("✅ Verificación exitosa: Búsqueda por estado de resolución funciona correctamente");
    }
}