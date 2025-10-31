package pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

/**
 * DTO para mapear la respuesta del MS-USUARIOS
 */
public class UserServiceResponseDTO {
    
    @JsonProperty("success")
    private Boolean success;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("data")
    private UserServiceDataDTO data;
    
    // Explicit getters and setters to avoid Lombok compilation issues
    public Boolean getSuccess() {
        return success;
    }
    
    public void setSuccess(Boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public UserServiceDataDTO getData() {
        return data;
    }
    
    public void setData(UserServiceDataDTO data) {
        this.data = data;
    }
    
    public static class UserServiceDataDTO {
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("userCode")
        private String userCode;
        
        @JsonProperty("firstName")
        private String firstName;
        
        @JsonProperty("lastName")
        private String lastName;
        
        @JsonProperty("documentType")
        private String documentType;
        
        @JsonProperty("documentNumber")
        private String documentNumber;
        
        @JsonProperty("email")
        private String email;
        
        @JsonProperty("phone")
        private String phone;
        
        @JsonProperty("address")
        private String address;
        
        @JsonProperty("roles")
        private List<String> roles;
        
        @JsonProperty("status")
        private String status;
        
        @JsonProperty("createdAt")
        private LocalDateTime createdAt;
        
        @JsonProperty("updatedAt")
        private LocalDateTime updatedAt;
        
        @JsonProperty("organization")
        private OrganizationDTO organization;
        
        @JsonProperty("zone")
        private ZoneDTO zone;
        
        @JsonProperty("street")
        private StreetDTO street;
        
        // Explicit getters and setters to avoid Lombok compilation issues
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getUserCode() {
            return userCode;
        }
        
        public void setUserCode(String userCode) {
            this.userCode = userCode;
        }
        
        public String getFirstName() {
            return firstName;
        }
        
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }
        
        public String getLastName() {
            return lastName;
        }
        
        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
        
        public String getDocumentType() {
            return documentType;
        }
        
        public void setDocumentType(String documentType) {
            this.documentType = documentType;
        }
        
        public String getDocumentNumber() {
            return documentNumber;
        }
        
        public void setDocumentNumber(String documentNumber) {
            this.documentNumber = documentNumber;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getPhone() {
            return phone;
        }
        
        public void setPhone(String phone) {
            this.phone = phone;
        }
        
        public String getAddress() {
            return address;
        }
        
        public void setAddress(String address) {
            this.address = address;
        }
        
        public List<String> getRoles() {
            return roles;
        }
        
        public void setRoles(List<String> roles) {
            this.roles = roles;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
        
        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
        
        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }
        
        public void setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }
        
        public OrganizationDTO getOrganization() {
            return organization;
        }
        
        public void setOrganization(OrganizationDTO organization) {
            this.organization = organization;
        }
        
        public ZoneDTO getZone() {
            return zone;
        }
        
        public void setZone(ZoneDTO zone) {
            this.zone = zone;
        }
        
        public StreetDTO getStreet() {
            return street;
        }
        
        public void setStreet(StreetDTO street) {
            this.street = street;
        }
    }
    
    public static class OrganizationDTO {
        @JsonProperty("organizationId")
        private String organizationId;
        
        @JsonProperty("organizationCode")
        private String organizationCode;
        
        @JsonProperty("organizationName")
        private String organizationName;
        
        @JsonProperty("legalRepresentative")
        private String legalRepresentative;
        
        @JsonProperty("phone")
        private String phone;
        
        @JsonProperty("address")
        private String address;
        
        @JsonProperty("status")
        private String status;
        
        // Explicit getters and setters to avoid Lombok compilation issues
        public String getOrganizationId() {
            return organizationId;
        }
        
        public void setOrganizationId(String organizationId) {
            this.organizationId = organizationId;
        }
        
        public String getOrganizationCode() {
            return organizationCode;
        }
        
        public void setOrganizationCode(String organizationCode) {
            this.organizationCode = organizationCode;
        }
        
        public String getOrganizationName() {
            return organizationName;
        }
        
        public void setOrganizationName(String organizationName) {
            this.organizationName = organizationName;
        }
        
        public String getLegalRepresentative() {
            return legalRepresentative;
        }
        
