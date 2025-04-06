package buysell.services;

import buysell.cache.CustomCache;
import buysell.dao.create.CreateProductDto;
import buysell.dao.entityes.Product;
import buysell.dao.get.GetProductDto;
import buysell.dao.mappers.ProductMapper;
import buysell.dao.repository.ProductRepository;
import buysell.enums.Status;
import buysell.errors.BadRequestException;
import buysell.errors.CannotDeleteProductException;
import buysell.errors.ErrorMessages;
import buysell.errors.ResourceNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CustomCache<Long, GetProductDto> productCache;

    public GetProductDto getProductById(long id) {
        GetProductDto cachedProduct = productCache.get(id);
        if (cachedProduct != null) {
            log.info("Cache hit for Product ID: {}", id);
            return cachedProduct;
        }

        log.info("Cache miss for Product ID: {}", id);
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format(ErrorMessages.PRODUCT_NOT_FOUND, id)
            ));

        GetProductDto productDto = productMapper.toDto(product);
        productCache.put(id, productDto);
        log.info("Product ID {} added to cache", id);

        return productDto;
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format(ErrorMessages.PRODUCT_NOT_FOUND, id)
            ));

        try {
            productRepository.delete(product);
            productCache.remove(id);
            log.info("Product ID {} removed from cache and deleted", id);
        } catch (PersistenceException e) {
            log.error("Failed to delete Product ID {}: {}", id, e.getMessage());
            throw new CannotDeleteProductException(
                String.format(ErrorMessages.PRODUCT_IN_USE, id)
            );
        }
    }

    public List<GetProductDto> getFilteredProducts(
        String title, Integer price, String city, String author, String orderStatus
    ) {
        return productMapper.toDtos(
            productRepository.findByFilters(title, price, city, author, orderStatus)
        );
    }

    public List<GetProductDto> findByPriceRange(String title, Double minPrice, Double maxPrice,
                                          String city, String author, String orderStatus) {
        log.info("Executing query with params: title={}, minPrice={}, maxPrice={}, city={}, author={}",
            title, minPrice, maxPrice, city, author);

        List<Product> result = productRepository.findByPriceRange(
            title,
            minPrice != null ? minPrice : null,
            maxPrice != null ? maxPrice : null,
            city,
            author
        );

        log.info("Found {} products", result.size());
        return productMapper.toDtos(result);
    }

    public List<GetProductDto> getFilteredProductsJPQL(
        String title, Integer price, String city, String author, String orderStatus
    ) throws BadRequestException {
        Status statusEnum = null;
        if (orderStatus != null && !orderStatus.isEmpty()) {
            try {
                statusEnum = Status.valueOf(orderStatus.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid order status: " + orderStatus);
            }
        }

        return productMapper.toDtos(
            productRepository.findByFiltersJPQL(title, price, city, author, statusEnum)
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
        GetProductDto productDto = productMapper.toDto(product);

        productCache.put(id, productDto);

        return productDto;
    }
}











