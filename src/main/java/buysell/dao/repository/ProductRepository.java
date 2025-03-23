package buysell.dao.repository;

import buysell.dao.entityes.Product;
import buysell.enums.Status;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = "SELECT DISTINCT p.* FROM products p "
        + "LEFT JOIN order_products op ON p.id = op.product_id "
        + "LEFT JOIN orders o ON op.order_id = o.id "
        + "WHERE (:title IS NULL OR LOWER(p.title) LIKE LOWER('%' || :title || '%')) "
        + "AND (:price IS NULL OR p.price = :price) "
        + "AND (:city IS NULL OR LOWER(p.city) LIKE LOWER('%' || :city || '%')) "
        + "AND (:author IS NULL OR LOWER(p.author) LIKE LOWER('%' || :author || '%')) "
        + "AND (:orderStatus IS NULL OR o.status = :orderStatus)",
        nativeQuery = true)
    List<Product> findByFilters(
        @Param("title") String title,
        @Param("price") Integer price,
        @Param("city") String city,
        @Param("author") String author,
        @Param("orderStatus") String orderStatus
    );

    @Query("SELECT DISTINCT p FROM Product p "
        + "LEFT JOIN p.orders o "
        + "WHERE (:title IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%'))) "
        + "AND (:price IS NULL OR p.price = :price) "
        + "AND (:city IS NULL OR LOWER(p.city) LIKE LOWER(CONCAT('%', :city, '%'))) "
        + "AND (:author IS NULL OR LOWER(p.author) LIKE LOWER(CONCAT('%', :author, '%'))) "
        + "AND (:orderStatus IS NULL OR o.status = :orderStatus OR :orderStatus IS NULL)")
    List<Product> findByFiltersJPQL(
        @Param("title") String title,
        @Param("price") Integer price,
        @Param("city") String city,
        @Param("author") String author,
        @Param("orderStatus") Status orderStatus
    );

}



