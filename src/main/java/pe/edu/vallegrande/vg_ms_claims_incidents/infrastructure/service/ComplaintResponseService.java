package pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.service;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vg_ms_claims_incidents.domain.models.ComplaintResponse;
import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.dto.ComplaintResponseDTO;
import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.repository.ComplaintResponseRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ComplaintResponseService implements pe.edu.vallegrande.vg_ms_claims_incidents.application.services.ComplaintResponseService {

    private final ComplaintResponseRepository complaintResponseRepository;

    public ComplaintResponseService(ComplaintResponseRepository complaintResponseRepository) {
        this.complaintResponseRepository = complaintResponseRepository;
    }

    @Override
    public Flux<ComplaintResponseDTO> findAll() {
        return complaintResponseRepository.findAll()
                .map(this::convertToDTO);
    }

    @Override
    public Mono<ComplaintResponseDTO> findById(String id) {
        return complaintResponseRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    public Mono<ComplaintResponseDTO> save(ComplaintResponseDTO complaintResponseDTO) {
        ComplaintResponse complaintResponse = convertToEntity(complaintResponseDTO);
        return complaintResponseRepository.save(complaintResponse)
                .map(this::convertToDTO);
    }

    @Override
    public Mono<ComplaintResponseDTO> update(String id, ComplaintResponseDTO complaintResponseDTO) {
        return complaintResponseRepository.findById(id)
                .flatMap(existingResponse -> {
                    BeanUtils.copyProperties(complaintResponseDTO, existingResponse, "id", "createdAt");
                    return complaintResponseRepository.save(existingResponse);
                })
                .map(this::convertToDTO);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return complaintResponseRepository.deleteById(id);
    }

    private ComplaintResponseDTO convertToDTO(ComplaintResponse complaintResponse) {
        ComplaintResponseDTO complaintResponseDTO = new ComplaintResponseDTO();
        BeanUtils.copyProperties(complaintResponse, complaintResponseDTO);
        return complaintResponseDTO;
    }

    private ComplaintResponse convertToEntity(ComplaintResponseDTO complaintResponseDTO) {
        ComplaintResponse complaintResponse = new ComplaintResponse();
        BeanUtils.copyProperties(complaintResponseDTO, complaintResponse);
        return complaintResponse;
    }
}