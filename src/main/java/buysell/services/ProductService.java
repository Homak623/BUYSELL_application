package buysell.services;

import buysell.dao.dto.create.CreateProductDto;
import buysell.dao.dto.get.GetProductDto;
import buysell.dao.entityes.Product;
import buysell.dao.mappers.ProductMapper;
import buysell.dao.repository.ProductRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public GetProductDto getProductById(long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Product with id " + id + " not found"));
        return productMapper.toDto(product);
    }

    public List<GetProductDto> getFilteredProducts(String title,
                                                   Integer price, String city, String author) {
        return productMapper.toDtos(
            productRepository.findByTitleIgnoreCaseAndPriceAndCityIgnoreCaseAndAuthorIgnoreCase(
                title, price, city, author)
        );
    }

    @Transactional
    public GetProductDto createProduct(CreateProductDto createProductDto) {
        Product product = productMapper.toEntity(createProductDto);
        product = productRepository.save(product);
        return productMapper.toDto(product);
    }

    @Transactional
    public GetProductDto updateProduct(Long id, CreateProductDto createProductDto) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Product with id " + id + " not found"));

        productMapper.updateProductFromDto(createProductDto, product);
        product = productRepository.save(product);

        return productMapper.toDto(product);
    }

    @Transactional
    public void deleteProduct(long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Product with id " + id + " not found"));
        productRepository.delete(product);
    }
}









