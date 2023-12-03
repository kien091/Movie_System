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

    @Query(value = "SELECT DISTINCT lower(genre) FROM movie", nativeQuery = true)
    List<String> getAllGenres();

    @Query(value = "SELECT DISTINCT release_date FROM movie", nativeQuery = true)
    List<String> getAllReleaseDate();

    @Query(value = "SELECT DISTINCT lower(nation) FROM movie", nativeQuery = true)
    List<String> getAllNation();

    @Query(value = "SELECT * FROM movie ORDER BY movie_id DESC LIMIT 6", nativeQuery = true)
    List<Movie> findTop6MoviesByOrderByMovieIdDesc();

    @Query(value = "SELECT * FROM movie WHERE total_episode > 1", nativeQuery = true)
    List<Movie> findMoviesSeries();

    @Query(value = "SELECT * FROM movie WHERE total_episode = 1", nativeQuery = true)
    List<Movie> findFeatureMovies();

    @Query(value = "SELECT * FROM movie m WHERE m.total_episode = " +
            "(SELECT COUNT(e.episode_id) FROM episode e WHERE e.movie_id = m.movie_id)", nativeQuery = true)
    List<Movie> findMoviesComplete();
}
