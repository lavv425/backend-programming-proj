package com.booker.modules.customer.entity;

import jakarta.persistence.*;

import com.booker.modules.user.entity.User;

@Entity
@Table(name = "customers", uniqueConstraints = @UniqueConstraint(name = "uk_customers_phone", columnNames = "phone_number"), indexes = @Index(name = "idx_customers_phone", columnList = "phone_number"))
public class Customer extends User {

    @Column(length = 15, nullable = false)
    private String phoneNumber;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer loyaltyPoints;

    // getters/setters

    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }
}
