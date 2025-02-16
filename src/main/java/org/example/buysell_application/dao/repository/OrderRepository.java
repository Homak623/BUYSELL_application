package org.example.buysell_application.dao.repository;

import java.util.List;
import org.example.buysell_application.dao.entityes.Order;
import org.example.buysell_application.dao.entityes.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);
}

