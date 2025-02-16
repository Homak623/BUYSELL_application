package org.example.buysell_application.dao.mappers;

import org.example.buysell_application.dao.entityes.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = BaseMapper.class)
public interface OrderMapper extends BaseMapper<Order, OrderDto> {

}

