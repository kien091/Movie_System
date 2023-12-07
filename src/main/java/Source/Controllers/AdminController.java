package Source.Controllers;

import Source.Models.Episode;
import Source.Models.Movie;
import Source.Services.EpisodeService;
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
    private final EpisodeService episodeService;
    @Autowired
    public AdminController(MovieService movieService,
                           EpisodeService episodeService) {
        this.movieService = movieService;
        this.episodeService = episodeService;
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
        model.addAttribute("page", "movie");
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
                .sorted(Comparator.comparing(Movie::getMovieId).reversed())
                .toList();

        if (!movieName.isEmpty()){
            movies = movies.stream()
                    .filter(movie -> movie.getTitle().toLowerCase().contains(movieName.toLowerCase()))
                    .toList();
        }

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
        model.addAttribute("dividerPage", "filter");
        model.addAttribute("movieNameFilter", movieName);
        model.addAttribute("genreFilter", genre);
        model.addAttribute("yearFilter", year);
        return viewAdmin(page, size, model, session);
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
    public String handleEditMovie(@RequestParam("id") String movieId,
                                  @RequestParam("title") String title,
                                  @RequestParam("description") String description,
                                  @RequestParam("genre") String genre,
                                  @RequestParam("episode") int episode,
                                  @RequestParam("director") String director,
                                  @RequestParam("poster") String poster,
                                  @RequestParam("trailer") String trailer,
                                  @RequestParam("nation") String nation,
                                  @RequestParam("date") String releaseDate,
                                  Model model, HttpSession session){
        if(session.getAttribute("user") == null){
            return "redirect:/";
        }

        Movie movie = movieService.findById(Integer.parseInt(movieId));
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

    @RequestMapping("/delete")
    public String deleteMovie(@RequestParam("id") int movieId,
                              Model model, HttpSession session){
        if(session.getAttribute("user") == null){
            return "redirect:/";
        }

        Movie movie = movieService.findById(movieId);
        if(movie != null){
            movieService.delete(movie);
        }
        return viewAdmin(0, 7, model, session);
    }

    @RequestMapping("/episode")
    public String viewEpisode(@RequestParam(name = "page", defaultValue = "0") int page,
                              @RequestParam(name = "size", defaultValue = "7") int size,
                              Model model, HttpSession session){
        if(session.getAttribute("user") == null){
            return "redirect:/";
        }

        List<Episode> episodes;
        if (model.getAttribute("episodes") == null){
            episodes = episodeService.findAll()
                    .stream()
                    .sorted(Comparator.comparing(Episode::getEpisodeId).reversed())
                    .toList();
            model.addAttribute("dividerPage", "");
        }else {
            episodes = (List<Episode>) model.getAttribute("episodes");
        }

        Pageable pageable = PageRequest.of(page, size);
        assert episodes != null;
        Page<Episode> episodePage = episodeService.pageableEpisodes(episodes, pageable);

        model.addAttribute("episodes", episodePage.getContent());
        model.addAttribute("episodePage", episodePage);
        model.addAttribute("movies", movieService.findAll());
        model.addAttribute("page", "episode");

        return "admin";
    }

    @RequestMapping("/episode/filter")
    public String filterEpisodeBy(@RequestParam("movie") String movie,
                                  @RequestParam(name = "page", defaultValue = "0") int page,
                                  @RequestParam(name = "size", defaultValue = "7") int size,
                                  Model model, HttpSession session){
        List<Episode> episodes = episodeService.findAll();

        if(!movie.equals("Tất cả")){
            episodes = episodes.stream()
                    .filter(episode -> episode.getMovie().getTitle().equals(movie))
                    .toList();
        }

        model.addAttribute("episodes", episodes);
        model.addAttribute("dividerPage", "filter");
        model.addAttribute("movieFilter", movie);
        return viewEpisode(page, size, model, session);
    }

    @RequestMapping("/episode/edit")
    public String editEpisode(@RequestParam("id") int episodeId,
                              Model model, HttpSession session){
        if(session.getAttribute("user") == null){
            return "redirect:/";
        }

        Episode episode = episodeService.findById(episodeId);
        model.addAttribute("episodeEdit", episode);
        model.addAttribute("showEpisodeForm", true);
        model.addAttribute("movies", movieService.findAll());
        return viewEpisode(0, 7, model, session);
    }

    @RequestMapping("/episode/handle")
    public String handleEditEpisode(@RequestParam("id") String episodeId,
                                    @RequestParam("episodeName") String episodeName,
                                    @RequestParam("episodeFilm") String episodeFilm,
                                    @RequestParam("duration") int duration,
                                    @RequestParam("movie") String movieTitle,
                                    Model model, HttpSession session){
        if(session.getAttribute("user") == null){
            return "redirect:/";
        }

        Episode episode = episodeService.findById(Integer.parseInt(episodeId));
        Movie movie = movieService.findAll()
                .stream()
                .filter(m -> m.getTitle().equals(movieTitle))
                .findFirst()
                .orElse(null);
        if(episode != null){
            episode.setEpisodeName(episodeName);
            episode.setEpisodeFilm(episodeFilm);
            episode.setDuration(duration);
            episode.setMovie(movie);
            episodeService.update(episode);
        }else {
            Episode newEpisode = new Episode(0, episodeName, episodeFilm, duration, 0, movie);
            episodeService.save(newEpisode);
        }

        return viewEpisode(0, 7, model, session);
    }
}
