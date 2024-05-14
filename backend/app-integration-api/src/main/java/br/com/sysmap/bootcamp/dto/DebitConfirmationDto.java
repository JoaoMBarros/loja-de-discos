package br.com.sysmap.bootcamp.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class DebitConfirmationDto implements Serializable {
    private String userEmail;
    private String idSpotify;
    private Boolean isDebitConfirmed;
}