package buysell.dao.mappers;

import buysell.dao.dto.create.OrderCreateDto;
import buysell.dao.dto.get.OrderGetDto;
import buysell.dao.entityes.Order;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", config = BaseMapper.class)
public interface OrderMapper extends BaseMapper<Order, OrderGetDto> {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Order toEntity(OrderCreateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateOrderFromDto(OrderGetDto dto, @MappingTarget Order order);
}



