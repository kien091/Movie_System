package Source.Services;

import Source.Models.Movie;
import Source.Repositories.MovieRepository;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@SuppressWarnings("deprecation")
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

    public List<String> getAllGenres(){
        List<String> genres = movieRepository.getAllGenres();
        List<String> handleGenres = new ArrayList<>();

        for (String genre : genres) {
            String[] splitGenres = genre.split("[-,]");
            for(String genreString : splitGenres)
                handleGenres.add(WordUtils.capitalize(genreString.trim()));
        }

        return new ArrayList<>(new LinkedHashSet<>(handleGenres));
    }

    public List<String> getAllReleaseDate(){
        Set<String> year = new HashSet<>();
        for(String yearString : movieRepository.getAllReleaseDate()){
            String yearNumber = getYearFromDateString(yearString);
            year.add(yearNumber);
        }

        return new ArrayList<>(year);
    }

    private String getYearFromDateString(String dateString) {
        if (dateString.matches("\\d{4}")) {
            return dateString;
        } else if (dateString.matches("\\d{1,2}/\\d{1,2}/\\d{4}")) {
            String[] parts = dateString.split("/");
            return parts[2];
        } else {
            return LocalDateTime.now().getYear() + "";
        }
    }

    public List<Movie> top6NewestMovies(){
        return movieRepository.findTop6MoviesByOrderByMovieIdDesc();
    }

    public Page<Movie> filterMoviesByCategory(String category, Pageable pageable) {
        List<Movie> movies = new ArrayList<>();

        switch (category) {
            case "series":
                movies = movieRepository.findMoviesSeries();
                break;
            case "feature-film":
                movies = movieRepository.findFeatureMovies();;
                break;
            case "complete":
                movies = movieRepository.findMoviesComplete();
                break;
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), movies.size());
        return new PageImpl<>(movies.subList(start, end), pageable, movies.size());
    }
}
