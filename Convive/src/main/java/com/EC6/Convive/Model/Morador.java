package com.EC6.Convive.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "Morador")
public class Morador extends Usuario {

    @Column(name = "apartamento")
    private int apartamento;
}
