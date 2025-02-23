package buysell.dao.repository;

import buysell.dao.entityes.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Iterable<Product> findByTitleIgnoreCaseAndPriceAndCityIgnoreCaseAndAuthorIgnoreCase(
        String title, Integer price, String city, String author);
}



