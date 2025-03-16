package buysell.controllers;

import buysell.dao.create.CreateProductDto;
import buysell.dao.get.GetProductDto;
import buysell.services.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{id}")
    public GetProductDto getProductById(@PathVariable long id) {
        return productService.getProductById(id);
    }

    @GetMapping("/all")
    public List<GetProductDto> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping
    public List<GetProductDto> getProducts(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) Integer price,
        @RequestParam(required = false) String city,
        @RequestParam(required = false) String author,
        @RequestParam(required = false) String orderStatus
    ) {
        return productService.getFilteredProducts(title, price, city, author, orderStatus);
    }

    @PostMapping
    public GetProductDto createProduct(@RequestBody CreateProductDto productDto) {
        return productService.createProduct(productDto);
    }

    @PutMapping("/{id}")
    public GetProductDto updateProduct(@PathVariable Long id,
                                       @RequestBody CreateProductDto productDto) {
        return productService.updateProduct(id, productDto);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

}






