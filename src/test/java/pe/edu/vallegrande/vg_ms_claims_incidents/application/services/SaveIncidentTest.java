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

class SaveIncidentTest {

    @Mock
    private IncidentRepository incidentRepository;

    @InjectMocks
    private IncidentServiceImpl incidentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveIncident_Success() {
        // Given
        IncidentDTO incidentDTO = new IncidentDTO();
        incidentDTO.setIncidentCode("INC-001");
        incidentDTO.setOrganizationId("ORG-001");
        incidentDTO.setIncidentTypeId("TYPE-001");
        incidentDTO.setIncidentCategory("CATEGORY-001");
        incidentDTO.setTitle("Test Incident");
        incidentDTO.setDescription("Test Description");
        incidentDTO.setReportedByUserId("USER-001");

        Incident incident = new Incident();
        incident.setId("1");
        incident.setIncidentCode("INC-001");
        incident.setOrganizationId("ORG-001");
        incident.setIncidentTypeId("TYPE-001");
        incident.setIncidentCategory("CATEGORY-001");
        incident.setTitle("Test Incident");
        incident.setDescription("Test Description");
        incident.setReportedByUserId("USER-001");

        when(incidentRepository.save(any(Incident.class))).thenReturn(Mono.just(incident));

        // When
        Mono<IncidentDTO> result = incidentService.save(incidentDTO);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(dto -> {
                    // Verificación exitosa: El código del incidente coincide con el esperado
                    boolean matches = dto.getIncidentCode().equals("INC-001");
                    if (!matches) {
                        System.out.println("Error: El código del incidente no coincide. Esperado: INC-001, Actual: " + dto.getIncidentCode());
                    } else {
                        System.out.println("✅ Verificación exitosa: El código del incidente coincide con el esperado (INC-001)");
                    }
                    return matches;
                })
                .expectComplete()
                .verify();

        // Verificación exitosa: El método save del repositorio fue llamado exactamente una vez
        verify(incidentRepository, times(1)).save(any(Incident.class));
        System.out.println("✅ Verificación exitosa: El incidente fue guardado correctamente en el repositorio");
    }
}