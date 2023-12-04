package Source.Controllers;

import Source.Models.Episode;
import Source.Models.Movie;
import Source.Services.EpisodeService;
import Source.Services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/information")
public class InformationController {
    private final MovieService movieService;
    private final EpisodeService episodeService;

    @Autowired
    public InformationController(MovieService movieService, EpisodeService episodeService) {
        this.movieService = movieService;
        this.episodeService = episodeService;
    }

    @RequestMapping("")
    public String viewInformation(@RequestParam("movie_id") int movieId,
                                  Model model) {
        Movie movie = movieService.findById(movieId);
        model.addAttribute("movie", movie);

        Episode newestEpisode = movie.getEpisodes()
                .stream()
                .max(Comparator.comparingInt(Episode::getEpisodeId))
                .orElse(null);


        String time;
        assert newestEpisode != null;
        if(newestEpisode.getDuration() > 60){
            time = newestEpisode.getDuration()/60 + " giờ " + newestEpisode.getDuration()%60 + " phút";
        }else {
            time = newestEpisode.getDuration() + " phút";
        }
        model.addAttribute("time", time);

        List<Movie> favorite = movieService.findTop16ByOrderByTotalViewDesc();
        model.addAttribute("carousel", favorite);
        model.addAttribute("newestEpisode", newestEpisode);
        model.addAttribute("navigation", "Phim " + movie.getNation());
        model.addAttribute("genres", movieService.getAllGenres());
        model.addAttribute("nations", movieService.getAllNation());
        model.addAttribute("releaseDates", movieService.getAllReleaseDate());
        model.addAttribute("top6MoviesNewest", movieService.top6NewestMovies());
        return "information";
    }
}
