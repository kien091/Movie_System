package Source.Controllers;

import Source.Services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {
    private final MovieService movieService;
    @Autowired
    public HomeController(MovieService movieService) {
        this.movieService = movieService;
    }

    @RequestMapping("")
    public String viewHome() {
        return "home";
    }
}
