package org.example.buysell_application.dao.repository;

import java.util.List;
import org.example.buysell_application.dao.entityes.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> getUsersById(Long id);

    List<User> id(Long id);

    boolean findByEmail(String email);
}
