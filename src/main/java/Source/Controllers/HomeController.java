package Source.Controllers;

import Source.Models.Movie;
import Source.Models.User;
import Source.Services.EmailService;
import Source.Services.MovieService;
import Source.Services.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
@Controller
@RequestMapping("/")
public class HomeController {
    private final MovieService movieService;
    private final UserService userService;

    private final EmailService emailService;
    @Autowired
    public HomeController(MovieService movieService, UserService userService, EmailService emailService) {
        this.movieService = movieService;
        this.userService = userService;
        this.emailService = emailService;
    }

    @RequestMapping("")
    public String viewHome(Model model, HttpSession session) {
        List<Movie> movies = movieService.findAll()
                .stream()
                .filter(movie -> movie.getTotalEpisode() > 1)
                .sorted((movie1, movie2) -> Integer.compare(movie2.getTotalView(), movie1.getTotalView()))
                .limit(16)
                .collect(Collectors.toList());
        model.addAttribute("movies", movies);

        List<Movie> cartoon = movieService.findMoviesByGenre("Hoạt hình")
                .stream()
                .sorted((movie1, movie2) -> Integer.compare(movie2.getTotalView(), movie1.getTotalView()))
                .limit(16)
                .collect(Collectors.toList());
        model.addAttribute("cartoon", cartoon);

        List<Movie> favorite = movieService.findTop16ByOrderByTotalViewDesc();
        model.addAttribute("favorite", favorite);
        model.addAttribute("carousel", favorite);
        model.addAttribute("navigation", "Phim bộ");

        model.addAttribute("genres", movieService.getAllGenres());
        model.addAttribute("releaseDates", movieService.getAllReleaseDate());
        model.addAttribute("nations", movieService.getAllNation());
        model.addAttribute("top6MoviesNewest", movieService.top6NewestMovies());
        if(session.getAttribute("user") == null && session.getAttribute("user_storage") == null){
            model.addAttribute("showLogin", true);
        }
        return "home";
    }

