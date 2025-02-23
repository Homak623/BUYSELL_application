package org.example.buysell_application.dao.mappers;

import org.example.buysell_application.dao.entityes.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OrderMapper extends BaseMapper<Order, OrderGetDto> {

    // Маппинг из OrderCreateDto в Order (для создания сущности)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Order toEntity(OrderCreateDto dto);

    // Маппинг из Order в OrderGetDto
    @Override
    OrderGetDto toDto(Order order);

    // Метод обновления существующего Order из OrderGetDto
    @Mapping(target = "id", ignore = true) // Не меняем ID при обновлении
    @Mapping(target = "createdAt", ignore = true) // Не трогаем дату создания
    void updateOrderFromDto(OrderGetDto dto, @MappingTarget Order order);
}


