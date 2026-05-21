package com.EC6.Convive.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Comunicado")
public class Comunicado {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "conteudo")
    private String conteudo;

    @Column(name = "publicadoEm")
    private LocalDateTime publicadoEm;

    @ManyToOne
    @JoinColumn(name = "publicadoPor")
    private Moderador moderador;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    private TipoComunicado tipo = TipoComunicado.Geral;

    @Column(name = "urlImagem", length = 500)
    private String urlImagem;

}
