package com.EC6.Convive.Controller;

import com.EC6.Convive.Service.ContactMailService;
import com.EC6.Convive.dto.ContactMessageDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ContactController {

    private final ContactMailService contactMailService;

    @GetMapping("/contact")
    public String contactPage(Model model) {
        if (!model.containsAttribute("contactForm")) {
            model.addAttribute("contactForm", new ContactMessageDto());
        }
        return "public/contact";
    }

    @PostMapping("/contact")
    public String submitContact(@Valid @ModelAttribute("contactForm") ContactMessageDto contactForm,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            log.warn("Contato: validação falhou | campos={}",
                    bindingResult.getFieldErrors().stream().map(FieldError::getField).distinct().toList());
            return "public/contact";
        }
        try {
            contactMailService.send(contactForm);
        } catch (MailException | IllegalStateException e) {
            log.warn("Contato: envio não concluído para o utilizador | tipo={} | motivo={}",
                    e.getClass().getSimpleName(), e.getMessage());
            model.addAttribute("contactMailError", true);
            return "public/contact";
        }
        redirectAttributes.addFlashAttribute("contactSuccess", true);
        return "redirect:/contact";
    }
}
