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

    @Query(value = "SELECT p.* FROM products p " +
        "WHERE (:title IS NULL OR p.title ILIKE '%' || :title || '%') " +
        "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
        "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
        "AND (:city IS NULL OR p.city ILIKE '%' || :city || '%') " +
        "AND (:author IS NULL OR p.author ILIKE '%' || :author || '%')",
        nativeQuery = true)
    List<Product> findByPriceRange(
        @Param("title") String title,
        @Param("minPrice") Double minPrice,
        @Param("maxPrice") Double maxPrice,
        @Param("city") String city,
        @Param("author") String author
    );
}



