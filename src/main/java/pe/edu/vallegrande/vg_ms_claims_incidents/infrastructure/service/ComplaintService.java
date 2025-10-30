package pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.service;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vg_ms_claims_incidents.domain.models.Complaint;
import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.dto.ComplaintDTO;
import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.repository.ComplaintRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

import org.bson.types.ObjectId;

@Slf4j
@Service
public class ComplaintService implements pe.edu.vallegrande.vg_ms_claims_incidents.application.services.ComplaintService {

    private final ComplaintRepository complaintRepository;

    public ComplaintService(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    @Override
    public Flux<ComplaintDTO> findAll() {
        log.info("Buscando todas las quejas");
        return complaintRepository.findAll()
                .doOnNext(complaint -> log.info("Queja encontrada: {}", complaint.getId()))
                .map(this::convertToDTO)
                .doOnComplete(() -> log.info("Búsqueda de quejas completada"));
    }

    @Override
    public Mono<ComplaintDTO> findById(String id) {
        return complaintRepository.findById(new ObjectId(id))
                .map(this::convertToDTO);
    }

    @Override
    public Mono<ComplaintDTO> save(ComplaintDTO complaintDTO) {
        // Validate required fields
        if (complaintDTO.getSubject() == null || complaintDTO.getSubject().trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Subject is required"));
        }
        if (complaintDTO.getDescription() == null || complaintDTO.getDescription().trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Description is required"));
        }
        if (complaintDTO.getPriority() == null || complaintDTO.getPriority().trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Priority is required"));
        }
        if (complaintDTO.getOrganizationId() == null || complaintDTO.getOrganizationId().trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Organization ID is required"));
        }
        if (complaintDTO.getUserId() == null || complaintDTO.getUserId().trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("User ID is required"));
        }
        if (complaintDTO.getCategoryId() == null || complaintDTO.getCategoryId().trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Category ID is required"));
        }
        if (complaintDTO.getWaterBoxId() == null || complaintDTO.getWaterBoxId().trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Water Box ID is required"));
        }

        // Set default values
        if (complaintDTO.getStatus() == null || complaintDTO.getStatus().isEmpty()) {
            complaintDTO.setStatus("RECEIVED");
        }

        // Validate status value
        String status = complaintDTO.getStatus().toUpperCase();
        if (!status.matches("RECEIVED|IN_PROGRESS|RESOLVED|CLOSED")) {
            return Mono.error(new IllegalArgumentException(
                    "Invalid status value. Must be one of: RECEIVED, IN_PROGRESS, RESOLVED, CLOSED"));
        }
        complaintDTO.setStatus(status);

        // Validate priority value
        String priority = complaintDTO.getPriority().toUpperCase();
        if (!priority.matches("LOW|MEDIUM|HIGH|CRITICAL")) {
            return Mono.error(new IllegalArgumentException(
                    "Invalid priority value. Must be one of: LOW, MEDIUM, HIGH, CRITICAL"));
        }
        complaintDTO.setPriority(priority);

        // Set timestamps
        Instant now = Instant.now();
        if (complaintDTO.getComplaintDate() == null) {
            complaintDTO.setComplaintDate(now);
        }
        if (complaintDTO.getCreatedAt() == null) {
            complaintDTO.setCreatedAt(now);
        }

        // Convert and save
        Complaint complaint = convertToEntity(complaintDTO);
        return complaintRepository.save(complaint)
                .map(this::convertToDTO)
                .doOnSuccess(savedComplaint -> log.info("Complaint created successfully with code: {}",
                        savedComplaint.getComplaintCode()))
                .doOnError(error -> log.error("Error creating complaint: {}", error.getMessage()));
    }

    @Override
    public Mono<ComplaintDTO> update(String id, ComplaintDTO complaintDTO) {
        return complaintRepository.findById(new ObjectId(id))
                .flatMap(existingComplaint -> {
                    BeanUtils.copyProperties(complaintDTO, existingComplaint, "id", "createdAt");
                    return complaintRepository.save(existingComplaint);
                })
                .map(this::convertToDTO);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return complaintRepository.findById(new ObjectId(id))
                .flatMap(complaint -> {
                    complaint.setStatus("INACTIVE");
                    return complaintRepository.save(complaint);
                })
                .then();
    }

