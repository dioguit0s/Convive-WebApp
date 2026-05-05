package com.EC6.Convive.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "Ocorrencia")
public class Ocorrencia {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "prioridade")
    private Prioridade prioridade;

    @Column(name = "status")
    private StatusOcorrencia status;

    @ManyToOne
    @JoinColumn(name = "feitaPorId")
    private Morador morador;
}
