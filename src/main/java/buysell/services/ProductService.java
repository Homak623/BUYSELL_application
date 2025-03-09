package buysell.services;

import buysell.dao.create.CreateProductDto;
import buysell.dao.entityes.Product;
import buysell.dao.get.GetProductDto;
import buysell.dao.mappers.ProductMapper;
import buysell.dao.repository.ProductRepository;
import buysell.errors.CannotDeleteProductException;
import buysell.errors.ErrorMessages;
import buysell.errors.ResourceNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public GetProductDto getProductById(long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format(ErrorMessages.PRODUCT_NOT_FOUND, id)
            ));
        return productMapper.toDto(product);
    }

    public List<GetProductDto> getFilteredProducts(String title,
                                                   Integer price, String city, String author) {
        return productMapper.toDtos(
            productRepository.findByFilters(
                title, price, city, author
            )
        );
    }

    public List<GetProductDto> getAllProducts() {
        return productMapper.toDtos(productRepository.findAll());
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
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format(ErrorMessages.PRODUCT_NOT_FOUND, id)
            ));

        productMapper.updateProductFromDto(createProductDto, product);
        product = productRepository.save(product);

        return productMapper.toDto(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format(ErrorMessages.PRODUCT_NOT_FOUND, id)
            ));

        try {
            productRepository.delete(product);
        } catch (PersistenceException e) {
            throw new CannotDeleteProductException(
                String.format(ErrorMessages.PRODUCT_IN_USE, id)
            );
        }
    }
}











