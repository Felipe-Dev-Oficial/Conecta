package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Entities;

import com.etec.zl.conecta.Domain.ValueObjects.*;
import com.etec.zl.conecta.Infraestructure.Security.Service.EncryptorService;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity implements Serializable {

    @Id
    @Column(name = "id", length = 20)
    private String id;
    @Column(name = "nome", length = 250, nullable = false)
    private String nome;
    @Convert(converter = EncryptorService.class)
    @Column(name = "email", nullable = false, columnDefinition = "TEXT")
    private String email;
    @Convert(converter = EncryptorService.class)
    @Column(name = "numero", length = 11)
    private String numero;
    @Column(name = "senha", nullable = false, columnDefinition = "TEXT")
    private String senha;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", length = 10, nullable = false)
    private Tipo tipo;
    @Convert(converter = EncryptorService.class)
    @Column(name = "password_updater_token", columnDefinition = "TEXT")
    private String passwordUpdaterToken;
    @Column(name = "password_updater_token_expiration")
    private LocalDateTime passwordTokenExpiration;
    @Column(name = "email_updater_token", columnDefinition = "TEXT")
    private String emailUpdaterToken;
    @Column(name = "email_updater_token_expiration")
    private LocalDateTime emailTokenExpiration;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "aluno_turmas",
            joinColumns = @JoinColumn(name = "aluno_id"),
            inverseJoinColumns = @JoinColumn(name = "turma_id")
    )
    private List<TurmaEntity> turmasComoAluno;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "professor_turmas",
            joinColumns = @JoinColumn(name = "professor_id"),
            inverseJoinColumns = @JoinColumn(name = "turma_id")
    )
    private List<TurmaEntity> turmasComoProfessor;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<NotificadorEntity> notificadores;
}