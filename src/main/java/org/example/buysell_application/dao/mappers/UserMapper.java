package org.example.buysell_application.dao.mappers;

import org.example.buysell_application.dao.dto.UserDto;
import org.example.buysell_application.dao.dto.create.CreateUserDto;
import org.example.buysell_application.dao.dto.get.GetUserDto;
import org.example.buysell_application.dao.entityes.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper extends BaseMapper<User, GetUserDto> {

    @Mapping(target = "id", ignore = true) // Игнорируем ID при обновлении
    User toEntity(CreateUserDto dto);

    @Override
    GetUserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    void updateUserFromDto(CreateUserDto dto, @MappingTarget User user);
}


