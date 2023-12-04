package Source.Repositories;

import Source.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<Object> findByUsername(String username);

    @Query(value = "SELECT * FROM user WHERE email = ?1", nativeQuery = true)
    User findByEmail(String email);
}
