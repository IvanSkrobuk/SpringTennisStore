package tennnisshop.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import tennnisshop.entity.User;
import tennnisshop.entity.Authority;
import tennnisshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Optional;

@Controller
public class MainController {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/index")
    public String toHomepage() {
        return "shop";
    }

    @GetMapping("/login")
    public String toLoginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(User user,Model model) {
        Optional<User> existingUser = userRepository.findById(user.getUsername());
        if (existingUser.isPresent()) {
            model.addAttribute("error", "Уже есть аккаунт с таким логином.");
            return "register";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);

        if (user.getAuthorities() == null) {
            user.setAuthorities(new ArrayList<>());
        }

        Authority authority = new Authority();
        authority.setAuthority("ROLE_USER");
        authority.setUser(user);
        user.getAuthorities().add(authority);
        userRepository.save(user);

        return "redirect:/login";
    }

}
