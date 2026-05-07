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
@Table(name = "Notificacao")
public class Notificacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(name = "mensagem")
    private String mensagem;

    @Column(name = "enviadaEm")
    private LocalDate enviadaEm;

    @ManyToOne
    @JoinColumn(name = "enviadoPorId")
    private Usuario enviadaPor;
}
