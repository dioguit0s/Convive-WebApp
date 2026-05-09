package com.EC6.Convive.Controller;

import com.EC6.Convive.Service.ContactMailService;
import com.EC6.Convive.Model.ContactMessageModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(ContactController.class)
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContactMailService contactMailService;

    @Test
    void getContact_showsForm() throws Exception {
        mockMvc.perform(get("/contact"))
                .andExpect(status().isOk())
                .andExpect(view().name("public/contact"))
                .andExpect(model().attributeExists("contactForm"))
                .andExpect(model().attribute("contactForm", instanceOf(ContactMessageModel.class)));
    }

    @Test
    void postContact_valid_redirectsAndCallsService() throws Exception {
        mockMvc.perform(post("/contact")
                        .param("fullName", "João Silva")
                        .param("email", "joao@empresa.com.br")
                        .param("subject", "Demonstração")
                        .param("message", "Gostaria de agendar uma demo."))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/contact"));

        verify(contactMailService).send(any(ContactMessageModel.class));
    }

    @Test
    void postContact_invalid_doesNotCallService() throws Exception {
        mockMvc.perform(post("/contact")
                        .param("fullName", "")
                        .param("email", "invalid")
                        .param("subject", "")
                        .param("message", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("public/contact"));

        verify(contactMailService, never()).send(any());
    }
}
