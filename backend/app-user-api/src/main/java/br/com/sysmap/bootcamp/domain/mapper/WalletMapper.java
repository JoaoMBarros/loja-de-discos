package br.com.sysmap.bootcamp.domain.mapper;

import br.com.sysmap.bootcamp.dto.WalletCreationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import br.com.sysmap.bootcamp.domain.entities.Wallet;

@Mapper
public interface WalletMapper {

    WalletMapper INSTANCE = Mappers.getMapper(WalletMapper.class);

    @Mapping(target = "id")
    Wallet toEntity(WalletCreationDto walletDto);

    //@Mapping(target = "id")
    //WalletDto toDto(Wallet wallet);
}
