package br.com.sysmap.bootcamp.dto;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WalletCreationDto implements Serializable {
    private Long id;
    private BigDecimal balance;
    private Long points;
    private LocalDateTime lastUpdate;
    private UserDto user;
}
