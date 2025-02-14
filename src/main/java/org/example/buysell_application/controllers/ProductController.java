package org.example.buysell_application.controllers;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.buysell_application.dao.entityes.Product;
import org.example.buysell_application.services.ProductServiceInMemoryImpl;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductServiceInMemoryImpl productService;

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable long id) {
        return productService.getProductById(id);
    }

    @GetMapping
    public List<Product> getProducts(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) Integer price,
        @RequestParam(required = false) String city,
        @RequestParam(required = false) String author) {
        return productService.getFilteredProducts(title, price, city, author);
    }
}