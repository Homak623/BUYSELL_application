package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import buysell.cache.CustomCache;
import buysell.dao.create.CreateProductDto;
import buysell.dao.entityes.Product;
import buysell.dao.get.GetProductDto;
import buysell.dao.mappers.ProductMapper;
import buysell.dao.repository.ProductRepository;
import buysell.enums.Status;
import buysell.errors.BadRequestException;
import buysell.errors.CannotDeleteProductException;
import buysell.errors.ResourceNotFoundException;
import buysell.services.ProductService;
import jakarta.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private CustomCache<Long, GetProductDto> productCache;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private CreateProductDto createProductDto;
    private GetProductDto getProductDto;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setTitle("Test Product");

        createProductDto = new CreateProductDto();
        createProductDto.setTitle("Test Product");

        getProductDto = new GetProductDto();
        getProductDto.setId(1L);
        getProductDto.setTitle("Test Product");
    }

    @Test
    void getProductById_CacheHit() {

        Long productId = 1L;
        when(productCache.get(productId)).thenReturn(getProductDto);

        GetProductDto result = productService.getProductById(productId);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(productCache, times(1)).get(productId);
        verify(productRepository, never()).findById(anyLong());
    }

    @Test
    void getProductById_CacheMiss() {

        Long productId = 1L;
        when(productCache.get(productId)).thenReturn(null); // Кэш пуст
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(getProductDto);

        GetProductDto result = productService.getProductById(productId);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(productCache, times(1)).get(productId); // Проверяем, что кэш был проверен
        verify(productRepository, times(1)).findById(productId); // Проверяем, что данные загружены из репозитория
        verify(productCache, times(1)).put(productId, getProductDto); // Проверяем, что данные добавлены в кэш
    }

    @Test
    void deleteProduct_SuccessWithCache() {

        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        productService.deleteProduct(productId);

        verify(productRepository, times(1)).findById(productId); // Проверяем, что репозиторий был вызван
        verify(productRepository, times(1)).delete(product); // Проверяем, что продукт был удален
        verify(productCache, times(1)).remove(productId); // Проверяем, что кэш был очищен
    }

    @Test
    void getFilteredProducts_EmptyList() {

        when(productRepository.findByFilters("title", 100,
            "city", "author", "status"))
            .thenReturn(List.of());
        when(productMapper.toDtos(List.of())).thenReturn(List.of());

        List<GetProductDto> result = productService.getFilteredProducts("title",
            100, "city", "author", "status");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository, times(1)).findByFilters("title",
            100, "city", "author", "status");
        verify(productMapper, times(1)).toDtos(List.of());
    }

    @Test
    void getProductById_ProductNotFound() {

        Long productId = 1L;
        when(productCache.get(productId)).thenReturn(null);
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(productId));
        verify(productCache, times(1)).get(productId);
        verify(productRepository, times(1)).findById(productId);
        verify(productCache, never()).put(anyLong(), any());
    }

    @Test
    void getFilteredProductsJPQL_NullStatus() throws BadRequestException {

        when(productRepository.findByFiltersJPQL("title", 100, "city",
            "author", null))
            .thenReturn(List.of(product));
        when(productMapper.toDtos(List.of(product))).thenReturn(List.of(getProductDto));

        List<GetProductDto> result = productService.getFilteredProductsJPQL("title",
            100, "city", "author", null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).findByFiltersJPQL("title",
            100, "city", "author", null);
        verify(productMapper, times(1)).toDtos(List.of(product));
    }

    @Test
    void getFilteredProductsJPQL_EmptyList() throws BadRequestException {
        when(productRepository.findByFiltersJPQL("title", 100, "city", "author",
            Status.CREATED))
            .thenReturn(List.of());
        when(productMapper.toDtos(List.of())).thenReturn(List.of());

        List<GetProductDto> result = productService.getFilteredProductsJPQL("title", 100,
            "city", "author", "CREATED");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository, times(1)).findByFiltersJPQL("title",
            100, "city", "author", Status.CREATED);
        verify(productMapper, times(1)).toDtos(List.of());
    }

    @Test
    void getAllProducts_EmptyList() {
        when(productRepository.findAll()).thenReturn(List.of());
        when(productMapper.toDtos(List.of())).thenReturn(List.of());

        List<GetProductDto> result = productService.getAllProducts();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository, times(1)).findAll();
        verify(productMapper, times(1)).toDtos(List.of());
    }

    @Test
    void updateProduct_UpdateCache() {

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(getProductDto);

        GetProductDto result = productService.updateProduct(1L, createProductDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(productCache, times(1)).put(1L, getProductDto);
    }

    @Test
    void deleteProduct_ProductNotFound() {

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(1L));
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, never()).delete(any(Product.class));
        verify(productCache, never()).remove(anyLong());
    }

    @Test
    void deleteProduct_FailedToDelete() {

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doThrow(PersistenceException.class).when(productRepository).delete(product);

        assertThrows(CannotDeleteProductException.class, () -> productService.deleteProduct(1L));
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).delete(product);
        verify(productCache, never()).remove(anyLong());
    }

    @Test
    void getFilteredProducts_Success() {

        when(productRepository.findByFilters("title", 100, "city", "author", "status"))
            .thenReturn(List.of(product));
        when(productMapper.toDtos(List.of(product))).thenReturn(List.of(getProductDto));

        List<GetProductDto>
            result = productService.getFilteredProducts("title", 100, "city", "author", "status");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(productRepository, times(1)).findByFilters("title", 100, "city", "author", "status");
        verify(productMapper, times(1)).toDtos(List.of(product));
    }

    @Test
    void getFilteredProductsJPQL_Success() throws BadRequestException {

        when(productRepository.findByFiltersJPQL("title", 100, "city", "author", Status.CREATED))
            .thenReturn(List.of(product));
        when(productMapper.toDtos(List.of(product))).thenReturn(List.of(getProductDto));

        List<GetProductDto> result =
            productService.getFilteredProductsJPQL("title", 100, "city", "author", "CREATED");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(productRepository, times(1)).findByFiltersJPQL("title", 100, "city", "author",
            Status.CREATED);
        verify(productMapper, times(1)).toDtos(List.of(product));
    }

    @Test
    void getFilteredProductsJPQL_InvalidStatus() {
        assertThrows(BadRequestException.class, () -> productService.getFilteredProductsJPQL("title", 100, "city", "author", "INVALID_STATUS"));
    }

    @Test
    void getAllProducts_Success() {

        when(productRepository.findAll()).thenReturn(List.of(product));
        when(productMapper.toDtos(List.of(product))).thenReturn(List.of(getProductDto));

        List<GetProductDto> result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(productRepository, times(1)).findAll();
        verify(productMapper, times(1)).toDtos(List.of(product));
    }

    @Test
    void createProduct_Success() {

        when(productMapper.toEntity(createProductDto)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(getProductDto);

        GetProductDto result = productService.createProduct(createProductDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(productMapper, times(1)).toEntity(createProductDto);
        verify(productRepository, times(1)).save(product);
        verify(productMapper, times(1)).toDto(product);
    }

    @Test
    void updateProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(getProductDto);

        GetProductDto result = productService.updateProduct(1L, createProductDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(productRepository, times(1)).findById(1L);
        verify(productMapper, times(1)).updateProductFromDto(createProductDto, product);
        verify(productRepository, times(1)).save(product);
        verify(productMapper, times(1)).toDto(product);
    }

    @Test
    void updateProduct_ProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(1L, createProductDto));
        verify(productRepository, times(1)).findById(1L);
        verify(productMapper, never()).updateProductFromDto(any(), any());
        verify(productRepository, never()).save(any());
        verify(productCache, never()).put(anyLong(), any());
    }
}