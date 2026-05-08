package com.EC6.Convive.Model;

public enum StatusReserva {
    PENDENTE("pendente"),
    APROVADO("aprovado"),
    REPROVADO("reprovado");

    private String statusReserva;

    StatusReserva(String statusReserva){this.statusReserva = statusReserva;}
}
