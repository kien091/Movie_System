package Source.Controllers;

import Source.Models.Episode;
import Source.Models.Movie;
import Source.Models.User;
import Source.Models.WatchHistory;
import Source.Services.EpisodeService;
import Source.Services.MovieService;
import Source.Services.WatchHistoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/watch")
public class WatchController {
    private final MovieService movieService;
    private final EpisodeService episodeService;

    private final WatchHistoryService watchHistoryService;
    @Autowired
    public WatchController(MovieService movieService,
                           EpisodeService episodeService,
                           WatchHistoryService watchHistoryService) {
        this.movieService = movieService;
        this.episodeService = episodeService;
        this.watchHistoryService = watchHistoryService;
    }

    @RequestMapping("")
    public String watch(@RequestParam("episode_id") int episode_id,
                        Model model, HttpSession session) {
        if(session.getAttribute("user") == null){
            return "redirect:/";
        }
        // add to watch history
        User user = (User) session.getAttribute("user");
        Movie movie = episodeService.findById(episode_id).getMovie();
        WatchHistory history = new WatchHistory(0, new Date(), user, movie);
        watchHistoryService.save(history);

        // Update view of episode
        Episode episode = episodeService.findById(episode_id);
        episode.setView(episode.getView() + 1);
        episodeService.save(episode);
        model.addAttribute("episode", episode);

        // Update total view of movie
        model.addAttribute("movie", movie);
        int total_view = 0;
        for(Episode e : movie.getEpisodes()) {
            total_view += e.getView();
        }
        movie.setTotalView(total_view);
        movieService.save(movie);

        List<Movie> favorite = movieService.findTop16ByOrderByTotalViewDesc();
        model.addAttribute("carousel", favorite);
        model.addAttribute("navigation", "Phim " + movie.getNation());
        model.addAttribute("genres", movieService.getAllGenres());
        model.addAttribute("nations", movieService.getAllNation());
        model.addAttribute("releaseDates", movieService.getAllReleaseDate());
        model.addAttribute("top6MoviesNewest", movieService.top6NewestMovies());
        return "watch";
    }
}
