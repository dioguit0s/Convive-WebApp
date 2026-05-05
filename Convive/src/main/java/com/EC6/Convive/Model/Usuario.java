package com.EC6.Convive.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "Usuario")
public class Usuario {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "email")
    private String email;

    @Column(name = "senhaHash")
    private String senhaHash;

    @Column(name = "status")
    private String status;

    @Column(name = "isInadimplente")
    private boolean isInadimplente;

}
