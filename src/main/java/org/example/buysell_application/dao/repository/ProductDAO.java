package org.example.buysell_application.dao.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.example.buysell_application.dao.entityes.Product;
import org.springframework.stereotype.Repository;

@Repository
public class ProductDAO {

    private final List<Product> productStorage = new ArrayList<>();
    private long idCounter;

    public ProductDAO() {
        idCounter = 1;
        productStorage.add(new Product(idCounter++, "Yogurt",
            "Description 1", 100, "New York", "Author A"));
        productStorage.add(new Product(idCounter++, "Yogurt",
            "Description 2", 200, "Los Angeles", "Author B"));
        productStorage.add(new Product(idCounter++, "Product 3",
            "Description 3", 300, "Chicago", "Author C"));
    }

    public Optional<Product> findById(long id) {
        return productStorage.stream()
            .filter(product -> product.getId() == id)
            .findFirst();
    }

    public List<Product> findAll() {
        return new ArrayList<>(productStorage);
    }
}


