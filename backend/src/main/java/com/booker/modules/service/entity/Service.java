package com.booker.modules.service.entity;

import java.time.Instant;

import jakarta.persistence.*;

@Entity
@Table(name = "services", uniqueConstraints = @UniqueConstraint(name = "uk_services_name", columnNames = "name"), indexes = @Index(name = "idx_services_name", columnList = "name"))
public class Service {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private java.util.UUID id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 1000, nullable = false)
    private String description;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer durationInMinutes;

    @Column(nullable = false, columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
    private Double price;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "professional_uuid", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_services_professional"))
    private java.util.UUID professional;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE", nullable = false)
    private Boolean active;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

    // getters/setters
    public java.util.UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getDurationInMinutes() {
        return durationInMinutes;
    }

    public Double getPrice() {
        return price;
    }

    public java.util.UUID getProfessional() {
        return professional;
    }

    public Boolean getActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDurationInMinutes(Integer durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setProfessional(java.util.UUID professional) {
        this.professional = professional;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
