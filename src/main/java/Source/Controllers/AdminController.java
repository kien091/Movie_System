package Source.Controllers;

import Source.Models.Movie;
import Source.Services.MovieService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;

@SuppressWarnings("unchecked")
@Controller
@RequestMapping("/admin")
public class AdminController {
    private final MovieService movieService;

    @Autowired
    public AdminController(MovieService movieService) {
        this.movieService = movieService;
    }

    @RequestMapping("")
    public String viewAdmin(@RequestParam(name = "page", defaultValue = "0") int page,
                            @RequestParam(name = "size", defaultValue = "7") int size,
                            Model model, HttpSession session){
        if(session.getAttribute("user") == null){
            return "redirect:/";
        }

        List<Movie> movies;
        if(model.getAttribute("movies") == null){
            movies = movieService.findAll()
                    .stream()
                    .sorted(Comparator.comparing(Movie::getMovieId).reversed())
                    .toList();
            model.addAttribute("dividerPage", "");
        }else {
            movies = (List<Movie>) model.getAttribute("movies");
        }

        Pageable pageable = PageRequest.of(page, size);
        assert movies != null;
        Page<Movie> moviePage = movieService.pageableMovies(movies, pageable);

        model.addAttribute("movies", moviePage.getContent());
        model.addAttribute("moviePage", moviePage);
        model.addAttribute("genres", movieService.getAllGenres());
        model.addAttribute("years", movieService.getAllReleaseDate());
        return "admin";
    }

    @RequestMapping("/filter")
    public String filterBy(@RequestParam("movie_name") String movieName,
                           @RequestParam("genre") String genre,
                           @RequestParam("year") String year,
                           @RequestParam(name = "page", defaultValue = "0") int page,
                           @RequestParam(name = "size", defaultValue = "7") int size,
                           Model model, HttpSession session){
        List<Movie> movies = movieService.findAll()
                .stream()
                .filter(movie -> movie.getTitle().toLowerCase().contains(movieName.toLowerCase()))
                .toList();

        if(!genre.equals("Tất cả")){
            movies = movies.stream()
                    .filter(movie -> movie.getGenre().toLowerCase().contains(genre.toLowerCase()))
                    .toList();
        }

        if(!year.equals("Tất cả")){
            movies = movies.stream()
                    .filter(movie -> movie.getReleaseDate().contains(year))
                    .toList();
        }

        model.addAttribute("movies", movies);
        model.addAttribute("dividerPage", "/filter");
        model.addAttribute("movieNameFilter", movieName);
        model.addAttribute("genreFilter", genre);
        model.addAttribute("yearFilter", year);
        return viewAdmin(page, size, model, session);
    }

    @RequestMapping("/add")
    public String addMovie(@RequestParam("title") String title,
                           @RequestParam("description") String description,
                           @RequestParam("genre") String genre,
                           @RequestParam("episode") int episode,
                           @RequestParam("director") String director,
                           @RequestParam("poster") String poster,
                           @RequestParam("trailer") String trailer,
                           @RequestParam("nation") String nation,
                           @RequestParam("release_date") String releaseDate,
                           Model model, HttpSession session){
        if(session.getAttribute("user") == null){
            return "redirect:/";
        }

        Movie movie = new Movie(title, description, genre,
                        episode, director, poster, trailer,
                        nation, releaseDate, 0, 0);
        movieService.save(movie);
        return viewAdmin(0, 7, model, session);
    }

    @RequestMapping("/edit")
    public String editMovie(@RequestParam("id") int movieId,
                            Model model, HttpSession session){
        if(session.getAttribute("user") == null){
            return "redirect:/";
        }

        Movie movie = movieService.findById(movieId);
        model.addAttribute("movieEdit", movie);
        model.addAttribute("showMovieForm", true);
        return viewAdmin(0, 7, model, session);
    }

    @RequestMapping("/handle")
    public String handleEditMovie(@RequestParam("id") int movieId,
                                  @RequestParam("title") String title,
                                  @RequestParam("description") String description,
                                  @RequestParam("genre") String genre,
                                  @RequestParam("episode") int episode,
                                  @RequestParam("director") String director,
                                  @RequestParam("poster") String poster,
                                  @RequestParam("trailer") String trailer,
                                  @RequestParam("nation") String nation,
                                  @RequestParam("release_date") String releaseDate,
                                  Model model, HttpSession session){
        if(session.getAttribute("user") == null){
            return "redirect:/";
        }

        Movie movie = movieService.findById(movieId);
        if(movie != null){
            movie.setTitle(title);
            movie.setDescription(description);
            movie.setGenre(genre);
            movie.setTotalEpisode(episode);
            movie.setDirector(director);
            movie.setPoster(poster);
            movie.setTrailer(trailer);
            movie.setNation(nation);
            movie.setReleaseDate(releaseDate);
            movieService.update(movie);
        }else {
            Movie newMovie = new Movie(title, description, genre,
                    episode, director, poster, trailer,
                    nation, releaseDate, 0, 0);
            movieService.save(newMovie);
        }

        return viewAdmin(0, 7, model, session);
    }
}
