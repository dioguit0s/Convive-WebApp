package com.EC6.Convive.Model;

public enum Prioridade {
    NAO_DEFINIDA("Aguardando triagem"),
    ALTA("alta"),
    BAIXA("baixa"),
    MEDIA("media");

    private String prioridade;

    Prioridade(String prioridade){this.prioridade = prioridade;}

    public String getPrioridade() {
        return this.prioridade;
    }
}
