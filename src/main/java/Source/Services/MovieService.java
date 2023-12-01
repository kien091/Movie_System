package Source.Services;

import Source.Models.Movie;
import Source.Repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {
    private final MovieRepository movieRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository){
        this.movieRepository = movieRepository;
    }

    public Movie save(Movie movie){
        return movieRepository.save(movie);
    }

    public List<Movie> findAll(){
        return movieRepository.findAll();
    }

    public Movie findById(int id){
        return movieRepository.findById(id).orElse(null);
    }

    public void update(Movie movie){
        Movie m = movieRepository.findById(movie.getMovieId())
                .orElse(null);
        if(m != null){
            m.setTitle(movie.getTitle());
            m.setDescription(movie.getDescription());
            m.setGenre(movie.getGenre());
            m.setTotalEpisode(movie.getTotalEpisode());
            m.setDirector(movie.getDirector());
            m.setPoster(movie.getPoster());
            m.setTrailer(movie.getTrailer());
            m.setNation(movie.getNation());
            m.setReleaseDate(movie.getReleaseDate());
            m.setRating(movie.getRating());
            m.setTotalView(movie.getTotalView());

            m.setWatchHistories(movie.getWatchHistories());
            m.setReviews(movie.getReviews());
            m.setFavorites(movie.getFavorites());
            m.setEpisodes(movie.getEpisodes());

            m.setActors(movie.getActors());

            movieRepository.save(m);
        }
    }

    public void delete(Movie movie){
        movieRepository.delete(movie);
    }

    public List<Movie> findMoviesByGenre(String genre){
        return movieRepository.findMoviesByGenre(genre);
    }
    public List<Movie> findTop16ByOrderByTotalViewDesc(){
        return movieRepository.findTop16ByOrderByTotalViewDesc();
    }
}
