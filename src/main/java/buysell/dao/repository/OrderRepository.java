package buysell.dao.repository;

import buysell.dao.entityes.Order;
import buysell.dao.entityes.User;
import io.micrometer.common.lang.NonNull;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"products"})
    @NonNull
    @Override
    List<Order> findAll();

    Optional<Order> findWithProductsById(Long id);

    List<Order> findByUser(User user);
}

