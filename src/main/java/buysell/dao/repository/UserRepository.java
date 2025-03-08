package buysell.dao.repository;

import buysell.dao.entityes.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> id(Long id);

    boolean existsByEmail(String email);
}
