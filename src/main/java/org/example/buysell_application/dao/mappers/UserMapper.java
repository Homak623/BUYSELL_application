package org.example.buysell_application.dao.mappers;

import org.example.buysell_application.dao.dto.UserDto;
import org.example.buysell_application.dao.entityes.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper extends BaseMapper<User, UserGetDto> {

    @Mapping(target = "id", ignore = true) // Игнорируем ID при обновлении
    User toEntity(UserCreateDto dto);

    @Override
    UserGetDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    void updateUserFromDto(UserCreateDto dto, @MappingTarget User user);
}


