package pe.edu.vallegrande.vg_ms_claims_incidents.domain.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document(collection = "complaint_responses")
public class ComplaintResponse {
    @Id
    private String id;
    @Field("complaint_id")
    private String complaintId;
    @Field("response_date")
    private Instant responseDate;
    @Field("response_type")
    private String responseType; // INVESTIGACION, SOLUCION, SEGUIMIENTO
    private String message;
    @Field("responded_by_user_id")
    private String respondedByUserId;
    @Field("internal_notes")
    private String internalNotes;
    @Field("created_at")
    private Instant createdAt;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    public Instant getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(Instant responseDate) {
        this.responseDate = responseDate;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRespondedByUserId() {
        return respondedByUserId;
    }

    public void setRespondedByUserId(String respondedByUserId) {
        this.respondedByUserId = respondedByUserId;
    }

    public String getInternalNotes() {
        return internalNotes;
    }

    public void setInternalNotes(String internalNotes) {
        this.internalNotes = internalNotes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}