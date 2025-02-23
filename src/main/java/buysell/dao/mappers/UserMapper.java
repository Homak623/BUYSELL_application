package buysell.dao.mappers;

import buysell.dao.create.CreateUserDto;
import buysell.dao.entityes.User;
import buysell.dao.get.GetUserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper extends BaseMapper<User, GetUserDto> {

    @Mapping(target = "id", ignore = true)
    User toEntity(CreateUserDto dto);

    @Override
    GetUserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    void updateUserFromDto(CreateUserDto dto, @MappingTarget User user);
}


