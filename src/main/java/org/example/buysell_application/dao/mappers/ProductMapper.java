package org.example.buysell_application.dao.mappers;

import org.example.buysell_application.dao.dto.ProductDto;
import org.example.buysell_application.dao.entityes.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper extends BaseMapper<Product, ProductGetDto> {

    @Mapping(target = "id", ignore = true) // Игнорируем ID при создании
    Product toEntity(ProductCreateDto dto);

    @Override
    ProductGetDto toDto(Product product);

    // Метод обновления существующего продукта
    @Mapping(target = "id", ignore = true) // Не меняем ID
    void updateProductFromDto(ProductCreateDto dto, @MappingTarget Product product);
}

