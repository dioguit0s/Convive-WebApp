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
    @Table(name = "Notificacao")
    public class Notificacao {

        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;

        @ManyToOne
        @JoinColumn(name = "emitidoPorId", nullable = false)
        private Moderador emitidoPor;

        @ManyToOne
        @JoinColumn(name = "moradorId", nullable = false)
        private Morador morador;

        @Column(name = "apartamento", nullable = false)
        private int apartamento;

        @Column(name = "titulo", nullable = false)
        private String titulo;

        @Column(name = "descricao", length = 2000, nullable = false)
        private String descricao;

        @Enumerated(EnumType.STRING)
        @Column(name = "gravidade", nullable = false)
        private GravidadeNotificacao gravidade;

        @Column(name = "dataEnvio", nullable = false)
        private LocalDateTime dataEnvio;

        @Column(name = "dataOcorrencia")
        private LocalDateTime dataOcorrencia;

        @Column(name = "gerouMulta")
        private boolean gerouMulta = false;

    }