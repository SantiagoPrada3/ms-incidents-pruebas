package pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.service;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vg_ms_claims_incidents.domain.models.ComplaintCategory;
import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.dto.ComplaintCategoryDTO;
import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.repository.ComplaintCategoryRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ComplaintCategoryService implements pe.edu.vallegrande.vg_ms_claims_incidents.application.services.ComplaintCategoryService {

    private final ComplaintCategoryRepository complaintCategoryRepository;

    public ComplaintCategoryService(ComplaintCategoryRepository complaintCategoryRepository) {
        this.complaintCategoryRepository = complaintCategoryRepository;
    }

    @Override
    public Flux<ComplaintCategoryDTO> findAll() {
        return complaintCategoryRepository.findAll()
                .map(this::convertToDTO);
    }

    @Override
    public Mono<ComplaintCategoryDTO> findById(String id) {
        return complaintCategoryRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    public Mono<ComplaintCategoryDTO> save(ComplaintCategoryDTO complaintCategoryDTO) {
        if (complaintCategoryDTO.getStatus() == null || complaintCategoryDTO.getStatus().isEmpty()) {
            complaintCategoryDTO.setStatus("ACTIVE");
        }
        ComplaintCategory complaintCategory = convertToEntity(complaintCategoryDTO);
        return complaintCategoryRepository.save(complaintCategory)
                .map(this::convertToDTO);
    }

    @Override
    public Mono<ComplaintCategoryDTO> update(String id, ComplaintCategoryDTO complaintCategoryDTO) {
        return complaintCategoryRepository.findById(id)
                .flatMap(existingCategory -> {
                    BeanUtils.copyProperties(complaintCategoryDTO, existingCategory, "id", "createdAt");
                    return complaintCategoryRepository.save(existingCategory);
                })
                .map(this::convertToDTO);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return complaintCategoryRepository.findById(id)
                .flatMap(category -> {
                    category.setStatus("INACTIVE");
                    return complaintCategoryRepository.save(category);
                })
                .then();
    }

    @Override
    public Mono<ComplaintCategoryDTO> restoreById(String id) {
        return complaintCategoryRepository.findById(id)
                .flatMap(category -> {
                    category.setStatus("ACTIVE");
                    return complaintCategoryRepository.save(category);
                })
                .map(this::convertToDTO);
    }

    private ComplaintCategoryDTO convertToDTO(ComplaintCategory complaintCategory) {
        ComplaintCategoryDTO complaintCategoryDTO = new ComplaintCategoryDTO();
        BeanUtils.copyProperties(complaintCategory, complaintCategoryDTO);
        return complaintCategoryDTO;
    }

    private ComplaintCategory convertToEntity(ComplaintCategoryDTO complaintCategoryDTO) {
        ComplaintCategory complaintCategory = new ComplaintCategory();
        BeanUtils.copyProperties(complaintCategoryDTO, complaintCategory);
        return complaintCategory;
    }
}