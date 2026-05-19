package com.EC6.Convive.Controller;

import com.EC6.Convive.Model.ForgotPasswordForm;
import com.EC6.Convive.Model.ResetPasswordForm;
import com.EC6.Convive.Service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @GetMapping("/forgot-password")
    public String forgotPasswordPage(Model model) {
        if (!model.containsAttribute("forgotPasswordForm")) {
            model.addAttribute("forgotPasswordForm", new ForgotPasswordForm());
        }
        return "public/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String submitForgotPassword(@Valid @ModelAttribute("forgotPasswordForm") ForgotPasswordForm form,
                                       BindingResult bindingResult,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            log.warn("Forgot password: validação falhou | campos={}",
                    bindingResult.getFieldErrors().stream().map(FieldError::getField).distinct().toList());
            return "public/forgot-password";
        }
        passwordResetService.requestReset(form.getEmail());
        redirectAttributes.addFlashAttribute("resetEmailSent", true);
        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam(required = false) String token, Model model) {
        if (token == null || token.isBlank()) {
            model.addAttribute("tokenInvalid", true);
            return "public/reset-password";
        }
        if (passwordResetService.findValidToken(token).isEmpty()) {
            model.addAttribute("tokenInvalid", true);
            return "public/reset-password";
        }
        ResetPasswordForm form = new ResetPasswordForm();
        form.setToken(token);
        model.addAttribute("resetPasswordForm", form);
        return "public/reset-password";
    }

    @PostMapping("/reset-password")
    public String submitResetPassword(@Valid @ModelAttribute("resetPasswordForm") ResetPasswordForm form,
                                      BindingResult bindingResult,
                                      Model model) {
        if (bindingResult.hasErrors()) {
            return "public/reset-password";
        }
        if (passwordResetService.findValidToken(form.getToken()).isEmpty()) {
            model.addAttribute("tokenInvalid", true);
            return "public/reset-password";
        }
        try {
            passwordResetService.resetPassword(form.getToken(), form.getPassword(), form.getConfirmPassword());
        } catch (IllegalArgumentException e) {
            if ("As senhas não coincidem.".equals(e.getMessage())) {
                bindingResult.rejectValue("confirmPassword", "mismatch", e.getMessage());
                return "public/reset-password";
            }
            model.addAttribute("tokenInvalid", true);
            return "public/reset-password";
        }
        return "redirect:/login?reset=success";
    }
}
