package com.fintech.ledger.entity;

import com.fintech.ledger.enums.Currency;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallets", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "currency"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    @Builder.Default
    @Column(name = "available_balance", nullable = false, precision = 30, scale = 8)
    private BigDecimal availableBalance = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "system_wallet", nullable = false)
    private Boolean systemWallet = false;

    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Version
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
