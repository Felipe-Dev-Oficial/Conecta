package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Entities;

import com.etec.zl.conecta.Domain.ValueObjects.Cursos;
import com.etec.zl.conecta.Domain.ValueObjects.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "turmas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TurmaEntity implements Serializable {

    @Id
    @Column(name = "id")
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "curso", length = 50, nullable = false)
    private Cursos curso;

    @Column(name = "modulos", nullable = false)
    private Integer modulos;

    @Column(name = "atual", nullable = false)
    private Integer atual;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 3, nullable = false)
    private Status status;
}
