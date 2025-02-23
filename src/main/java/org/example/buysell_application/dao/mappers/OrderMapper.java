package org.example.buysell_application.dao.mappers;

import org.example.buysell_application.dao.dto.create.CreateOrderDto;
import org.example.buysell_application.dao.dto.get.GetOrderDto;
import org.example.buysell_application.dao.entityes.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OrderMapper extends BaseMapper<Order, GetOrderDto> {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Order toEntity(CreateOrderDto dto);

    @Override
    GetOrderDto toDto(Order order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateOrderFromDto(GetOrderDto dto, @MappingTarget Order order);
}



