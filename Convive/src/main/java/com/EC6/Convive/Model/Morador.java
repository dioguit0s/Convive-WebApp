package com.EC6.Convive.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "Morador")
public class Morador extends Usuario {

    @Column(name = "apartamento")
    private int apartamento;
}
