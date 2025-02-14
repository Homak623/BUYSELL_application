package org.example.buysell_application.services;

import java.util.List;
import org.example.buysell_application.dao.entityes.Product;

public interface ProductService {
    Product getProductById(long id);

    List<Product> getFilteredProducts(String title, Integer price,
                                      String city, String author);
}

