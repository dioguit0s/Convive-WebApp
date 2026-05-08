package com.EC6.Convive.Model;

import lombok.Getter;

@Getter
public enum StatusOcorrencia {
    REGISTRADA("registrada"),
    EM_ANALISE("em_analise"),
    RESOLVIDA("resolvida"),
    REJEITADA("rejeitada");

    private String statusOcorrencia;

    StatusOcorrencia(String statusOcorrencia){this.statusOcorrencia = statusOcorrencia; }
}
