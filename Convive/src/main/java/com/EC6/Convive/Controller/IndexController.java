package com.EC6.Convive.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "public/landing";
    }

    @GetMapping("/features")
    public String features() {return "public/features";}

    @GetMapping("/about")
    public String about() {return "public/about";}

}