        public void setLegalRepresentative(String legalRepresentative) {
            this.legalRepresentative = legalRepresentative;
        }
        
        public String getPhone() {
            return phone;
        }
        
        public void setPhone(String phone) {
            this.phone = phone;
        }
        
        public String getAddress() {
            return address;
        }
        
        public void setAddress(String address) {
            this.address = address;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
    }
    
    public static class ZoneDTO {
        @JsonProperty("zoneId")
        private String zoneId;
        
        @JsonProperty("zoneCode")
        private String zoneCode;
        
        @JsonProperty("zoneName")
        private String zoneName;
        
        @JsonProperty("description")
        private String description;
        
        @JsonProperty("status")
        private String status;
        
        // Explicit getters and setters to avoid Lombok compilation issues
        public String getZoneId() {
            return zoneId;
        }
        
        public void setZoneId(String zoneId) {
            this.zoneId = zoneId;
        }
        
        public String getZoneCode() {
            return zoneCode;
        }
        
        public void setZoneCode(String zoneCode) {
            this.zoneCode = zoneCode;
        }
        
        public String getZoneName() {
            return zoneName;
        }
        
        public void setZoneName(String zoneName) {
            this.zoneName = zoneName;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
    }
    
    public static class StreetDTO {
        @JsonProperty("streetId")
        private String streetId;
        
        @JsonProperty("streetCode")
        private String streetCode;
        
        @JsonProperty("streetName")
        private String streetName;
        
        @JsonProperty("streetType")
        private String streetType;
        
        @JsonProperty("status")
        private String status;
        
        // Explicit getters and setters to avoid Lombok compilation issues
        public String getStreetId() {
            return streetId;
        }
        
        public void setStreetId(String streetId) {
            this.streetId = streetId;
        }
        
        public String getStreetCode() {
            return streetCode;
        }
        
        public void setStreetCode(String streetCode) {
            this.streetCode = streetCode;
        }
        
        public String getStreetName() {
            return streetName;
        }
        
        public void setStreetName(String streetName) {
            this.streetName = streetName;
        }
        
        public String getStreetType() {
            return streetType;
        }
        
        public void setStreetType(String streetType) {
            this.streetType = streetType;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
    }
    
    /**
     * Convierte la respuesta del MS-USUARIOS a nuestro UserDTO
     */
    public UserDTO toUserDTO() {
        if (!success || data == null) {
            return null;
        }
        
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(data.getId());
        userDTO.setUserCode(data.getUserCode());
        userDTO.setUsername(data.getUserCode());
        userDTO.setStatus(data.getStatus());
        userDTO.setRoles(data.getRoles());
        
        // Personal Info
        UserDTO.PersonalInfo personalInfo = new UserDTO.PersonalInfo();
        personalInfo.setFirstName(data.getFirstName());
        personalInfo.setLastName(data.getLastName());
        personalInfo.setDocumentType(data.getDocumentType());
        personalInfo.setDocumentNumber(data.getDocumentNumber());
        userDTO.setPersonalInfo(personalInfo);
        
        // Contact
        UserDTO.Contact contact = new UserDTO.Contact();
        contact.setEmail(data.getEmail());
        contact.setPhone(data.getPhone());
        userDTO.setContact(contact);
        
        // Address
        UserDTO.Address address = new UserDTO.Address();
        address.setStreet(data.getAddress());
        if (data.getZone() != null) {
            address.setDistrict(data.getZone().getZoneName());
        }
        if (data.getOrganization() != null) {
            address.setProvince(data.getOrganization().getOrganizationName());
        }
        address.setDepartment("Lima"); // Valor por defecto
        address.setCountry("Perú");
        userDTO.setAddress(address);
        
        // Organization ID
        if (data.getOrganization() != null) {
            userDTO.setOrganizationId(data.getOrganization().getOrganizationId());
        }
        
        // Timestamps
        if (data.getCreatedAt() != null) {
            userDTO.setCreatedAt(Date.from(data.getCreatedAt().toInstant(ZoneOffset.UTC)));
        }
        if (data.getUpdatedAt() != null) {
            userDTO.setUpdatedAt(Date.from(data.getUpdatedAt().toInstant(ZoneOffset.UTC)));
        }
        
        return userDTO;
    }
}