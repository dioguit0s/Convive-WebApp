package com.EC6.Convive.Controller;

import com.EC6.Convive.Model.Ocorrencia;
import com.EC6.Convive.Model.Prioridade;
import com.EC6.Convive.Model.StatusOcorrencia;
import com.EC6.Convive.Model.Usuario;
import com.EC6.Convive.Security.CustomUserDetails;
import com.EC6.Convive.Service.OcorrenciaService;
import com.EC6.Convive.Service.UsuarioService;
import com.EC6.Convive.Util.PaginationConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/moderador/triagemOcorrencias")
public class TriagemOcorrenciasController {

    private final OcorrenciaService ocorrenciaService;
    private final UsuarioService usuarioService;

    @GetMapping
    public String gerenciarOcorrencias(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String busca,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "ALL") String prioridade,
            @RequestParam(defaultValue = "DESC") String ordem,
            Model model) {

        Usuario usuarioLogado = usuarioService.getByEmail(userDetails.getUsername());
        Page<Ocorrencia> ocorrenciasPage = ocorrenciaService.findTriagemPaginated(
                page, PaginationConstants.DEFAULT_PAGE_SIZE, busca, status, prioridade, ordem);

        populateTriagemModel(model, usuarioLogado, ocorrenciasPage, page, busca, status, prioridade, ordem);
        return "moderador/triagemOcorrencias";
    }

    @GetMapping("/mais")
    public String carregarMais(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) String busca,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "ALL") String prioridade,
            @RequestParam(defaultValue = "DESC") String ordem,
            Model model) {

        Page<Ocorrencia> ocorrenciasPage = ocorrenciaService.findTriagemPaginated(
                page, PaginationConstants.DEFAULT_PAGE_SIZE, busca, status, prioridade, ordem);
        model.addAttribute("ocorrencias", ocorrenciasPage.getContent());
        return "moderador/triagemOcorrencias :: listagemCards";
    }

    @PostMapping("/atualizar")
    public String atualizarOcorrencia(@RequestParam UUID id,
                                      @RequestParam Prioridade prioridade,
                                      @RequestParam StatusOcorrencia status,
                                      @RequestParam(required = false) String respostaModerador,
                                      RedirectAttributes attributes) {
        Ocorrencia ocorrencia = ocorrenciaService.searchById(id);

        ocorrencia.setPrioridade(prioridade);
        ocorrencia.setStatus(status);
        if (respostaModerador != null && !respostaModerador.isBlank()) {
            ocorrencia.setRespostaModerador(respostaModerador);
        }

        ocorrenciaService.update(ocorrencia);

        attributes.addFlashAttribute("sucesso", "Ocorrência atualizada com sucesso!");
        return "redirect:/moderador/triagemOcorrencias";
    }

    private void populateTriagemModel(Model model, Usuario usuario, Page<Ocorrencia> page, int paginaAtual,
                                      String busca, String status, String prioridade, String ordem) {
        model.addAttribute("usuario", usuario);
        model.addAttribute("ocorrencias", page.getContent());
        model.addAttribute("paginaAtual", paginaAtual);
        model.addAttribute("totalPaginas", page.getTotalPages());
        model.addAttribute("totalElementos", page.getTotalElements());
        model.addAttribute("busca", busca != null ? busca : "");
        model.addAttribute("filtroStatus", status);
        model.addAttribute("filtroPrioridade", prioridade);
        model.addAttribute("filtroOrdem", ordem);
        model.addAttribute("filtrosAtivos", hasActiveFilters(busca, status, prioridade));
        model.addAttribute("listaVazia", page.getTotalElements() == 0);
    }

    private static boolean hasActiveFilters(String busca, String status, String prioridade) {
        return (busca != null && !busca.isBlank())
                || (status != null && !"ALL".equalsIgnoreCase(status))
                || (prioridade != null && !"ALL".equalsIgnoreCase(prioridade));
    }
}
