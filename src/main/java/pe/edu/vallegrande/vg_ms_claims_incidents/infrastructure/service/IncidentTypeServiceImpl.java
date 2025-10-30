package pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.service;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vg_ms_claims_incidents.application.services.IncidentTypeService;
import pe.edu.vallegrande.vg_ms_claims_incidents.domain.models.IncidentType;
import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.dto.IncidentTypeDTO;
import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.repository.IncidentTypeRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class IncidentTypeServiceImpl
        implements IncidentTypeService {

    private final IncidentTypeRepository incidentTypeRepository;

    public IncidentTypeServiceImpl(IncidentTypeRepository incidentTypeRepository) {
        this.incidentTypeRepository = incidentTypeRepository;
    }

    @Override
    public Flux<IncidentTypeDTO> findAll() {
        return incidentTypeRepository.findAll()
                .map(this::convertToDTO);
    }

    @Override
    public Mono<IncidentTypeDTO> findById(String id) {
        return incidentTypeRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    public Mono<IncidentTypeDTO> save(IncidentTypeDTO incidentTypeDTO) {
        if (incidentTypeDTO.getStatus() == null || incidentTypeDTO.getStatus().isEmpty()) {
            incidentTypeDTO.setStatus("ACTIVE");
        }
        IncidentType incidentType = convertToEntity(incidentTypeDTO);
        return incidentTypeRepository.save(incidentType)
                .map(this::convertToDTO);
    }

    @Override
    public Mono<IncidentTypeDTO> update(String id, IncidentTypeDTO incidentTypeDTO) {
        return incidentTypeRepository.findById(id)
                .flatMap(existingType -> {
                    BeanUtils.copyProperties(incidentTypeDTO, existingType, "id", "createdAt");
                    return incidentTypeRepository.save(existingType);
                })
                .map(this::convertToDTO);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return incidentTypeRepository.findById(id)
                .flatMap(incidentType -> {
                    incidentType.setStatus("INACTIVE");
                    return incidentTypeRepository.save(incidentType);
                })
                .then();
    }

    @Override
    public Mono<IncidentTypeDTO> restoreById(String id) {
        return incidentTypeRepository.findById(id)
                .flatMap(incidentType -> {
                    incidentType.setStatus("ACTIVE");
                    return incidentTypeRepository.save(incidentType);
                })
                .map(this::convertToDTO);
    }

    private IncidentTypeDTO convertToDTO(IncidentType incidentType) {
        IncidentTypeDTO incidentTypeDTO = new IncidentTypeDTO();
        BeanUtils.copyProperties(incidentType, incidentTypeDTO);
        return incidentTypeDTO;
    }

    private IncidentType convertToEntity(IncidentTypeDTO incidentTypeDTO) {
        IncidentType incidentType = new IncidentType();
        BeanUtils.copyProperties(incidentTypeDTO, incidentType);
        return incidentType;
    }
}