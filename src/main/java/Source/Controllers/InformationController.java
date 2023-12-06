package Source.Controllers;

import Source.Models.*;
import Source.Services.FavoriteService;
import Source.Services.MovieService;
import Source.Services.ReviewService;
import jakarta.servlet.http.HttpSession;
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
    private final FavoriteService favoriteService;
    private final ReviewService reviewService;

    @Autowired
    public InformationController(MovieService movieService,
                                 FavoriteService favoriteService,
                                 ReviewService reviewService) {
        this.movieService = movieService;
        this.favoriteService = favoriteService;
        this.reviewService = reviewService;
    }

    @RequestMapping("")
    public String viewInformation(@RequestParam("movie_id") int movieId,
                                  Model model, HttpSession session) {
        if(session.getAttribute("user") == null){
            return "redirect:/";
        }
        // check favorite or not
        Favorite checkFavorite = favoriteService.findAll()
                .stream()
                .filter(f -> f.getUser().getUserId() == ((User) session.getAttribute("user")).getUserId()
                        && f.getMovie().getMovieId() == movieId)
                .findFirst()
                .orElse(null);
        if(checkFavorite != null){
            model.addAttribute("checkFavorite", true);
        }else {
            model.addAttribute("checkFavorite", false);
        }

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

        Review review = reviewService.findAll()
                .stream()
                .filter(r -> r.getUser().getUserId() == ((User) session.getAttribute("user")).getUserId()
                        && r.getMovie().getMovieId() == movieId)
                .findFirst()
                .orElse(null);
        if(review != null){
            model.addAttribute("review", review.getRating());
        }else {
            model.addAttribute("review", 0);
        }

        List<Movie> favorite = movieService.findTop16ByOrderByTotalViewDesc();
        model.addAttribute("carousel", favorite);
        model.addAttribute("newestEpisode", newestEpisode);
        model.addAttribute("navigation", "Phim " + movie.getNation());
        model.addAttribute("genres", movieService.getAllGenres());
        model.addAttribute("nations", movieService.getAllNation());
        model.addAttribute("releaseDates", movieService.getAllReleaseDate());
        model.addAttribute("top6MoviesNewest", movieService.top6NewestMovies());
        model.addAttribute("movie_id", movieId);
        return "information";
    }

    @RequestMapping("/favorite")
    public String favorite(@RequestParam("movie_id") int movieId,
                           Model model, HttpSession session) {
        if(session.getAttribute("user") == null){
            return "redirect:/";
        }

        User user = (User) session.getAttribute("user");
        Movie movie = movieService.findById(movieId);
        Favorite checkFavorite = favoriteService.findAll()
                .stream()
                .filter(f -> f.getUser().getUserId() == user.getUserId()
                        && f.getMovie().getMovieId() == movieId)
                .findFirst()
                .orElse(null);
        if (checkFavorite != null) {
            favoriteService.delete(checkFavorite);
        }else {
            Favorite favorite = new Favorite(user, movie);
            favoriteService.save(favorite);
        }
        return viewInformation(movieId, model, session);
    }

    @RequestMapping("/evaluate")
    public String evaluate(@RequestParam("movie_id") int movieId,
                           @RequestParam("evaluate") int evaluate,
                           Model model, HttpSession session) {
        if(session.getAttribute("user") == null){
            return "redirect:/";
        }

        User user = (User) session.getAttribute("user");
        Movie movie = movieService.findById(movieId);
        Review review = reviewService.findAll()
                .stream()
                .filter(r -> r.getUser().getUserId() == user.getUserId()
                        && r.getMovie().getMovieId() == movieId)
                .findFirst()
                .orElse(null);
        if (review != null) {
            review.setRating(evaluate);
            reviewService.save(review);
        } else {
            review = new Review(0, evaluate, user, movie);
            reviewService.save(review);
        }

        // update total rating
        List<Review> reviews = reviewService.findAll()
                .stream()
                .filter(r -> r.getMovie().getMovieId() == movieId)
                .toList();
        double rating = 0;
        for (Review r : reviews) {
            rating += r.getRating();
        }
        movie.setRating(rating/reviews.size());
        movieService.save(movie);

        return viewInformation(movieId, model, session);
    }
}
