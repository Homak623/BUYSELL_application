package buysell.dao.mappers;

import buysell.dao.create.CreateProductDto;
import buysell.dao.entityes.Product;
import buysell.dao.get.GetProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper extends BaseMapper<Product, GetProductDto> {

    @Mapping(target = "id", ignore = true) // Игнорируем ID при создании
    Product toEntity(CreateProductDto dto);

    @Override
    GetProductDto toDto(Product product);

    // Метод обновления существующего продукта
    @Mapping(target = "id", ignore = true) // Не меняем ID
    void updateProductFromDto(CreateProductDto dto, @MappingTarget Product product);
}

