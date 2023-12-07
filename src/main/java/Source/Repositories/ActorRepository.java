package Source.Repositories;

import Source.Models.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ActorRepository extends JpaRepository<Actor, Integer> {
    @Modifying
    @Query(value = "DELETE FROM action_movie WHERE actor_id = :actorId", nativeQuery = true)
    void deleteActionMovie(@Param("actorId") int actorId);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO action_movie (movie_id, actor_id) VALUES (:movieId, :actorId)", nativeQuery = true)
    void insertActionMovie(@Param("actorId") int actorId, @Param("movieId") int movieId);

    @Modifying
    @Query(value = "DELETE FROM action_movie WHERE actor_id = :actorId AND movie_id = :movieId", nativeQuery = true)
    void deleteActionMovie(@Param("actorId") int actorId, @Param("movieId") int movieId);
}
