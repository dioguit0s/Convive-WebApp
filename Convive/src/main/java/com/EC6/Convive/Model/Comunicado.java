package com.EC6.Convive.Model;

import jakarta.persistence.*;

import java.time.LocalDate;

public class Comunicado {
    @Id
    @GeneratedValue
    private int id;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "conteudo")
    private String conteudo;

    @Column(name = "publicadoEm")
    private LocalDate publicadoEm;

    @ManyToOne
    @JoinColumn(name = "publicadoPor")
    private Moderador moderador;
}
