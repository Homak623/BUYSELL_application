package buysell.dao.mappers;

import buysell.dao.entityes.Order;
import buysell.dao.get.GetOrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", config = BaseMapper.class)
public interface OrderMapper extends BaseMapper<Order, GetOrderDto> {

    @Override
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "productIds", expression = "java(order.getProducts() != null "
        + "? order.getProducts().stream()"
        + ".map(buysell.dao.entityes.Product::getId).toList() : new ArrayList<>())")
    GetOrderDto toDto(Order order);
}




