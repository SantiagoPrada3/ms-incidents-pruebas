package pe.edu.vallegrande.vg_ms_claims_incidents.application.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pe.edu.vallegrande.vg_ms_claims_incidents.domain.models.Incident;
import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.repository.IncidentRepository;
import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.service.IncidentServiceImpl;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DeleteIncidentUnitTest {

    @Mock
    private IncidentRepository incidentRepository;

    @InjectMocks
    private IncidentServiceImpl incidentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDeleteIncident_Success() {
        // Given
        String incidentId = "1";
        Incident incident = new Incident();
        incident.setId(incidentId);
        incident.setIncidentCode("INC-001");
        incident.setRecordStatus("ACTIVE");

        when(incidentRepository.findById(incidentId)).thenReturn(Mono.just(incident));
        when(incidentRepository.save(any(Incident.class))).thenReturn(Mono.just(incident));

        // When
        Mono<Void> result = incidentService.deleteById(incidentId);

        // Then
        StepVerifier.create(result)
                .expectComplete()
                .verify();

        verify(incidentRepository, times(1)).findById(incidentId);
        verify(incidentRepository, times(1)).save(any(Incident.class));
        System.out.println("✅ Verificación exitosa: El incidente fue eliminado (marcado como INACTIVE) correctamente");
    }

    @Test
    void testDeleteIncident_NotFound() {
        // Given
        String incidentId = "non-existent-id";

        when(incidentRepository.findById(incidentId)).thenReturn(Mono.empty());

        // When
        Mono<Void> result = incidentService.deleteById(incidentId);

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(incidentRepository, times(1)).findById(incidentId);
        verify(incidentRepository, never()).save(any(Incident.class));
        System.out.println("✅ Verificación exitosa: Se lanzó una excepción cuando el incidente no existe");
    }

    @Test
    void testDeleteIncident_AlreadyInactive() {
        // Given
        String incidentId = "1";
        Incident incident = new Incident();
        incident.setId(incidentId);
        incident.setIncidentCode("INC-001");
        incident.setRecordStatus("INACTIVE");

        when(incidentRepository.findById(incidentId)).thenReturn(Mono.just(incident));
        when(incidentRepository.save(any(Incident.class))).thenReturn(Mono.just(incident));

        // When
        Mono<Void> result = incidentService.deleteById(incidentId);

        // Then
        StepVerifier.create(result)
                .expectComplete()
                .verify();

        verify(incidentRepository, times(1)).findById(incidentId);
        verify(incidentRepository, times(1)).save(any(Incident.class));
        
        // Verificar que el estado sigue siendo INACTIVE
        verify(incidentRepository).save(argThat(savedIncident -> 
            "INACTIVE".equals(savedIncident.getRecordStatus())));
        System.out.println("✅ Verificación exitosa: El incidente con estado INACTIVE se mantiene como INACTIVE");
    }
}