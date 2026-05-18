package com.EC6.Convive.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Reserva")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "inicio")
    private LocalDateTime inicio;

    @Column(name = "fim")
    private LocalDateTime fim;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusReserva status = StatusReserva.PENDENTE;

    @ManyToOne
    @JoinColumn(name = "reservadoPorId")
    private Usuario reservadoPor;

    @ManyToOne
    @JoinColumn(name = "areaReservada")
    private AreaComum areaReservada;

    @Column(name = "convidados_estimados")
    private Integer convidadosEstimados;

    @Column(name = "observacoes", length = 2000)
    private String observacoes;

    @Column(name = "motivo_rejeicao", length = 1000)
    private String motivoRejeicao;

}
