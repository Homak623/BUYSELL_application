package buysell.controllers;

import buysell.dao.dto.create.CreateProductDto;
import buysell.dao.dto.get.GetProductDto;
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

    @GetMapping
    public List<GetProductDto> getProducts(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) Integer price,
        @RequestParam(required = false) String city,
        @RequestParam(required = false) String author) {
        return productService.getFilteredProducts(title, price, city, author);
    }

    @PostMapping("/create")
    public GetProductDto createProduct(@RequestBody CreateProductDto productDto) {
        return productService.createProduct(productDto);
    }

    @PutMapping("/{id}")
    public GetProductDto updateProduct(@PathVariable Long id,
                                       @RequestBody CreateProductDto productDto) {
        return productService.updateProduct(id, productDto);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable long id) {
        productService.deleteProduct(id);
    }
}






