package br.com.sysmap.bootcamp.domain.mapper;

import br.com.sysmap.bootcamp.domain.entities.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import br.com.sysmap.bootcamp.dto.UserDto;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id")
    Users toEntity(UserDto userDto);

    //UserDto toDto(Users user);
}
