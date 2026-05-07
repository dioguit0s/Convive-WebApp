package com.EC6.Convive.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
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
    private LocalDate inicio;

    @Column(name = "fim")
    private LocalDate fim;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusReserva status;

    @ManyToOne
    @JoinColumn(name = "reservadoPorId")
    private Usuario reservadoPor;
}
