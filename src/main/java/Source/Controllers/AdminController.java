package Source.Controllers;

import Source.Models.Actor;
import Source.Models.Episode;
import Source.Models.Movie;
import Source.Models.User;
import Source.Services.ActorService;
import Source.Services.EpisodeService;
import Source.Services.MovieService;
import Source.Services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings("unchecked")
@Controller
@RequestMapping("/admin")
public class AdminController {
    private final MovieService movieService;
    private final EpisodeService episodeService;
    private final ActorService actorService;
    private final UserService userService;
    @Autowired
    public AdminController(MovieService movieService,
                           EpisodeService episodeService,
                           ActorService actorService,
                           UserService userService) {
        this.movieService = movieService;
        this.episodeService = episodeService;
        this.actorService = actorService;
        this.userService = userService;
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

    @RequestMapping("/episode/delete")
    public String deleteEpisode(@RequestParam("id") int episodeId,
                                Model model, HttpSession session){
        if(session.getAttribute("user") == null){
            return "redirect:/";
        }

        Episode episode = episodeService.findById(episodeId);
        if(episode != null){
            episodeService.delete(episode);
        }
        return viewEpisode(0, 7, model, session);
    }

    @RequestMapping("/actor")
    public String viewActor(@RequestParam(name = "page", defaultValue = "0") int page,
                            @RequestParam(name = "size", defaultValue = "7") int size,
                            Model model, HttpSession session){
        if(session.getAttribute("user") == null){
            return "redirect:/";
        }

        List<Actor> actors;
        if(model.getAttribute("actors") == null){
            actors = actorService.findAll()
                    .stream()
                    .sorted(Comparator.comparing(Actor::getActorId).reversed())
                    .toList();
            model.addAttribute("dividerPage", "");
        }else {
            actors = (List<Actor>) model.getAttribute("actors");
        }

        Pageable pageable = PageRequest.of(page, size);
        assert actors != null;
        Page<Actor> actorPage = actorService.pageableActors(actors, pageable);

        model.addAttribute("actors", actorPage.getContent());
        model.addAttribute("actorPage", actorPage);
        model.addAttribute("page", "actor");
        if(model.getAttribute("movies") == null){
            model.addAttribute("movies", movieService.findAll());
        }
        return "admin";
    }

    @RequestMapping("/actor/filter")
    public String filterActorBy(@RequestParam("actor_name") String actorName,
                                @RequestParam("movie") String movie,
                                @RequestParam(name = "page", defaultValue = "0") int page,
                                @RequestParam(name = "size", defaultValue = "7") int size,
                                Model model, HttpSession session){
        List<Actor> actors = actorService.findAll();

        if(!actorName.isEmpty()){
            actors = actors.stream()
                    .filter(actor -> actor.getName().toLowerCase().contains(actorName.toLowerCase()))
                    .toList();
        }

        if(!movie.equals("Tất cả")){
            actors = actors.stream()
                    .filter(actor -> actor.getMovies()
                            .stream()
                            .anyMatch(m -> m.getTitle().equals(movie)))
                    .toList();
        }

        model.addAttribute("actors", actors);
        model.addAttribute("dividerPage", "filter");
        model.addAttribute("actorNameFilter", actorName);
        model.addAttribute("movieFilter", movie);
        return viewActor(page, size, model, session);
    }

    @RequestMapping("/actor/edit")
    public String editActor(@RequestParam("id") int actorId,
                            Model model, HttpSession session){
        if(session.getAttribute("user") == null){
            return "redirect:/";
        }

        Actor actor = actorService.findById(actorId);
        model.addAttribute("actorEdit", actor);
        model.addAttribute("showActorForm", true);
        model.addAttribute("movies", movieService.findAll());
        return viewActor(0, 7, model, session);
    }

    @RequestMapping("/actor/handle")
    public String handleEditActor(@RequestParam("id") String actorId,
                                  @RequestParam("actorName") String actorName,
                                  @RequestParam("biography") String biography,
                                  Model model, HttpSession session){
        if(session.getAttribute("user") == null){
            return "redirect:/";
        }

        Actor actor = actorService.findById(Integer.parseInt(actorId));
        if(actor != null){
            actor.setName(actorName);
            actor.setBiography(biography);
            actorService.update(actor);
        }else {
            Actor newActor = new Actor(0, actorName, biography, new ArrayList<>());
            actorService.save(newActor);
        }

        return viewActor(0, 7, model, session);
    }

    @RequestMapping("/actor/delete")
    public String deleteActor(@RequestParam("id") int actorId,
                              Model model, HttpSession session){
        if(session.getAttribute("user") == null){
            return "redirect:/";
        }

        Actor actor = actorService.findById(actorId);
        if(actor != null){
            actorService.delete(actor);
        }
        return viewActor(0, 7, model, session);
    }

    @RequestMapping("/actor/movie")
    public String addMovieToActor(@RequestParam("id") int actorId,
                                  @RequestParam(name = "page", defaultValue = "0") int page,
                                  @RequestParam(name = "size", defaultValue = "7") int size,
                                  Model model, HttpSession session){
        if(session.getAttribute("user") == null){
            return "redirect:/";
        }

        Actor actor = actorService.findById(actorId);

        List<Movie> movies;
        if(model.getAttribute("movies") == null) {
            movies = movieService.findAll()
                    .stream()
                    .filter(movie -> movie.getActors().contains(actor))
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
        model.addAttribute("page", "actor-movie");
        model.addAttribute("actor", actor);

        movies = movieService.findAll()
                .stream()
                .filter(movie -> !movie.getActors().contains(actor))
                .toList();
        model.addAttribute("moviesNotInActor", movies);

        return "admin";
    }

    @RequestMapping("/actor/movie/filter")
    public String filterMovieByActor(@RequestParam("id") int actorId,
                                     @RequestParam("movie") String movie,
                                     @RequestParam(name = "page", defaultValue = "0") int page,
                                     @RequestParam(name = "size", defaultValue = "7") int size,
                                     Model model, HttpSession session){
        List<Movie> movies = movieService.findAll()
                .stream()
                .filter(m -> m.getActors()
                        .stream()
                        .anyMatch(actor -> actor.getActorId() == actorId)
                        && m.getTitle().toLowerCase().contains(movie.toLowerCase()))
                .toList();

        model.addAttribute("movies", movies);
        model.addAttribute("dividerPage", "filter");
        model.addAttribute("actorIdFilter", actorId);
        model.addAttribute("movieFilter", movie);
        return addMovieToActor(actorId, page, size, model, session);
    }

    @RequestMapping("/actor/movie/handle")
    public String handleEditMovieToActor(@RequestParam("id") int actorId,
                                         @RequestParam("actorName") String actorName,
                                         @RequestParam("movie") String movieTitle,
                                         Model model, HttpSession session){
        if(session.getAttribute("user") == null){
            return "redirect:/";
        }

        Movie movie = movieService.findAll()
                .stream()
                .filter(m -> m.getTitle().equals(movieTitle))
                .findFirst()
                .orElse(null);

        assert movie != null;
        actorService.insertActionMovie(actorId, movie.getMovieId());
        return addMovieToActor(actorId, 0, 7, model, session);
    }

    @RequestMapping("/actor/movie/delete")
    public String deleteMovieFromActor(@RequestParam("actor_id") int actorId,
                                       @RequestParam("movie_id") int movieId,
                                       Model model, HttpSession session){
        if(session.getAttribute("user") == null){
            return "redirect:/";
        }

        actorService.deleteActionMovie(actorId, movieId);
        return addMovieToActor(actorId, 0, 7, model, session);
    }

    @RequestMapping("/user")
    public String viewUser(@RequestParam(name = "page", defaultValue = "0") int page,
                           @RequestParam(name = "size", defaultValue = "7") int size,
                           Model model, HttpSession session){
        if(session.getAttribute("user") == null){
            return "redirect:/";
        }

        List<User> users;
        if(model.getAttribute("users") == null){
            users = userService.findAll()
                    .stream()
                    .filter(user -> !user.getRole().equals("admin"))
                    .sorted(Comparator.comparing(User::getUserId).reversed())
                    .toList();
            model.addAttribute("dividerPage", "");
        }else {
            users = (List<User>) model.getAttribute("users");
            assert users != null;
            users = users.stream()
                    .filter(user -> !user.getRole().equals("admin"))
                    .toList();
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userService.pageableUsers(users, pageable);

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("userPage", userPage);
        model.addAttribute("page", "user");
        return "admin";
    }

    @RequestMapping("/user/status")
    public String changeUserStatus(@RequestParam("id") int userId,
                                   Model model, HttpSession session){
        if(session.getAttribute("user") == null){
            return "redirect:/";
        }

        User user = userService.findById(userId);
        if(user != null){
            user.setStatus(!user.isStatus());
            userService.update(user);
        }
        return viewUser(0, 7, model, session);
    }

    @RequestMapping("/user/role")
    public String changeUserRole(@RequestParam("id") int userId,
                                 Model model, HttpSession session){
        if(session.getAttribute("user") == null){
            return "redirect:/";
        }

        User user = userService.findById(userId);
        if(user != null){
            user.setRole(user.getRole().equals("user") ? "admin" : "user");
            userService.update(user);
        }
        return viewUser(0, 7, model, session);
    }

    @RequestMapping("/user/search")
    public String searchUser(@RequestParam("search") String search,
                             @RequestParam(name = "page", defaultValue = "0") int page,
                             @RequestParam(name = "size", defaultValue = "7") int size,
                             Model model, HttpSession session){
        List<User> users = userService.findAll()
                .stream()
                .filter(user -> {
                    String username = user.getUsername();
                    String email = user.getEmail();
                    String firstName = user.getFirstName();
                    String lastName = user.getLastName();
                    String dateOfBirth = user.getDateOfBirth();
                    String gender = user.getGender();

                    return (username != null && username.toLowerCase().contains(search.toLowerCase()))
                            || (email != null && email.toLowerCase().contains(search.toLowerCase()))
                            || (firstName != null && firstName.toLowerCase().contains(search.toLowerCase()))
                            || (lastName != null && lastName.toLowerCase().contains(search.toLowerCase()))
                            || (dateOfBirth != null && dateOfBirth.toLowerCase().contains(search.toLowerCase()))
                            || (gender != null && gender.toLowerCase().contains(search.toLowerCase()));
                })
                .toList();

        model.addAttribute("users", users);
        model.addAttribute("dividerPage", "search");
        model.addAttribute("searchFilter", search);
        return viewUser(page, size, model, session);
    }
}
