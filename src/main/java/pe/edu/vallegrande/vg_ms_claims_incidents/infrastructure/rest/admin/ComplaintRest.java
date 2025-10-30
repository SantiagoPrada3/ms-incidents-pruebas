package pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.rest.admin;

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
@RequestMapping("/api/admin/complaints")
@Tag(name = "Admin Complaints", description = "API de administración para gestión completa de quejas")
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
     * Crear nueva queja
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear nueva queja", description = "Crea una nueva queja en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Queja creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de queja inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Mono<ResponseEntity<ComplaintDTO>> createComplaint(@RequestBody ComplaintDTO complaintDTO) {
        log.info("Creando nueva queja");
        return complaintService.save(complaintDTO)
                .map(savedComplaint -> ResponseEntity.status(HttpStatus.CREATED).body(savedComplaint))
                .onErrorResume(e -> {
                    log.error("Error al crear queja: {}", e.getMessage());
                    if (e instanceof IllegalArgumentException) {
                        return Mono.just(ResponseEntity.badRequest().build());
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    /**
     * Actualizar queja existente
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar queja", description = "Actualiza una queja existente por su ID")
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
     * Eliminar queja (cambiar estado a INACTIVE)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar queja", description = "Elimina una queja por su ID (cambio de estado a INACTIVE)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Queja eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Queja no encontrada"),
            @ApiResponse(responseCode = "400", description = "ID inválido"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Mono<ResponseEntity<Void>> deleteComplaint(
            @Parameter(description = "ID de la queja", required = true) @PathVariable String id) {
        log.info("Eliminando queja con ID: {}", id);
        try {
            // Validar que el ID sea un ObjectId válido
            new ObjectId(id);
            return complaintService.deleteById(id)
                    .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                    .onErrorResume(e -> {
                        log.error("Error al eliminar queja: {}", e.getMessage());
                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                    });
        } catch (IllegalArgumentException e) {
            log.error("ID inválido: {}", id);
            return Mono.just(ResponseEntity.badRequest().build());
        }
    }

    /**
     * Restaurar queja (cambiar estado a ACTIVE)
     */
    @PutMapping("/{id}/restore")
    @Operation(summary = "Restaurar queja", description = "Restaura una queja eliminada por su ID (cambio de estado a ACTIVE)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Queja restaurada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Queja no encontrada"),
            @ApiResponse(responseCode = "400", description = "ID inválido"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Mono<ResponseEntity<ComplaintDTO>> restoreComplaint(
            @Parameter(description = "ID de la queja", required = true) @PathVariable String id) {
        log.info("Restaurando queja con ID: {}", id);
        try {
            // Validar que el ID sea un ObjectId válido
            new ObjectId(id);
            return complaintService.restoreById(id)
                    .map(ResponseEntity::ok)
                    .defaultIfEmpty(ResponseEntity.notFound().build())
                    .onErrorResume(e -> {
                        log.error("Error al restaurar queja: {}", e.getMessage());
                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                    });
        } catch (IllegalArgumentException e) {
            log.error("ID inválido: {}", id);
            return Mono.just(ResponseEntity.badRequest().build());
        }
    }
}