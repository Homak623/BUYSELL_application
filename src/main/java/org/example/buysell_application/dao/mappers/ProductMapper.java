package org.example.buysell_application.dao.mappers;

import org.example.buysell_application.dao.dto.ProductDto;
import org.example.buysell_application.dao.entityes.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper extends BaseMapper<Product, ProductDto> {
}
