package br.com.sysmap.bootcamp.dto;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class WalletDebitDto implements Serializable {

    private String email;

    private BigDecimal value;

    private String idSpotify;
}
