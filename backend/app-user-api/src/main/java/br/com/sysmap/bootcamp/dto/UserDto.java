package br.com.sysmap.bootcamp.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder(toBuilder = true)
public class UserDto implements Serializable {
        private Long id;
        private String name;
        private String email;
        private String password;
}
