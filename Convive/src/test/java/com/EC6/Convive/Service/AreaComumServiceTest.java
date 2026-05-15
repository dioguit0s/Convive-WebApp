package com.EC6.Convive.Service;

import com.EC6.Convive.Model.AreaComum;
import com.EC6.Convive.Repository.AreaComumRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AreaComumServiceTest {

    @Mock
    private AreaComumRepository areaComumRepository;
    @InjectMocks
    private AreaComumService areaComumService;

    @Test
    void areaComumService_SearchByName_Sucesso() {
        AreaComum area = new AreaComum();
        area.setNome("Piscina");
        when(areaComumRepository.findByNome("Piscina")).thenReturn(Optional.of(area));

        AreaComum encontrada = areaComumService.searchByName("Piscina");

        assertNotNull(encontrada);
        assertEquals("Piscina", encontrada.getNome());
    }

    @Test
    void areaComumService_SearchByName_ErroNaoEncontrado() {
        when(areaComumRepository.findByNome("Inexistente")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> areaComumService.searchByName("Inexistente"));
    }
}
