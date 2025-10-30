package pe.edu.vallegrande.vg_ms_claims_incidents.domain.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.bson.types.ObjectId;

import java.time.Instant;

@Document(collection = "complaints")
public class Complaint {
    @Id
    private ObjectId id;

    @Field("organization_id")
    private ObjectId organizationId;

    @Field("complaint_code")
    private String complaintCode;

    @Field("user_id")
    private ObjectId userId;

    @Field("category_id")
    private ObjectId categoryId;

    @Field("water_box_id")
    private ObjectId waterBoxId;

    @Field("complaint_date")
    private Instant complaintDate;

    private String subject;
    private String description;
    private String priority;
    private String status; // RECEIVED, IN_PROGRESS, RESOLVED, CLOSED

    @Field("assigned_to_user_id")
    private ObjectId assignedToUserId;

    @Field("expected_resolution_date")
    private Instant expectedResolutionDate;

    @Field("actual_resolution_date")
    private Instant actualResolutionDate;

    @Field("satisfaction_rating")
    private Integer satisfactionRating; // 1-5 cuando se resuelve

    @Field("created_at")
    private Instant createdAt;

    // Getters and Setters
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(ObjectId organizationId) {
        this.organizationId = organizationId;
    }

    public String getComplaintCode() {
        return complaintCode;
    }

    public void setComplaintCode(String complaintCode) {
        this.complaintCode = complaintCode;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public ObjectId getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(ObjectId categoryId) {
        this.categoryId = categoryId;
    }

    public ObjectId getWaterBoxId() {
        return waterBoxId;
    }

    public void setWaterBoxId(ObjectId waterBoxId) {
        this.waterBoxId = waterBoxId;
    }

    public Instant getComplaintDate() {
        return complaintDate;
    }

    public void setComplaintDate(Instant complaintDate) {
        this.complaintDate = complaintDate;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ObjectId getAssignedToUserId() {
        return assignedToUserId;
    }

    public void setAssignedToUserId(ObjectId assignedToUserId) {
        this.assignedToUserId = assignedToUserId;
    }

    public Instant getExpectedResolutionDate() {
        return expectedResolutionDate;
    }

    public void setExpectedResolutionDate(Instant expectedResolutionDate) {
        this.expectedResolutionDate = expectedResolutionDate;
    }

    public Instant getActualResolutionDate() {
        return actualResolutionDate;
    }

    public void setActualResolutionDate(Instant actualResolutionDate) {
        this.actualResolutionDate = actualResolutionDate;
    }

    public Integer getSatisfactionRating() {
        return satisfactionRating;
    }

    public void setSatisfactionRating(Integer satisfactionRating) {
        this.satisfactionRating = satisfactionRating;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}