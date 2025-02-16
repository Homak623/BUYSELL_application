package org.example.buysell_application.controllers;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.buysell_application.dao.dto.ProductDto;
import org.example.buysell_application.dao.entityes.Product;
import org.example.buysell_application.services.ProductServiceInDataBase;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductServiceInDataBase productService;

    @GetMapping("/{id}")
    public ProductDto getProductById(@PathVariable long id) {
        return productService.getProductById(id);
    }

    @GetMapping
    public List<ProductDto> getProducts(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) Integer price,
        @RequestParam(required = false) String city,
        @RequestParam(required = false) String author) {
        return productService.getFilteredProducts(title, price, city, author);
    }

    @PostMapping("/create")
    public ProductDto createProduct(@RequestBody ProductDto productDto) {
        return productService.createProduct(productDto);
    }

    @PutMapping("/{id}")
    public ProductDto updateProduct(@PathVariable Long id, @RequestBody ProductDto productDto) {
        return productService.updateProduct(id, productDto);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable long id) {
        productService.deleteProduct(id);
    }
}




