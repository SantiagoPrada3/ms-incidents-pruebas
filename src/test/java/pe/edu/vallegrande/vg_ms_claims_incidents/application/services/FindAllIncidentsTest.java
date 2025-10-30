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

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

class FindAllIncidentsTest {

    @Mock
    private IncidentRepository incidentRepository;

    @InjectMocks
    private IncidentServiceImpl incidentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAllIncidents_Success() {
        // Given
        Incident incident1 = new Incident();
        incident1.setId("1");
        incident1.setIncidentCode("INC-001");

        Incident incident2 = new Incident();
        incident2.setId("2");
        incident2.setIncidentCode("INC-002");

        List<Incident> incidents = Arrays.asList(incident1, incident2);

        when(incidentRepository.findAll()).thenReturn(Flux.fromIterable(incidents));

        // When
        Flux<IncidentDTO> result = incidentService.findAll();

        // Then
        StepVerifier.create(result)
                .expectNextMatches(dto -> {
                    boolean matches = dto.getIncidentCode().equals("INC-001");
                    if (matches) {
                        System.out.println("✅ Verificación exitosa: Primer incidente encontrado con código INC-001");
                    } else {
                        System.out.println("❌ Error: El primer incidente no tiene el código esperado. Esperado: INC-001, Actual: " + dto.getIncidentCode());
                    }
                    return matches;
                })
                .expectNextMatches(dto -> {
                    boolean matches = dto.getIncidentCode().equals("INC-002");
                    if (matches) {
                        System.out.println("✅ Verificación exitosa: Segundo incidente encontrado con código INC-002");
                    } else {
                        System.out.println("❌ Error: El segundo incidente no tiene el código esperado. Esperado: INC-002, Actual: " + dto.getIncidentCode());
                    }
                    return matches;
                })
                .expectComplete()
                .verify();
        System.out.println("✅ Verificación exitosa: La lista de incidentes se completó correctamente");

        verify(incidentRepository, times(1)).findAll();
        System.out.println("✅ Verificación exitosa: El método findAll del repositorio fue llamado correctamente");
    }
}