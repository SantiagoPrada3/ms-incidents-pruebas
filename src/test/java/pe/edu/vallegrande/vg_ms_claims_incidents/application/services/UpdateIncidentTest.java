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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UpdateIncidentTest {

    @Mock
    private IncidentRepository incidentRepository;

    @InjectMocks
    private IncidentServiceImpl incidentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateIncident_Success() {
        // Given
        String incidentId = "1";
        IncidentDTO incidentDTO = new IncidentDTO();
        incidentDTO.setTitle("Updated Incident Title");
        incidentDTO.setDescription("Updated Description");

        Incident existingIncident = new Incident();
        existingIncident.setId(incidentId);
        existingIncident.setIncidentCode("INC-001");
        existingIncident.setTitle("Original Title");
        existingIncident.setDescription("Original Description");

        Incident updatedIncident = new Incident();
        updatedIncident.setId(incidentId);
        updatedIncident.setIncidentCode("INC-001");
        updatedIncident.setTitle("Updated Incident Title");
        updatedIncident.setDescription("Updated Description");

        when(incidentRepository.findById(incidentId)).thenReturn(Mono.just(existingIncident));
        when(incidentRepository.save(any(Incident.class))).thenReturn(Mono.just(updatedIncident));

        // When
        Mono<IncidentDTO> result = incidentService.update(incidentId, incidentDTO);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(dto -> {
                    boolean matches = dto.getTitle().equals("Updated Incident Title") && 
                                     dto.getDescription().equals("Updated Description");
                    if (matches) {
                        System.out.println("✅ Verificación exitosa: El incidente fue actualizado correctamente");
                    } else {
                        System.out.println("❌ Error: El incidente no fue actualizado correctamente");
                    }
                    return matches;
                })
                .expectComplete()
                .verify();

        verify(incidentRepository, times(1)).findById(incidentId);
        verify(incidentRepository, times(1)).save(any(Incident.class));
        System.out.println("✅ Verificación exitosa: El método findById y save del repositorio fueron llamados correctamente");
    }

    @Test
    void testUpdateIncident_NotFound() {
        // Given
        String incidentId = "non-existent-id";
        IncidentDTO incidentDTO = new IncidentDTO();
        incidentDTO.setTitle("Updated Title");

        when(incidentRepository.findById(incidentId)).thenReturn(Mono.empty());

        // When
        Mono<IncidentDTO> result = incidentService.update(incidentId, incidentDTO);

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(incidentRepository, times(1)).findById(incidentId);
        verify(incidentRepository, never()).save(any(Incident.class));
        System.out.println("✅ Verificación exitosa: Se lanzó una excepción cuando el incidente no existe");
    }

    @Test
    void testUpdateIncident_ResolveIncident() {
        // Given
        String incidentId = "1";
        IncidentDTO incidentDTO = new IncidentDTO();
        incidentDTO.setResolved(true);
        incidentDTO.setResolvedByUserId("resolver-user-id");

        Incident existingIncident = new Incident();
        existingIncident.setId(incidentId);
        existingIncident.setIncidentCode("INC-001");
        existingIncident.setResolved(false);

        Incident updatedIncident = new Incident();
        updatedIncident.setId(incidentId);
        updatedIncident.setIncidentCode("INC-001");
        updatedIncident.setResolved(true);
        updatedIncident.setResolvedByUserId("resolver-user-id");

        when(incidentRepository.findById(incidentId)).thenReturn(Mono.just(existingIncident));
        when(incidentRepository.save(any(Incident.class))).thenReturn(Mono.just(updatedIncident));

        // When
        Mono<IncidentDTO> result = incidentService.update(incidentId, incidentDTO);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(dto -> {
                    boolean matches = Boolean.TRUE.equals(dto.getResolved()) && 
                                     "resolver-user-id".equals(dto.getResolvedByUserId());
                    if (matches) {
                        System.out.println("✅ Verificación exitosa: El incidente fue resuelto correctamente");
                    } else {
                        System.out.println("❌ Error: El incidente no fue resuelto correctamente");
                    }
                    return matches;
                })
                .expectComplete()
                .verify();

        verify(incidentRepository, times(1)).findById(incidentId);
        verify(incidentRepository, times(1)).save(any(Incident.class));
        System.out.println("✅ Verificación exitosa: El incidente resuelto fue guardado correctamente");
    }
}