package com.EC6.Convive.Model;

public enum TipoComunicado {
    Obras(""),
    Reunião(""),
    Eventos(""),
    Geral("Geral");

    private String tipoComunicado;

    TipoComunicado(String tipoComunicado) {this.tipoComunicado = tipoComunicado;}
}
