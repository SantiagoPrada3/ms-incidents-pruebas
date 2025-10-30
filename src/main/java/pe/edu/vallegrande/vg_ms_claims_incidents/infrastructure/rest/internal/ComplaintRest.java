package pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.rest.internal;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.vg_ms_claims_incidents.application.services.ComplaintService;
import pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.dto.ComplaintDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/internal/complaints")
@Tag(name = "Internal Complaints", description = "API para personal interno para gestión de quejas")
public class ComplaintRest {

    private static final Logger log = LoggerFactory.getLogger(ComplaintRest.class);

    private final ComplaintService complaintService;

    public ComplaintRest(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    /**
     * Obtener todas las quejas
     */
    @GetMapping
    @Operation(summary = "Obtener todas las quejas", description = "Retorna todas las quejas registradas en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quejas encontradas"),
            @ApiResponse(responseCode = "204", description = "No hay quejas registradas"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Mono<ResponseEntity<Flux<ComplaintDTO>>> getAllComplaints() {
        log.info("Obteniendo todas las quejas");
        return Mono.just(ResponseEntity.ok(complaintService.findAll()));
    }

    /**
     * Obtener queja por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener queja por ID", description = "Retorna una queja específica por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Queja encontrada"),
            @ApiResponse(responseCode = "404", description = "Queja no encontrada"),
            @ApiResponse(responseCode = "400", description = "ID inválido"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Mono<ResponseEntity<ComplaintDTO>> getComplaintById(
            @Parameter(description = "ID de la queja", required = true) @PathVariable String id) {
        log.info("Obteniendo queja con ID: {}", id);
        try {
            // Validar que el ID sea un ObjectId válido
            new ObjectId(id);
            return complaintService.findById(id)
                    .map(ResponseEntity::ok)
                    .defaultIfEmpty(ResponseEntity.notFound().build())
                    .onErrorResume(e -> {
                        log.error("Error al obtener queja: {}", e.getMessage());
                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                    });
        } catch (IllegalArgumentException e) {
            log.error("ID inválido: {}", id);
            return Mono.just(ResponseEntity.badRequest().build());
        }
    }

    /**
     * Actualizar queja existente (para asignación y seguimiento)
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar queja", description = "Actualiza una queja existente por su ID (para asignación y seguimiento)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Queja actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Queja no encontrada"),
            @ApiResponse(responseCode = "400", description = "ID inválido o datos inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Mono<ResponseEntity<ComplaintDTO>> updateComplaint(
            @Parameter(description = "ID de la queja", required = true) @PathVariable String id,
            @RequestBody ComplaintDTO complaintDTO) {
        log.info("Actualizando queja con ID: {}", id);
        try {
            // Validar que el ID sea un ObjectId válido
            new ObjectId(id);
            return complaintService.update(id, complaintDTO)
                    .map(ResponseEntity::ok)
                    .defaultIfEmpty(ResponseEntity.notFound().build())
                    .onErrorResume(e -> {
                        log.error("Error al actualizar queja: {}", e.getMessage());
                        if (e instanceof IllegalArgumentException) {
                            return Mono.just(ResponseEntity.badRequest().build());
                        }
                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                    });
        } catch (IllegalArgumentException e) {
            log.error("ID inválido: {}", id);
            return Mono.just(ResponseEntity.badRequest().build());
        }
    }

    /**
     * Asignar queja a un usuario interno
     */
    @PutMapping("/{id}/assign")
    @Operation(summary = "Asignar queja", description = "Asigna una queja a un usuario interno para su atención")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Queja asignada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Queja no encontrada"),
            @ApiResponse(responseCode = "400", description = "ID inválido o datos inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Mono<ResponseEntity<ComplaintDTO>> assignComplaint(
            @Parameter(description = "ID de la queja", required = true) @PathVariable String id,
            @RequestParam String assignedToUserId) {
        log.info("Asignando queja con ID: {} al usuario: {}", id, assignedToUserId);
        try {
            // Validar que los IDs sean ObjectId válidos
            new ObjectId(id);
            new ObjectId(assignedToUserId);
            
            return complaintService.findById(id)
                    .flatMap(complaint -> {
                        complaint.setAssignedToUserId(assignedToUserId);
                        complaint.setStatus("IN_PROGRESS");
                        return complaintService.update(id, complaint);
                    })
                    .map(ResponseEntity::ok)
                    .defaultIfEmpty(ResponseEntity.notFound().build())
                    .onErrorResume(e -> {
                        log.error("Error al asignar queja: {}", e.getMessage());
                        if (e instanceof IllegalArgumentException) {
                            return Mono.just(ResponseEntity.badRequest().build());
                        }
                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                    });
        } catch (IllegalArgumentException e) {
            log.error("ID inválido: {} o assignedToUserId: {}", id, assignedToUserId);
            return Mono.just(ResponseEntity.badRequest().build());
        }
    }

    /**
     * Cambiar estado de la queja
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "Cambiar estado de queja", description = "Actualiza el estado de una queja existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado de queja actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Queja no encontrada"),
            @ApiResponse(responseCode = "400", description = "ID inválido o estado inválido"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Mono<ResponseEntity<ComplaintDTO>> updateComplaintStatus(
            @Parameter(description = "ID de la queja", required = true) @PathVariable String id,
            @RequestParam String status) {
        log.info("Actualizando estado de queja con ID: {} a: {}", id, status);
        try {
            // Validar que el ID sea un ObjectId válido
            new ObjectId(id);
            
            // Validar el estado
            String upperStatus = status.toUpperCase();
            if (!upperStatus.matches("RECEIVED|IN_PROGRESS|RESOLVED|CLOSED")) {
                return Mono.just(ResponseEntity.badRequest().build());
            }
            
            return complaintService.findById(id)
                    .flatMap(complaint -> {
                        complaint.setStatus(upperStatus);
                        if (upperStatus.equals("RESOLVED")) {
                            complaint.setActualResolutionDate(java.time.Instant.now());
                        }
                        return complaintService.update(id, complaint);
                    })
                    .map(ResponseEntity::ok)
                    .defaultIfEmpty(ResponseEntity.notFound().build())
                    .onErrorResume(e -> {
                        log.error("Error al actualizar estado de queja: {}", e.getMessage());
                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                    });
        } catch (IllegalArgumentException e) {
            log.error("ID inválido: {}", id);
            return Mono.just(ResponseEntity.badRequest().build());
        }
    }
}