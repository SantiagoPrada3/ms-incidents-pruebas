package pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.service;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vg_ms_claims_incidents.domain.models.IncidentResolution;
import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.dto.IncidentResolutionDTO;
import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.dto.MaterialUsedDTO;
import pe.edu.vallegrande.vg_ms_claims_incidents.domain.models.MaterialUsed;
import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.repository.IncidentResolutionRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;
import java.util.Date;

@Service
public class IncidentResolutionService implements pe.edu.vallegrande.vg_ms_claims_incidents.application.services.IncidentResolutionService {

    private final IncidentResolutionRepository incidentResolutionRepository;

    public IncidentResolutionService(IncidentResolutionRepository incidentResolutionRepository) {
        this.incidentResolutionRepository = incidentResolutionRepository;
    }

    @Override
    public Flux<IncidentResolutionDTO> findAll() {
        return incidentResolutionRepository.findAll()
                .map(this::convertToDTO);
    }

    @Override
    public Mono<IncidentResolutionDTO> findById(String id) {
        return incidentResolutionRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    public Mono<IncidentResolutionDTO> save(IncidentResolutionDTO incidentResolutionDTO) {
        IncidentResolution incidentResolution = convertToEntity(incidentResolutionDTO);
        return incidentResolutionRepository.save(incidentResolution)
                .map(this::convertToDTO);
    }

    @Override
    public Mono<IncidentResolutionDTO> update(String id, IncidentResolutionDTO incidentResolutionDTO) {
        return incidentResolutionRepository.findById(id)
                .flatMap(existingResolution -> {
                    BeanUtils.copyProperties(incidentResolutionDTO, existingResolution, "id", "createdAt");
                    if (incidentResolutionDTO.getMaterialsUsed() != null) {
                        existingResolution.setMaterialsUsed(incidentResolutionDTO.getMaterialsUsed().stream()
                                .map(this::convertMaterialUsedDTOToEntity)
                                .collect(Collectors.toList()));
                    }
                    return incidentResolutionRepository.save(existingResolution);
                })
                .map(this::convertToDTO);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return incidentResolutionRepository.deleteById(id);
    }

    @Override
    public Flux<IncidentResolutionDTO> findByIncidentId(String incidentId) {
        return incidentResolutionRepository.findByIncidentId(incidentId)
                .map(this::convertToDTO);
    }

    private IncidentResolutionDTO convertToDTO(IncidentResolution incidentResolution) {
        IncidentResolutionDTO incidentResolutionDTO = new IncidentResolutionDTO();
        BeanUtils.copyProperties(incidentResolution, incidentResolutionDTO);
        if (incidentResolution.getMaterialsUsed() != null) {
            incidentResolutionDTO.setMaterialsUsed(incidentResolution.getMaterialsUsed().stream()
                    .map(this::convertMaterialUsedEntityToDTO)
                    .collect(Collectors.toList()));
        }
        return incidentResolutionDTO;
    }

    private IncidentResolution convertToEntity(IncidentResolutionDTO incidentResolutionDTO) {
        IncidentResolution incidentResolution = new IncidentResolution();
        BeanUtils.copyProperties(incidentResolutionDTO, incidentResolution);
        
        // Configurar createdAt automáticamente si no está presente
        if (incidentResolution.getCreatedAt() == null) {
            incidentResolution.setCreatedAt(new Date());
        }
        
        if (incidentResolutionDTO.getMaterialsUsed() != null) {
            incidentResolution.setMaterialsUsed(incidentResolutionDTO.getMaterialsUsed().stream()
                    .map(this::convertMaterialUsedDTOToEntity)
                    .collect(Collectors.toList()));
        }
        return incidentResolution;
    }

    private MaterialUsedDTO convertMaterialUsedEntityToDTO(MaterialUsed materialUsed) {
        MaterialUsedDTO materialUsedDTO = new MaterialUsedDTO();
        // Mapeo manual para manejar las anotaciones @Field de MongoDB
        materialUsedDTO.setProductId(materialUsed.getProductId());
        materialUsedDTO.setQuantity(materialUsed.getQuantity());
        materialUsedDTO.setUnit(materialUsed.getUnit());
        materialUsedDTO.setUnitCost(materialUsed.getUnitCost());
        return materialUsedDTO;
    }

    private MaterialUsed convertMaterialUsedDTOToEntity(MaterialUsedDTO materialUsedDTO) {
        MaterialUsed materialUsed = new MaterialUsed();
        // Mapeo manual para manejar las anotaciones @Field de MongoDB
        materialUsed.setProductId(materialUsedDTO.getProductId());
        materialUsed.setQuantity(materialUsedDTO.getQuantity());
        materialUsed.setUnit(materialUsedDTO.getUnit());
        materialUsed.setUnitCost(materialUsedDTO.getUnitCost());
        return materialUsed;
    }
}