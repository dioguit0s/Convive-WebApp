package com.EC6.Convive.Controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String login() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {

            boolean isModerador = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_MODERADOR"));

            if (isModerador) {
                return "redirect:/morador/home";
            }

            return "redirect:/morador/home";
        }

        return "public/login";
    }
}
