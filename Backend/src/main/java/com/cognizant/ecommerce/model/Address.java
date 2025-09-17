package com.cognizant.ecommerce.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "addresses")
@Builder
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String address_line1;
    private String address_line2;
    private String city;
    private String state;
    private String postal_code;
    private String country;
    private String phone;

    // This is the correct way to map the field to the database column.
    @Column(name = "is_default")
    private boolean isDefault;

    @CreationTimestamp
    private Date created_at;

    @UpdateTimestamp
    private Date updated_at;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    public Object getAddressLine1() {
        Object addressLine1 = null;
        return addressLine1;
    }

    public Object getAddressLine2() {
        Object addressLine2 = null;
        return addressLine2;

    }

    public Object getPostalCode() {
        Object postalCode = null;
        return postalCode;
    }
}