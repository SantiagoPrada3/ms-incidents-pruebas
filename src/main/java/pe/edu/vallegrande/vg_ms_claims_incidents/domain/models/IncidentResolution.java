package pe.edu.vallegrande.vg_ms_claims_incidents.domain.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Document(collection = "incident_resolutions")
public class IncidentResolution {
    @Id
    private String id;
    @Field("incident_id")
    private String incidentId;
    @Field("resolution_date")
    private Date resolutionDate;
    @Field("resolution_type")
    private String resolutionType; // REPARACION_TEMPORAL, REPARACION_COMPLETA, REEMPLAZO
    @Field("actions_taken")
    private String actionsTaken;
    @Field("materials_used")
    private List<MaterialUsed> materialsUsed;
    @Field("labor_hours")
    private Integer laborHours;
    @Field("total_cost")
    private Double totalCost;
    @Field("resolved_by_user_id")
    private String resolvedByUserId;
    @Field("quality_check")
    private Boolean qualityCheck;
    @Field("follow_up_required")
    private Boolean followUpRequired;
    @Field("resolution_notes")
    private String resolutionNotes;
    @Field("created_at")
    private Date createdAt;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(String incidentId) {
        this.incidentId = incidentId;
    }

    public Date getResolutionDate() {
        return resolutionDate;
    }

    public void setResolutionDate(Date resolutionDate) {
        this.resolutionDate = resolutionDate;
    }

    public String getResolutionType() {
        return resolutionType;
    }

    public void setResolutionType(String resolutionType) {
        this.resolutionType = resolutionType;
    }

    public String getActionsTaken() {
        return actionsTaken;
    }

    public void setActionsTaken(String actionsTaken) {
        this.actionsTaken = actionsTaken;
    }

    public List<MaterialUsed> getMaterialsUsed() {
        return materialsUsed;
    }

    public void setMaterialsUsed(List<MaterialUsed> materialsUsed) {
        this.materialsUsed = materialsUsed;
    }

    public Integer getLaborHours() {
        return laborHours;
    }

    public void setLaborHours(Integer laborHours) {
        this.laborHours = laborHours;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public String getResolvedByUserId() {
        return resolvedByUserId;
    }

    public void setResolvedByUserId(String resolvedByUserId) {
        this.resolvedByUserId = resolvedByUserId;
    }

    public Boolean getQualityCheck() {
        return qualityCheck;
    }

    public void setQualityCheck(Boolean qualityCheck) {
        this.qualityCheck = qualityCheck;
    }

    public Boolean getFollowUpRequired() {
        return followUpRequired;
    }

    public void setFollowUpRequired(Boolean followUpRequired) {
        this.followUpRequired = followUpRequired;
    }

    public String getResolutionNotes() {
        return resolutionNotes;
    }

    public void setResolutionNotes(String resolutionNotes) {
        this.resolutionNotes = resolutionNotes;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}