package br.com.sysmap.bootcamp.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "WALLETS")
public class Wallet{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "points")
    private Long points;

    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

    @OneToOne
    @JoinColumn(name = "user_id")
    private Users user;
}
