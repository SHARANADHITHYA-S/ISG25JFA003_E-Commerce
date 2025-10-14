package com.cognizant.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime; // Use the modern date/time class
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payment_methods")
@Builder
public class PaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;
    private String provider;
    private String account_number;
    private String cardholderName;
    private String expiry_date;
    private boolean is_default;

    @CreationTimestamp // Automatically sets creation timestamp
    private LocalDateTime created_at;

    @UpdateTimestamp // Automatically updates timestamp on changes
    private LocalDateTime updated_at;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "paymentMethod", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Order> orders = new HashSet<>();

    @OneToMany(mappedBy = "paymentMethod", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Payment> payments = new HashSet<>();


    public void setIs_default(boolean aDefault) {
        this.is_default = aDefault;
    }

    public boolean isIs_default() {
        return is_default;
    }

    public void setUpdated_at(LocalDateTime date) {
        this.updated_at = date;
    }

    public void setCreated_at(LocalDateTime date) {
        this.created_at = date;
    }
}