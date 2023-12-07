package Source.Controllers;

import Source.Models.Favorite;
import Source.Models.Review;
import Source.Models.User;
import Source.Models.WatchHistory;
import Source.Services.*;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.List;

@SuppressWarnings("ResultOfMethodCallIgnored")
@Controller
@RequestMapping("/profile")
public class ProfileController {
    private final UserService userService;
    private final MovieService movieService;
    private final EmailService emailService;
    private final WatchHistoryService watchHistoryService;
    private final FavoriteService favoriteService;
    private final ReviewService reviewService;

    @Autowired
    public ProfileController(UserService userService,
                             MovieService movieService,
                             EmailService emailService,
                             WatchHistoryService watchHistoryService,
                             FavoriteService favoriteService,
                             ReviewService reviewService) {
        this.userService = userService;
        this.movieService = movieService;
        this.emailService = emailService;
        this.watchHistoryService = watchHistoryService;
        this.favoriteService = favoriteService;
        this.reviewService = reviewService;
    }

    @RequestMapping("")
    public String viewProfile(Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/";
        }

        model.addAttribute("genres", movieService.getAllGenres());
        model.addAttribute("nations", movieService.getAllNation());
        model.addAttribute("releaseDates", movieService.getAllReleaseDate());
        if(model.getAttribute("action") == null){
            model.addAttribute("action", "profile");
        }

        User user = (User) session.getAttribute("user");
        if (user.getFirstName() == null && user.getLastName() == null) {
            model.addAttribute("name", user.getUsername());
        } else {
            model.addAttribute("name", user.getFirstName() + " " + user.getLastName());
        }