    @RequestMapping("/filter")
    public String filterBy(@RequestParam("category") String category,
                           @RequestParam(name = "page", defaultValue = "0") int page,
                           @RequestParam(name = "size", defaultValue = "16") int size,
                           Model model){
        Pageable pageable = PageRequest.of(page, size);
        Page<Movie> moviePage;
        if(model.getAttribute("movies") == null){
            moviePage = movieService.filterMoviesByCategory(category, pageable);
            String navigation = switch (category) {
                case "series" -> "Phim bộ";
                case "feature-film" -> "Phim lẻ";
                case "complete" -> "Phim đã hoàn thành";
                case "english-language-films" -> "Phim chiếu rạp";
                case "favorite" -> "Phim yêu thích";
                default -> category;
            };
            model.addAttribute("navigation", navigation);
        }else{
            List<Movie> movies = (List<Movie>) model.getAttribute("movies");
            assert movies != null;
            moviePage = movieService.pageableMovies(movies, pageable);
        }

        model.addAttribute("movies", moviePage.getContent());
        model.addAttribute("moviesPage", moviePage);

        List<Movie> favorite = movieService.findTop16ByOrderByTotalViewDesc();
        model.addAttribute("carousel", favorite);


        model.addAttribute("genres", movieService.getAllGenres());
        model.addAttribute("releaseDates", movieService.getAllReleaseDate());
        model.addAttribute("nations", movieService.getAllNation());
        model.addAttribute("top6MoviesNewest", movieService.top6NewestMovies());
        if(model.getAttribute("dividerPage") == null){
            model.addAttribute("dividerPage", "filter");
            model.addAttribute("categoryFilter", category);
        }
        return "home";
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String search(@RequestParam("search") String search,
                         @RequestParam(name = "page", defaultValue = "0") int page,
                         @RequestParam(name = "size", defaultValue = "16") int size,
                         Model model) {
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
        model.addAttribute("dividerPage", "search");
        model.addAttribute("searchSearch", search);
        return filterBy("search", page, size, model);
    }

    @RequestMapping(value = "/filterBy", method = RequestMethod.GET)
    public String filterWithOption(@RequestParam("status") String status,
                                   @RequestParam("genre") String genre,
                                   @RequestParam("year") String year,
                                   @RequestParam("sort") String sort,
                                   @RequestParam(name = "page", defaultValue = "0") int page,
                                   @RequestParam(name = "size", defaultValue = "16") int size,
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
        model.addAttribute("dividerPage", "filterBy");
        model.addAttribute("statusFilterBy", status);
        model.addAttribute("genreFilterBy", genre);
        model.addAttribute("yearFilterBy", year);
        model.addAttribute("sortFilterBy", sort);
        return filterBy("filterBy", page, size, model);
    }

    @PostMapping(value = "/login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        Model model, HttpSession session){
        session.removeAttribute("user_storage");

        User user = new User(email, password);
        User foundUser = userService.findAll()
                .stream()
                .filter(u -> u.getEmail().equals(user.getEmail()))
                .findFirst()
                .orElse(null);
        if(userService.authenticateUser(user) && foundUser != null){
            session.setAttribute("user", foundUser);
        } else {
            model.addAttribute("error", "Email hoặc mật khẩu không đúng");
        }
        if(foundUser != null){
            if (foundUser.isStatus()){
                session.removeAttribute("user");
                model.addAttribute("error", "Tài khoản của bạn đã bị khóa");
            }
        }
        return viewHome(model, session);
    }

    @PostMapping(value = "/reset")
    public String reset(@RequestParam("email") String email,
                        Model model, HttpSession session){
        model.addAttribute("showReset", true);
        List<User> users = userService.findAll();
        User user = users.stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElse(null);
        if(user == null){
            model.addAttribute("reset", "Tài khoản chưa được đăng kí");
        } else {
            StringBuilder passwordBuilder = new StringBuilder();
            for(int i = 0; i < 8; i++){
                passwordBuilder.append((char) (Math.random() * 26 + 'a'));
            }

            // use bcrypt to hash password and save to database
            String newPassword = passwordBuilder.toString();
            BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
            String bcryptPassword = bcrypt.encode(newPassword);
            user.setPassword(bcryptPassword);
            userService.update(user);

            sendEmail(email, newPassword);

            model.addAttribute("reset", "Mật khẩu đã được gửi về email của bạn");
        }
        return viewHome(model, session);
    }

    private void sendEmail(String email, String newPassword){
        String from = "nguyntrungkin091@gmail.com";

        String subject = "Bạn vừa yêu cầu đặt lại mật khẩu";
        String body = "Mật khẩu mới của bạn là: " + newPassword;
        try {
            emailService.sendEmail(from, email,subject, body);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "/register")
    public String register(@RequestParam("email") String email,
                           @RequestParam("password") String password,
                           @RequestParam("confirmPassword") String confirmPassword,
                           Model model, HttpSession session){
        User userCheck = userService.findAll()
                .stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElse(null);

        if(userCheck != null){
            model.addAttribute("error_register", "Email đã được đăng kí");
            model.addAttribute("showRegister", true);
            model.addAttribute("showReset", false);
        }
        else if(!password.equals(confirmPassword)){
            model.addAttribute("error_register", "Mật khẩu không khớp");
            model.addAttribute("showRegister", true);
        } else {
            model.addAttribute("showOTP", true);
            User user = new User("user", password,
                    "/img/user.png", email, "user", false);

            session.setAttribute("user_storage", user);

            StringBuilder otp = new StringBuilder();
            for(int i = 0; i < 6; i++){
                otp.append((int) (Math.random() * 10));
            }
            session.setAttribute("otp", otp.toString());

            verifyEmail(email, otp.toString());
        }
        return viewHome(model, session);
    }
    private void verifyEmail(String email, String otp) {
        String from = "nguyntrungkin091@gmail.com";

        String subject = "Yêu cầu mã xác thực tài khoản";
        String body = "Mã xác thực tài khoản của bạn là: " + otp;
        try {
            emailService.sendEmail(from, email,subject, body);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "/code")
    public String verifyCode(@RequestParam("countdown") String countdown,
                            @RequestParam("code") String code,
                            Model model, HttpSession session){
        String otp = (String) session.getAttribute("otp");
        if(code.equals(otp) && !countdown.equals("0")){
            User user = (User) session.getAttribute("user_storage");

            // remove session
            session.removeAttribute("user_storage");
            session.removeAttribute("otp");

            userService.save(user);
            session.setAttribute("user", user);
        } else {
            model.addAttribute("error_code", "Mã xác thực không đúng hoặc đã hết hạn");
        }
        return viewHome(model, session);
    }

    @GetMapping(value = "/logout")
    public String logout(Model model, HttpSession session){
        session.removeAttribute("user");
        return viewHome(model, session);
    }
}
