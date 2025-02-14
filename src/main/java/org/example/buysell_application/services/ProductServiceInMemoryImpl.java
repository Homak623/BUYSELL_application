package org.example.buysell_application.services;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.buysell_application.dao.entityes.Product;
import org.example.buysell_application.dao.repository.ProductDAO;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceInMemoryImpl implements ProductService {

    private final ProductDAO productDAO;

    public Product getProductById(long id) {
        return productDAO.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Product with id " + id + " not found"));
    }

    public List<Product> getFilteredProducts(String title, Integer price,
                                             String city, String author) {
        List<Product> allProducts = productDAO.findAll();
        return allProducts.stream()
            .filter(product -> title == null || product.getTitle().equalsIgnoreCase(title))
            .filter(product -> price == null || product.getPrice().equals(price))
            .filter(product -> city == null || product.getCity().equalsIgnoreCase(city))
            .filter(product -> author == null || product.getAuthor().equalsIgnoreCase(author))
            .collect(Collectors.toList());
    }
}



