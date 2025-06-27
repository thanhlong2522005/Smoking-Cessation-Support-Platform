package com.example.smoking.platform.controller;

import com.example.smoking.platform.model.User;
import com.example.smoking.platform.model.SmokingLog;
<<<<<<< HEAD
import com.example.smoking.platform.model.Motivation;
import com.example.smoking.platform.service.MotivationService;
import com.example.smoking.platform.service.AchievementService;
import com.example.smoking.platform.service.SmokingLogService;
import com.example.smoking.platform.service.UserService;

=======
import com.example.smoking.platform.service.AchievementService;
import com.example.smoking.platform.service.SmokingLogService;
import com.example.smoking.platform.service.UserService;
>>>>>>> 35c6a47bdb5780cf48015a88cd926b4470d500c2
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
<<<<<<< HEAD
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
=======
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
>>>>>>> 35c6a47bdb5780cf48015a88cd926b4470d500c2
import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;
<<<<<<< HEAD

    @Autowired
    private AchievementService achievementService;

    @Autowired
    private SmokingLogService smokingLogService;

    @Autowired
    private MotivationService motivationService;

    // ✅ Trang chính Dashboard
=======
    @Autowired
    private AchievementService achievementService;
    @Autowired
    private SmokingLogService smokingLogService;

    @GetMapping("/")
    public String index() {
        return "index"; // Trang chủ giới thiệu
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // Hiển thị trang đăng nhập
    }

>>>>>>> 35c6a47bdb5780cf48015a88cd926b4470d500c2
    @GetMapping("/dashboard")
    public String dashboard(Model model, RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();

            Optional<User> currentUserOptional = userService.getUserByUsername(currentUsername);
<<<<<<< HEAD
=======

>>>>>>> 35c6a47bdb5780cf48015a88cd926b4470d500c2
            if (currentUserOptional.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy người dùng.");
                return "redirect:/login";
            }

            User currentUser = currentUserOptional.get();

<<<<<<< HEAD
            // ➕ Dữ liệu người dùng
            model.addAttribute("userId", currentUser.getId());
            model.addAttribute("userRole", currentUser.getRole().toString());
            model.addAttribute("user", currentUser);
            model.addAttribute("userAchievements", achievementService.getUserAchievements(currentUser));

            // ➕ Lịch sử và tiến trình
            Optional<SmokingLog> latestLogOptional = smokingLogService.findLatestSmokingLogByUser(currentUser);
            model.addAttribute("latestSmokingLog", latestLogOptional.orElse(null));
            model.addAttribute("smokeFreeDays", smokingLogService.calculateDaysWithoutSmoking(currentUser));
            model.addAttribute("moneySaved", smokingLogService.calculateMoneySaved(currentUser));
            model.addAttribute("healthMilestones", smokingLogService.getHealthProgress(currentUser));

            // ✅ Thêm quote động viên
            Motivation quote = motivationService.getRandomMotivation();
            model.addAttribute("quote", quote);
            model.addAttribute("newMotivation", new Motivation());
=======
            // Truyền dữ liệu cho frontend
            model.addAttribute("userId", currentUser.getId()); // Cần cho API JavaScript
            model.addAttribute("userRole", currentUser.getRole().toString());
            model.addAttribute("user", currentUser); // Đối tượng User đầy đủ
            model.addAttribute("userAchievements", achievementService.getUserAchievements(currentUser));

            Optional<SmokingLog> latestLogOptional = smokingLogService.findLatestSmokingLogByUser(currentUser);
            model.addAttribute("latestSmokingLog", latestLogOptional.orElse(null));

            model.addAttribute("smokeFreeDays", smokingLogService.calculateDaysWithoutSmoking(currentUser));
            model.addAttribute("moneySaved", smokingLogService.calculateMoneySaved(currentUser));
            model.addAttribute("healthMilestones", smokingLogService.getHealthProgress(currentUser)); // Từ file 1
>>>>>>> 35c6a47bdb5780cf48015a88cd926b4470d500c2

            return "dashboard";

        } catch (Exception e) {
<<<<<<< HEAD
            e.printStackTrace(); // Log lỗi (nên dùng logger trong production)
=======
            e.printStackTrace(); // Ghi log chi tiết lỗi (nên thay bằng logging framework như SLF4J trong production)
>>>>>>> 35c6a47bdb5780cf48015a88cd926b4470d500c2
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi khi tải dashboard.");
            return "redirect:/login";
        }
    }
<<<<<<< HEAD

    // ✅ Gửi lời động viên mới
    @PostMapping("/dashboard/addMotivation")
    public String addMotivation(@ModelAttribute("newMotivation") Motivation newMotivation,
                                Principal principal) {
        newMotivation.setAuthor(principal != null ? principal.getName() : "Ẩn danh");
        motivationService.save(newMotivation);
        return "redirect:/dashboard";
    }

    // ✅ Trang chủ giới thiệu
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User()); // để dùng th:object="${user}"
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute("user") User user,
                              @RequestParam("confirmPassword") String confirmPassword,
                              RedirectAttributes redirectAttributes) {
        try {
            if (!user.getPassword().equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu không khớp.");
                return "redirect:/register";
            }

            userService.registerUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi khi đăng ký.");
            return "redirect:/register";
        }
    }




    // ✅ Trang đăng nhập
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
=======
}
>>>>>>> 35c6a47bdb5780cf48015a88cd926b4470d500c2