    @Override
    public Mono<ComplaintDTO> restoreById(String id) {
        return complaintRepository.findById(new ObjectId(id))
                .flatMap(complaint -> {
                    complaint.setStatus("ACTIVE");
                    return complaintRepository.save(complaint);
                })
                .map(this::convertToDTO);
    }

    private Complaint convertToEntity(ComplaintDTO complaintDTO) {
        Complaint complaint = new Complaint();

        // Convert string IDs to ObjectId
        if (complaintDTO.getOrganizationId() != null) {
            complaint.setOrganizationId(new ObjectId(complaintDTO.getOrganizationId()));
        }
        if (complaintDTO.getUserId() != null) {
            complaint.setUserId(new ObjectId(complaintDTO.getUserId()));
        }
        if (complaintDTO.getCategoryId() != null) {
            complaint.setCategoryId(new ObjectId(complaintDTO.getCategoryId()));
        }
        if (complaintDTO.getWaterBoxId() != null) {
            complaint.setWaterBoxId(new ObjectId(complaintDTO.getWaterBoxId()));
        }
        if (complaintDTO.getAssignedToUserId() != null) {
            complaint.setAssignedToUserId(new ObjectId(complaintDTO.getAssignedToUserId()));
        }

        // Copy other fields
        complaint.setComplaintCode(complaintDTO.getComplaintCode());
        complaint.setComplaintDate(complaintDTO.getComplaintDate());
        complaint.setSubject(complaintDTO.getSubject());
        complaint.setDescription(complaintDTO.getDescription());
        complaint.setPriority(complaintDTO.getPriority());
        complaint.setStatus(complaintDTO.getStatus());
        complaint.setExpectedResolutionDate(complaintDTO.getExpectedResolutionDate());
        complaint.setActualResolutionDate(complaintDTO.getActualResolutionDate());
        complaint.setSatisfactionRating(complaintDTO.getSatisfactionRating());
        complaint.setCreatedAt(complaintDTO.getCreatedAt());

        return complaint;
    }

    private ComplaintDTO convertToDTO(Complaint complaint) {
        ComplaintDTO complaintDTO = new ComplaintDTO();

        // Convert ObjectId to string
        if (complaint.getOrganizationId() != null) {
            complaintDTO.setOrganizationId(complaint.getOrganizationId().toHexString());
        }
        if (complaint.getUserId() != null) {
            complaintDTO.setUserId(complaint.getUserId().toHexString());
        }
        if (complaint.getCategoryId() != null) {
            complaintDTO.setCategoryId(complaint.getCategoryId().toHexString());
        }
        if (complaint.getWaterBoxId() != null) {
            complaintDTO.setWaterBoxId(complaint.getWaterBoxId().toHexString());
        }
        if (complaint.getAssignedToUserId() != null) {
            complaintDTO.setAssignedToUserId(complaint.getAssignedToUserId().toHexString());
        }

        // Copy other fields
        complaintDTO.setId(complaint.getId().toHexString());
        complaintDTO.setComplaintCode(complaint.getComplaintCode());
        complaintDTO.setComplaintDate(complaint.getComplaintDate());
        complaintDTO.setSubject(complaint.getSubject());
        complaintDTO.setDescription(complaint.getDescription());
        complaintDTO.setPriority(complaint.getPriority());
        complaintDTO.setStatus(complaint.getStatus());
        complaintDTO.setExpectedResolutionDate(complaint.getExpectedResolutionDate());
        complaintDTO.setActualResolutionDate(complaint.getActualResolutionDate());
        complaintDTO.setSatisfactionRating(complaint.getSatisfactionRating());
        complaintDTO.setCreatedAt(complaint.getCreatedAt());

        return complaintDTO;
    }
}