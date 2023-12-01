package Source.Repositories;

import Source.Models.Episode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EpisodeRepository extends JpaRepository<Episode, Integer> {
    @Query(value = "SELECT * FROM episode WHERE e.movie.movieId = ?1", nativeQuery = true)
    List<Episode> findAllEpisodeByMovieId(int movieId);
}
