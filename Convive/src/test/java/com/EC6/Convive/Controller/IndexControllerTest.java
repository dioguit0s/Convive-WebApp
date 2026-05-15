package com.EC6.Convive.Controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IndexControllerTest {

    @Test
    void indexController_LandingPage_Sucesso() {
        IndexController indexController = new IndexController();
        assertEquals("public/landing", indexController.index());
    }

    @Test
    void loginController_Login_Sucesso() {
        LoginController loginController = new LoginController();
        assertEquals("public/login", loginController.login());
    }
}
