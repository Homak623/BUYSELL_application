package org.example.buysell_application.services;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.example.buysell_application.dao.dto.ProductDto;
import org.example.buysell_application.dao.entityes.Product;
import org.example.buysell_application.dao.mappers.ProductMapper;
import org.example.buysell_application.dao.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceInDataBase {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductGetDto getProductById(long id) {
        return productMapper.toDto(
            productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product with id " + id + " not found"))
        );
    }

    public List<ProductGetDto> getFilteredProducts(String title, Integer price, String city, String author) {
        return productMapper.toDtos(
            productRepository.findByTitleIgnoreCaseAndPriceAndCityIgnoreCaseAndAuthorIgnoreCase(title, price, city, author)
        );
    }

    public ProductGetDto createProduct(ProductCreateDto productCreateDto) {
        Product product = productMapper.toEntity(productCreateDto);
        return productMapper.toDto(productRepository.save(product));
    }

    public ProductGetDto updateProduct(Long id, ProductCreateDto productCreateDto) {
        return productRepository.findById(id).map(product -> {
            productMapper.updateProductFromDto(productCreateDto, product);
            return productMapper.toDto(productRepository.save(product));
        }).orElseThrow(() -> new NoSuchElementException("Product with id " + id + " not found"));
    }

    public void deleteProduct(long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Product with id " + id + " not found"));
        productRepository.delete(product);
    }
}







