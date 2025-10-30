package pe.edu.vallegrande.vg_ms_claims_incidents.domain.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document(collection = "incident_types")
public class IncidentType {
    @Id
    private String id;
    @Field("organization_id")
    private String organizationId;
    @Field("type_code")
    private String typeCode;
    @Field("type_name")
    private String typeName;
    private String description;
    @Field("priority_level")
    private String priorityLevel;
    @Field("estimated_resolution_time")
    private Integer estimatedResolutionTime; // hours
    @Field("requires_external_service")
    private Boolean requiresExternalService;
    private String status;
    @Field("created_at")
    private Instant createdAt;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(String priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public Integer getEstimatedResolutionTime() {
        return estimatedResolutionTime;
    }

    public void setEstimatedResolutionTime(Integer estimatedResolutionTime) {
        this.estimatedResolutionTime = estimatedResolutionTime;
    }

    public Boolean getRequiresExternalService() {
        return requiresExternalService;
    }

    public void setRequiresExternalService(Boolean requiresExternalService) {
        this.requiresExternalService = requiresExternalService;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}