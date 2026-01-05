package com.booker.modules.enums.user;

/**
 * Defines the different user roles in the booking system.
 * Each role has specific permissions and access levels.
 * 
 * - ADMIN: Full system access, can manage all resources
 * - PROFESSIONAL: Can manage own services, view appointments, handle bookings
 * - CUSTOMER: Can book appointments, leave reviews, manage own profile
 */
public enum UserRole {
    ADMIN,
    PROFESSIONAL,
    CUSTOMER
}