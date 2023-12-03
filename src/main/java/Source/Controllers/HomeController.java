package Source.Controllers;

import Source.Models.Movie;
import Source.Services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
@Controller
@RequestMapping("/")
public class HomeController {
    private final MovieService movieService;
    @Autowired
    public HomeController(MovieService movieService) {
        this.movieService = movieService;
    }

    @RequestMapping("")
    public String viewHome(Model model) {
        List<Movie> movies = movieService.findAll()
                .stream()
                .filter(movie -> movie.getTotalEpisode() > 1)
                .toList();
        model.addAttribute("movies", movies);

        List<Movie> cartoon = movieService.findMoviesByGenre("Hoạt hình");
        model.addAttribute("cartoon", cartoon);

        List<Movie> favorite = movieService.findTop16ByOrderByTotalViewDesc();
        model.addAttribute("favorite", favorite);
        model.addAttribute("carousel", favorite);
        model.addAttribute("navigation", "Phim bộ");

        model.addAttribute("genres", movieService.getAllGenres());
        model.addAttribute("releaseDates", movieService.getAllReleaseDate());
        model.addAttribute("top6MoviesNewest", movieService.top6NewestMovies());
        return "home";
    }

    @RequestMapping("/filter")
    public String filterBy(@RequestParam("category") String category,
                           @RequestParam(name = "page", defaultValue = "0") int page,
                           @RequestParam(name = "size", defaultValue = "16") int size,
                           Model model){
        Pageable pageable = PageRequest.of(page, size);
        Page<Movie> moviePage = null;
        if(!category.equals("search")){
            moviePage = movieService.filterMoviesByCategory(category, pageable);
            String navigation = switch (category) {
                case "series" -> "Phim bộ";
                case "feature-film" -> "Phim lẻ";
                case "complete" -> "Phim đã hoàn thành";
                default -> category;
            };
            model.addAttribute("navigation", navigation);
        }
        else{
            List<Movie> movies = (List<Movie>) model.getAttribute("movies");
            assert movies != null;
            moviePage = new PageImpl<>(movies, pageable, movies.size());
        }

        model.addAttribute("movies", moviePage.getContent());
        model.addAttribute("moviesPage", moviePage);

        model.addAttribute("category", category);


        List<Movie> favorite = movieService.findTop16ByOrderByTotalViewDesc();
        model.addAttribute("carousel", favorite);


        model.addAttribute("genres", movieService.getAllGenres());
        model.addAttribute("releaseDates", movieService.getAllReleaseDate());
        model.addAttribute("top6MoviesNewest", movieService.top6NewestMovies());
        return "home";
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String search(@RequestParam("search") String search, Model model) {
        List<Movie> movies = movieService.findAll()
                .stream()
                .filter(movie -> movie.getTitle().toLowerCase().contains(search.toLowerCase())
                                || movie.getGenre().toLowerCase().contains(search.toLowerCase())
                                || movie.getDirector().toLowerCase().contains(search.toLowerCase())
                                || movie.getNation().toLowerCase().contains(search.toLowerCase())
                                || movie.getReleaseDate().toLowerCase().contains(search.toLowerCase())
                                || movie.getActors().stream().anyMatch(actor ->
                                        actor.getName().toLowerCase().contains(search.toLowerCase())))
                .toList();
        model.addAttribute("movies", movies);
        model.addAttribute("navigation", "Kết quả tìm kiếm: \"" + search + "\"");
        return filterBy("search", 0, 16, model);
    }

    @RequestMapping(value = "/filterBy", method = RequestMethod.GET)
    public String filterWithOption(@RequestParam("status") String status,
                                   @RequestParam("genre") String genre,
                                   @RequestParam("year") String year,
                                   @RequestParam("sort") String sort,
                                   Model model){
        List<Movie> movies = movieService.findAll();
        if(status.equals("Hoàn thành")){
            movies = movies.stream()
                    .filter(movie -> movie.getTotalEpisode() == movie.getEpisodes().size())
                    .toList();
        } else if (status.equals("Đang tiến hành")){
            movies = movies.stream()
                    .filter(movie -> movie.getTotalEpisode() > movie.getEpisodes().size())
                    .toList();
        }

        if(!genre.equals("Tất cả")){
            movies = movies.stream()
                    .filter(movie -> movie.getGenre().toLowerCase().contains(genre.toLowerCase()))
                    .toList();
        }

        if(!year.equals("Tất cả")){
            movies = movies.stream()
                    .filter(movie -> movie.getReleaseDate().toLowerCase().contains(year.toLowerCase()))
                    .toList();
        }

        if(sort.equals("Lượt xem")){
            movies = movies.stream()
                    .sorted((movie1, movie2) -> movie2.getTotalView() - movie1.getTotalView())
                    .toList();
        } else if (sort.equals("Đánh giá")){
            movies = movies.stream()
                    .sorted((movie1, movie2) -> (int) (movie2.getRating() - movie1.getRating()))
                    .toList();
        }

        model.addAttribute("movies", movies);
        model.addAttribute("navigation",
                "Kết quả tìm kiếm: \" Lọc \"");

        return filterBy("search", 0, 16, model);
    }
}
