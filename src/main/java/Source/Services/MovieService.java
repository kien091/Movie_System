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
import java.util.stream.Collectors;

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
        return genres.stream()
                .flatMap(genre -> Arrays.stream(genre.split("[-,]")))
                .map(genreString -> WordUtils.capitalize(genreString.trim()))
                .distinct()
                .collect(Collectors.toList());
    }

    public List<String> getAllReleaseDate(){
        Set<String> year = new HashSet<>();
        for(String yearString : movieRepository.getAllReleaseDate()){
            String yearNumber = getYearFromDateString(yearString);
            year.add(yearNumber);
        }

        return new ArrayList<>(year);
    }

    public List<String> getAllNation(){
        List<String> nations = movieRepository.getAllNation();
        List<String> handleNations = new ArrayList<>();

        for (String nation : nations) {
            handleNations.add(WordUtils.capitalize(nation.trim()));
        }

        return new ArrayList<>(new LinkedHashSet<>(handleNations));
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
        List<Movie> movies;
        if(getAllGenres().contains(category)){
            movies = movieRepository.findMoviesByGenre(category);
        } else if (getAllReleaseDate().contains(category)) {
            movies = movieRepository.findAll()
                    .stream()
                    .filter(movie -> getYearFromDateString(movie.getReleaseDate()).equals(category))
                    .toList();
        } else if (getAllNation().contains(category)) {
            movies = movieRepository.findAll()
                    .stream()
                    .filter(movie -> movie.getNation().toLowerCase().contains(category.toLowerCase()))
                    .toList();

        } else {
            movies = switch (category) {
                case "series" -> movieRepository.findMoviesSeries();
                case "feature-film" -> movieRepository.findFeatureMovies();
                case "complete" -> movieRepository.findMoviesComplete();
                case "english-language-films" -> movieRepository.findAll()
                        .stream()
                        .filter(movie -> movie.getGenre().toLowerCase().contains("chiếu rạp"))
                        .toList();
                case "favorite" -> {
                    List<Movie> favorite = movieRepository.findAll()
                            .stream()
                            .filter(movie -> movie.getTotalView() > 2000)
                            .toList();

                    yield favorite.size() > 16 ? favorite : movieRepository.findTop16ByOrderByTotalViewDesc();
                }
                default -> movieRepository.findAll();
            };
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), movies.size());
        return new PageImpl<>(movies.subList(start, end), pageable, movies.size());
    }

    public Page<Movie> pageableMovies(List<Movie> movies, Pageable pageable){
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), movies.size());
        return new PageImpl<>(movies.subList(start, end), pageable, movies.size());
    }
}
