package library.controllers.security;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) boolean error, Model model) {
        if (error) {
            model.addAttribute("errorMsg", "Bad credentials!");
        }
        return "login/login";
    }

    @GetMapping("/logout")
    public String logout() {
        System.out.println("Был logout");
        return "redirect:/";
    }
}
