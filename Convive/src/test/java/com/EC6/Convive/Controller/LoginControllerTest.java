package com.EC6.Convive.Controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class LoginControllerTest {
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
