package org.example.buysell_application.dao.mappers;

import org.example.buysell_application.dao.dto.UserDto;
import org.example.buysell_application.dao.entityes.User;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapper.class)
public interface UserMapper extends BaseMapper<User, UserDto> {
}

