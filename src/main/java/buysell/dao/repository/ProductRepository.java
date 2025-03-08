package buysell.dao.repository;

import buysell.dao.entityes.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE "
        + "(:title IS NULL OR p.title ILIKE :title) AND "
        + "(:price IS NULL OR p.price = :price) AND "
        + "(:city IS NULL OR p.city ILIKE :city) AND "
        + "(:author IS NULL OR p.author ILIKE :author)")
    List<Product> findByFilters(
        @Param("title") String title,
        @Param("price") Integer price,
        @Param("city") String city,
        @Param("author") String author);
}



