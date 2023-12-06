package Source.Controllers;

import Source.Models.User;
import Source.Services.EmailService;
import Source.Services.MovieService;
import Source.Services.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
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

@SuppressWarnings("ResultOfMethodCallIgnored")
@Controller
@RequestMapping("/profile")
public class ProfileController {
    private final UserService userService;
    private final MovieService movieService;
    private final EmailService emailService;

    @Autowired
    public ProfileController(UserService userService,
                             MovieService movieService,
                             EmailService emailService) {
        this.userService = userService;
        this.movieService = movieService;
        this.emailService = emailService;
    }

    @RequestMapping("")
    public String viewProfile(Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/";
        }

        model.addAttribute("genres", movieService.getAllGenres());
        model.addAttribute("nations", movieService.getAllNation());
        model.addAttribute("releaseDates", movieService.getAllReleaseDate());

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

        byte[] bytes = Base64.getDecoder().decode(avatar);
        String filePath = "src/main/resources/static/img/avatar/";
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_avatar_" + user.getUserId() + ".png";
        filePath += fileName;
        try {
            Files.write(Path.of(filePath), bytes, StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String avatarPath = "/img/avatar/" + fileName;
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
}
