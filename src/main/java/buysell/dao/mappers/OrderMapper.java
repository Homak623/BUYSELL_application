package buysell.dao.mappers;

import buysell.dao.create.CreateOrderDto;
import buysell.dao.entityes.Order;
import buysell.dao.get.GetOrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", config = BaseMapper.class)
public interface OrderMapper extends BaseMapper<Order, GetOrderDto> {

    @Override
    @Mapping(target = "userId", source = "user.id") // Преобразуем User -> userId
    @Mapping(target = "productIds", expression = "java(order.getProducts() != null "
        + "? order.getProducts().stream()"
        + ".map(buysell.dao.entityes.Product::getId).toList() : new ArrayList<>())")
    GetOrderDto toDto(Order order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Order toEntity(CreateOrderDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateOrderFromDto(GetOrderDto dto, @MappingTarget Order order);
}




