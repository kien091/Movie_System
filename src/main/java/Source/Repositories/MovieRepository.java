package Source.Repositories;

import Source.Models.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {
    @Query(value = "SELECT * FROM movie WHERE lower(genre) like %?1%", nativeQuery = true)
    List<Movie> findMoviesByGenre(String genre);

    @Query(value = "SELECT * FROM movie ORDER BY total_view DESC LIMIT 16", nativeQuery = true)
    List<Movie> findTop16ByOrderByTotalViewDesc();
}
