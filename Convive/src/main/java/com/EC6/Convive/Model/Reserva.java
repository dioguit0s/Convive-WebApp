package com.EC6.Convive.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "Reserva")
public class Reserva {
    @Id
    @GeneratedValue
    private int id;

    @Column(name = "inicio")
    private LocalDate inicio;

    @Column(name = "fim")
    private LocalDate fim;

    @Column(name = "status")
    private StatusReserva status;

    @ManyToOne
    @JoinColumn(name = "reservadoPorId")
    private Usuario reservadoPor;
}
