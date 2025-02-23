package buysell.dao.repository;

import buysell.dao.entityes.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> getUsersById(Long id);

    List<User> id(Long id);

    boolean findByEmail(String email);

    boolean existsByEmail(String email);
}
