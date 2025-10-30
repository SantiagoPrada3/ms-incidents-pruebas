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

class DeleteIncidentTest {

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
        System.out.println("✅ Verificación exitosa: La operación de eliminación se completó correctamente");

        // Verificar que se buscó el incidente por su ID
        verify(incidentRepository, times(1)).findById(incidentId);
        System.out.println("✅ Verificación exitosa: El incidente fue encontrado por su ID: " + incidentId);
        
        // Verificar que se guardó el incidente actualizado
        verify(incidentRepository, times(1)).save(any(Incident.class));
        System.out.println("✅ Verificación exitosa: El incidente actualizado fue guardado en el repositorio");
        
        // Verificar que el recordStatus se haya cambiado a INACTIVE
        verify(incidentRepository).save(argThat(i -> {
            boolean statusChanged = "INACTIVE".equals(i.getRecordStatus());
            if (statusChanged) {
                System.out.println("✅ Verificación exitosa: El estado del registro fue cambiado a INACTIVE");
            } else {
                System.out.println("❌ Error: El estado del registro no fue cambiado a INACTIVE. Estado actual: " + i.getRecordStatus());
            }
            return statusChanged;
        }));
    }
}