package br.com.sysmap.bootcamp.dto;

import lombok.*;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthDto {
    private String email;
    private String password;
    private Long id;
    private String token;
}
