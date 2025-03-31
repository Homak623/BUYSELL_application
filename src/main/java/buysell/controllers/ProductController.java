package buysell.controllers;

import buysell.dao.create.CreateProductDto;
import buysell.dao.get.GetProductDto;
import buysell.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Product Controller", description = "API для управления товарами")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{id}")
    @Operation(summary = "Получить товар по ID",
        description = "Возвращает товар по его идентификатору")
    @ApiResponse(responseCode = "200", description = "Товар успешно найден")
    @ApiResponse(responseCode = "404", description = "Товар не найден")
    public GetProductDto getProductById(@PathVariable @Min(1) long id) {
        return productService.getProductById(id);
    }

    @GetMapping("/all")
    @Operation(summary = "Получить все товары", description = "Возвращает список всех товаров")
    @ApiResponse(responseCode = "200", description = "Список товаров успешно получен")
    public List<GetProductDto> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping
    @Operation(summary = "Фильтрация товаров",
        description = "Возвращает список товаров, отфильтрованных по параметрам")
    @ApiResponse(responseCode = "200", description = "Список товаров успешно отфильтрован")
    public List<GetProductDto> getProducts(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) @Min(0) Integer price,
        @RequestParam(required = false) String city,
        @RequestParam(required = false) String author,
        @RequestParam(required = false) String orderStatus
    ) {
        return productService.getFilteredProducts(title, price, city, author, orderStatus);
    }

    @PostMapping
    @Operation(summary = "Создать товар", description = "Создает новый товар")
    @ApiResponse(responseCode = "200", description = "Товар успешно создан")
    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
    public GetProductDto createProduct(@Valid @RequestBody CreateProductDto productDto) {
        return productService.createProduct(productDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить товар", description = "Обновляет товар по его идентификатору")
    @ApiResponse(responseCode = "200", description = "Товар успешно обновлен")
    @ApiResponse(responseCode = "404", description = "Товар не найден")
    public GetProductDto updateProduct(
        @PathVariable @Min(1) Long id,
        @Valid @RequestBody CreateProductDto productDto) {
        return productService.updateProduct(id, productDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить товар", description = "Удаляет товар по его идентификатору")
    @ApiResponse(responseCode = "200", description = "Товар успешно удален")
    @ApiResponse(responseCode = "404", description = "Товар не найден")
    public void deleteProduct(@PathVariable @Min(1) Long id) {
        productService.deleteProduct(id);
    }

    @GetMapping("/")
    @Operation(summary = "Фильтрация товаров (JPQL)",
        description = "Возвращает список товаров, отфильтрованных по параметрам (JPQL)")
    @ApiResponse(responseCode = "200", description = "Список товаров успешно отфильтрован")
    public List<GetProductDto> getProductsJPQL(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) @Min(0) Integer price,
        @RequestParam(required = false) String city,
        @RequestParam(required = false) String author,
        @RequestParam(required = false) String orderStatus
    ) {
        return productService.getFilteredProducts(title, price, city, author, orderStatus);
    }
}






