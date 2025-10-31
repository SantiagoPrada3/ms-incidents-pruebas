package pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.dto;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class UserDTO {
    private String userId;
    private String userCode;
    private String username;
    private String status;
    private List<String> roles;
    private String organizationId;
    private PersonalInfo personalInfo;
    private Contact contact;
    private Address address;
    private Date createdAt;
    private Date updatedAt;

    // Explicit getters and setters to avoid Lombok compilation issues
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getUserCode() {
        return userCode;
    }
    
    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public List<String> getRoles() {
        return roles;
    }
    
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
    
    public String getOrganizationId() {
        return organizationId;
    }
    
    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }
    
    public PersonalInfo getPersonalInfo() {
        return personalInfo;
    }
    
    public void setPersonalInfo(PersonalInfo personalInfo) {
        this.personalInfo = personalInfo;
    }
    
    public Contact getContact() {
        return contact;
    }
    
    public void setContact(Contact contact) {
        this.contact = contact;
    }
    
    public Address getAddress() {
        return address;
    }
    
    public void setAddress(Address address) {
        this.address = address;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Data
    public static class PersonalInfo {
        private String firstName;
        private String lastName;
        private String documentType;
        private String documentNumber;
        
        // Explicit getters and setters to avoid Lombok compilation issues
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
        
        public String getFullName() {
            return firstName + " " + lastName;
        }
    }

    @Data
    public static class Contact {
        private String email;
        private String phone;
        
        // Manual getters to avoid Lombok compilation issues
        public String getEmail() {
            return email;
        }
        
        public String getPhone() {
            return phone;
        }
        
        // Manual setters to avoid Lombok compilation issues
        public void setEmail(String email) {
            this.email = email;
        }
        
        public void setPhone(String phone) {
            this.phone = phone;
        }
    }

    @Data
    public static class Address {
        private String street;
        private String district;
        private String province;
        private String department;
        private String country;
        
        // Explicit getters and setters to avoid Lombok compilation issues
        public String getStreet() {
            return street;
        }
        
        public void setStreet(String street) {
            this.street = street;
        }
        
        public String getDistrict() {
            return district;
        }
        
        public void setDistrict(String district) {
            this.district = district;
        }
        
        public String getProvince() {
            return province;
        }
        
        public void setProvince(String province) {
            this.province = province;
        }
        
        public String getDepartment() {
            return department;
        }
        
        public void setDepartment(String department) {
            this.department = department;
        }
        
        public String getCountry() {
            return country;
        }
        
        public void setCountry(String country) {
            this.country = country;
        }
        
        public String getFullAddress() {
            return street + ", " + district + ", " + province + ", " + department;
        }
    }

    // Métodos de conveniencia
    public String getFullName() {
        return personalInfo != null ? personalInfo.getFullName() : username;
    }

    public String getContactEmail() {
        return contact != null ? contact.getEmail() : null;
    }

    public String getContactPhone() {
        return contact != null ? contact.getPhone() : null;
    }

    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    public List<String> getNotificationEmails() {
        if (contact != null && contact.getEmail() != null) {
            return List.of(contact.getEmail());
        }
        return List.of();
    }
}