        return "profile";
    }

    @PostMapping("/update")
    public String updateProfile(@RequestParam("avatarPath") String avatar,
                                @RequestParam("username") String username,
                                @RequestParam("firstName") String firstName,
                                @RequestParam("lastName") String lastName,
                                @RequestParam("gender") String gender,
                                @RequestParam("dateOfBirth") String dateOfBirth,
                                Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");

        String avatarPath;
        if(avatar.contains("data:image")){
            byte[] bytes = Base64.getDecoder().decode(avatar.split(",")[1].trim());
            String filePath = "src/main/resources/static/img/avatar/";
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }

            String targetPath = "target/classes/static/img/avatar/";
            File targetFile = new File(targetPath);
            if (!targetFile.exists()) {
                targetFile.mkdirs();
            }

            String fileName = System.currentTimeMillis() + "_avatar_" + user.getUserId() + ".png";
            filePath += fileName;
            targetPath += fileName;
            try {
                Files.write(Path.of(filePath), bytes, StandardOpenOption.CREATE);
                Files.write(Path.of(targetPath), bytes, StandardOpenOption.CREATE);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            avatarPath = "/img/avatar/" + fileName;
        }else {
            avatarPath = avatar;
        }

        user.setAvatar(avatarPath);
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setGender(gender);
        user.setDateOfBirth(dateOfBirth);
        userService.update(user);
        session.setAttribute("user", user);
        return viewProfile(model, session);
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        User u = new User(user.getEmail(), oldPassword);
        if (!userService.authenticateUser(u)) {
            model.addAttribute("error_password", "Mật khẩu cũ không đúng");
            model.addAttribute("error", true);
            return viewProfile(model, session);
        }

        if (newPassword.length() < 6) {
            model.addAttribute("error_password", "Mật khẩu mới phải có ít nhất 6 ký tự");
            model.addAttribute("error", true);
            return viewProfile(model, session);
        }

        if(oldPassword.equals(newPassword)){
            model.addAttribute("error_password", "Mật khẩu mới không được trùng với mật khẩu cũ");
            model.addAttribute("error", true);
            return viewProfile(model, session);
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error_password", "Mật khẩu xác nhận không đúng");
            model.addAttribute("error", true);
            return viewProfile(model, session);
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        newPassword = encoder.encode(newPassword);
        user.setPassword(newPassword);
        userService.update(user);
        session.setAttribute("user", user);
        notificationToEmail(user.getEmail());

        return viewProfile(model, session);
    }

    private void notificationToEmail(String email) {
        String from = "nguyntrungkin091@gmail.com";

        String subject = "Thay đổi mật khẩu trên hệ thống thành công";
        String body = """
                    Bạn vừa thay đổi mật khẩu trên hệ thống của chúng tôi.<br>
                    Nếu bạn không thực hiện hành động này, vui lòng liên hệ với chúng tôi ngay lập tức thông qua đường dẫn.<br>
                    <a href="https://www.facebook.com/kienk4.me/" type="button">https://www.facebook.com/kienk4.me/</a><br><br>
                    Trân trọng, Team Movie Online System
                """;

        try {
            emailService.sendEmail(from, email, subject, body);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping("/watch-history")
    public String viewWatchHistory(@RequestParam(name = "page", defaultValue = "0") int page,
                                   @RequestParam(name = "size", defaultValue = "20") int size,
                                   Model model, HttpSession session){
        Pageable pageable = PageRequest.of(page, size);
        User user = (User) session.getAttribute("user");
        Page<WatchHistory> historyPage = watchHistoryService.findByUserId(user.getUserId(), pageable);

        model.addAttribute("histories", historyPage.getContent());
        model.addAttribute("historyPage", historyPage);
        model.addAttribute("action", "watch-history");
        return viewProfile(model, session);
    }

    @RequestMapping("/watch-history/delete")
    public String deleteWatchHistory(@RequestParam("id") int id, Model model, HttpSession session){
        WatchHistory history = watchHistoryService.findById(id);
        watchHistoryService.delete(history);
        return viewWatchHistory(0, 20, model, session);
    }

    @RequestMapping("/favorite")
    public String viewFavorite(@RequestParam(name = "page", defaultValue = "0") int page,
                                   @RequestParam(name = "size", defaultValue = "20") int size,
                                   Model model, HttpSession session){
        Pageable pageable = PageRequest.of(page, size);
        User user = (User) session.getAttribute("user");
        Page<Favorite> favoritePage = favoriteService.findByUserId(user.getUserId(), pageable);

        model.addAttribute("favorites", favoritePage.getContent());
        model.addAttribute("favoritePage", favoritePage);
        model.addAttribute("action", "favorite");
        return viewProfile(model, session);
    }

    @RequestMapping("/favorite/delete")
    public String deleteFavorite(@RequestParam("id") int id, Model model, HttpSession session){
        Favorite favorite = favoriteService.findById(id);
        favoriteService.delete(favorite);
        return viewFavorite(0, 20, model, session);
    }

    @RequestMapping("/review")
    public String viewReview(@RequestParam(name = "page", defaultValue = "0") int page,
                                 @RequestParam(name = "size", defaultValue = "20") int size,
                                 Model model, HttpSession session){
        Pageable pageable = PageRequest.of(page, size);
        User user = (User) session.getAttribute("user");
        Page<Review> reviewPage = reviewService.findByUserId(user.getUserId(), pageable);

        model.addAttribute("reviews", reviewPage.getContent());
        model.addAttribute("reviewPage", reviewPage);
        model.addAttribute("action", "review");
        return viewProfile(model, session);
    }

    @RequestMapping("/review/delete")
    public String deleteReview(@RequestParam("id") int id, Model model, HttpSession session){
        Review review = reviewService.findById(id);
        reviewService.delete(review);

        // update total rating
        List<Review> reviews = reviewService.findAll()
                .stream()
                .filter(r -> r.getMovie().getMovieId() == review.getMovie().getMovieId())
                .toList();
        double rating = 0;
        for (Review r : reviews) {
            rating += r.getRating();
        }
        review.getMovie().setRating(rating/reviews.size());
        movieService.update(review.getMovie());

        return viewReview(0, 20, model, session);
    }
}
