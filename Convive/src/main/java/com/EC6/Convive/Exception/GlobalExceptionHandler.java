package com.EC6.Convive.Exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {
        String codigoErro = "ERR-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        String dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        
        model.addAttribute("codigoErro", codigoErro);
        model.addAttribute("dataHora", dataHora);
        model.addAttribute("mensagem", ex.getMessage());
        
        System.err.println("[" + codigoErro + "] " + ex.getMessage());
        ex.printStackTrace();
        
        return "public/error";
    }
}