package com.EC6.Convive.Model;

public enum CategoriaOcorrencia {
    BARULHO("Barulho", Prioridade.ALTA),
    INFRAESTRUTURA("Infraestrutura", Prioridade.ALTA),
    LIMPEZA("Limpeza", Prioridade.MEDIA),
    REGRAS("Regras", Prioridade.MEDIA),
    OUTRO("Outro", Prioridade.NAO_DEFINIDA);

    private final String rotulo;
    private final Prioridade prioridadePadrao;

    CategoriaOcorrencia(String rotulo, Prioridade prioridadePadrao) {
        this.rotulo = rotulo;
        this.prioridadePadrao = prioridadePadrao;
    }

    public String getRotulo() {
        return rotulo;
    }

    public Prioridade getPrioridadePadrao() {
        return prioridadePadrao;
    }
}
