package buysell.services;

import buysell.dao.create.CreateProductDto;
import buysell.dao.entityes.Product;
import buysell.dao.get.GetProductDto;
import buysell.dao.mappers.ProductMapper;
import buysell.dao.repository.ProductRepository;
import buysell.errors.ErrorMessages;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public GetProductDto getProductById(long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException(
                String.format(ErrorMessages.PRODUCT_NOT_FOUND, id)
            ));
        return productMapper.toDto(product);
    }

    public List<GetProductDto> getFilteredProducts(String title, Integer price, String city, String author) {
        return productMapper.toDtos(
            productRepository.findByTitleIgnoreCaseAndPriceAndCityIgnoreCaseAndAuthorIgnoreCase(
                title, price, city, author
            )
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
            .orElseThrow(() -> new NoSuchElementException(
                String.format(ErrorMessages.PRODUCT_NOT_FOUND, id)
            ));

        productMapper.updateProductFromDto(createProductDto, product);
        product = productRepository.save(product);

        return productMapper.toDto(product);
    }

    @Transactional
    public void deleteProduct(long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException(
                String.format(ErrorMessages.PRODUCT_NOT_FOUND, id)
            ));
        productRepository.delete(product);
    }
}










