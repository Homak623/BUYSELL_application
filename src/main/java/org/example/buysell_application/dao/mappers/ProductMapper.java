package org.example.buysell_application.dao.mappers;

import buysell.dao.dto.create.ProductCreateDto;
import org.example.buysell_application.dao.dto.ProductDto;
import org.example.buysell_application.dao.dto.create.CreateProductDto;
import org.example.buysell_application.dao.dto.get.GetProductDto;
import org.example.buysell_application.dao.entityes.Product;
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
    void updateProductFromDto(ProductCreateDto dto, @MappingTarget Product product);
}

