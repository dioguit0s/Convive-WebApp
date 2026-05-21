package com.EC6.Convive.Controller;

import com.EC6.Convive.Model.*;
import com.EC6.Convive.Security.CustomUserDetails;
import com.EC6.Convive.Service.FileStorageService;
import com.EC6.Convive.Service.OcorrenciaService;
import com.EC6.Convive.Util.PaginationConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/morador/ocorrencias")
public class OcorrenciaController {

    private final OcorrenciaService ocorrenciaService;
    private final FileStorageService fileStorageService;

    @GetMapping
    public String listarMinhasOcorrencias(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String busca,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "ALL") String prioridade,
            @RequestParam(defaultValue = "DESC") String ordem,
            Model model) {

        Usuario usuario = userDetails.getUsuario();
        Page<Ocorrencia> ocorrenciasPage = ocorrenciaService.findPaginatedByUser(
                usuario.getId(), page, PaginationConstants.DEFAULT_PAGE_SIZE, busca, status, prioridade, ordem);

        model.addAttribute("usuario", usuario);
        model.addAttribute("ocorrencias", ocorrenciasPage.getContent());
        model.addAttribute("paginaAtual", page);
        model.addAttribute("totalPaginas", ocorrenciasPage.getTotalPages());
        model.addAttribute("busca", busca != null ? busca : "");
        model.addAttribute("filtroStatus", status);
        model.addAttribute("filtroPrioridade", prioridade);
        model.addAttribute("filtroOrdem", ordem);
        model.addAttribute("filtrosAtivos", hasActiveFilters(busca, status, prioridade));
        model.addAttribute("listaVazia", ocorrenciasPage.getTotalElements() == 0);
        return "morador/ocorrencias";
    }

    @GetMapping("/mais")
    public String carregarMais(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) String busca,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "ALL") String prioridade,
            @RequestParam(defaultValue = "DESC") String ordem,
            Model model) {

        Usuario usuario = userDetails.getUsuario();
        Page<Ocorrencia> ocorrenciasPage = ocorrenciaService.findPaginatedByUser(
                usuario.getId(), page, PaginationConstants.DEFAULT_PAGE_SIZE, busca, status, prioridade, ordem);
        model.addAttribute("ocorrencias", ocorrenciasPage.getContent());
        return "morador/ocorrencias :: listagemCards";
    }

    @PostMapping
    public String registrarNovaOcorrencia(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String descricao,
            @RequestParam(value = "imagemEvidencia", required = false) MultipartFile imagemEvidencia,
            RedirectAttributes redirectAttributes) {
        try {
            Usuario usuarioLogado = userDetails.getUsuario();

            Ocorrencia novaOcorrencia = new Ocorrencia();
            novaOcorrencia.setDescricao(descricao);
            novaOcorrencia.setPrioridade(Prioridade.NAO_DEFINIDA);
            novaOcorrencia.setStatus(StatusOcorrencia.REGISTRADA);
            novaOcorrencia.setUsuario(usuarioLogado);

            if (imagemEvidencia != null && !imagemEvidencia.isEmpty()) {
                String urlImagem = fileStorageService.salvarImagemOcorrencia(imagemEvidencia);
                novaOcorrencia.setUrlEvidencia(urlImagem);
            }

            ocorrenciaService.insert(novaOcorrencia);
            redirectAttributes.addFlashAttribute("sucessoOcorrencia", "Ocorrência registrada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erroOcorrencia", "Erro ao registrar: " + e.getMessage());
        }
        return "redirect:/morador/ocorrencias";
    }

    @PostMapping("/excluir")
    public String excluirOcorrencia(@RequestParam UUID id, RedirectAttributes redirectAttributes) {
        try {
            ocorrenciaService.delete(id);
            redirectAttributes.addFlashAttribute("sucessoOcorrencia", "Ocorrência eliminada com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erroOcorrencia", "Erro ao eliminar a ocorrência.");
        }
        return "redirect:/morador/ocorrencias";
    }

    private static boolean hasActiveFilters(String busca, String status, String prioridade) {
        return (busca != null && !busca.isBlank())
                || (status != null && !"ALL".equalsIgnoreCase(status))
                || (prioridade != null && !"ALL".equalsIgnoreCase(prioridade));
    }
}
