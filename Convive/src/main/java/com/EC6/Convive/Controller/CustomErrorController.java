package com.EC6.Convive.Controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        String codigoErro = "ERR-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        String dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        if (status != null) {
            codigoErro = "HTTP-" + status.toString();
        }

        model.addAttribute("codigoErro", codigoErro);
        model.addAttribute("dataHora", dataHora);
        
        String msgErro = (message != null && !message.toString().isBlank())
                ? message.toString() 
                : "Acesso negado, recurso não encontrado ou ocorreu um erro inesperado.";
                
        model.addAttribute("mensagem", msgErro);

        return "public/error";
    }
}