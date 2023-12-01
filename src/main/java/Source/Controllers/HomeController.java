package Source.Controllers;

import Source.Models.Movie;
import Source.Services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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
        List<Movie> SeriesMovies = movieService.findAll()
                .stream()
                .filter(movie -> movie.getTotalEpisode() > 1)
                .toList();
        model.addAttribute("seriesMovie", SeriesMovies);

        List<Movie> cartoon = movieService.findMoviesByGenre("Hoạt hình");
        model.addAttribute("cartoon", cartoon);

        List<Movie> favorite = movieService.findTop16ByOrderByTotalViewDesc();
        model.addAttribute("favorite", favorite);

        return "home";
    }
}
