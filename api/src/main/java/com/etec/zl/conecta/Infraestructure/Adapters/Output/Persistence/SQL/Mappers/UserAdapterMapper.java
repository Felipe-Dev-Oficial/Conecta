package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Mappers;

import com.etec.zl.conecta.Domain.Entities.Users.User;
import com.etec.zl.conecta.Domain.ValueObjects.*;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Entities.TurmaEntity;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Entities.UserEntity;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Repositories.JpaTurmaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserAdapterMapper {

    private final PasswordEncoder passwordEncoder;
    private final JpaTurmaRepository turmaRepository;
    private static final String BCRYPT_PATTERN = "^\\$2[aby]?\\$\\d{2}\\$.{53}$";

    public User toDomain(UserEntity entity){
        var name = new Name(entity.getNome());
        var email = new Email(entity.getEmail());
        var number = new PhoneNumber(entity.getNumero());
        var senha = new Password(entity.getSenha());
        var passwordUpdater = new TokenUpdater(entity.getPasswordUpdaterToken(), entity.getPasswordTokenExpiration());
        var emailUpdater = new TokenUpdater(entity.getEmailUpdaterToken(), entity.getEmailTokenExpiration());
        var turmas = extrairIdsTurmas(entity);

        return new User(
                entity.getId(),
                name,
                email,
                number,
                senha,
                entity.getTipo(),
                passwordUpdater,
                emailUpdater,
                turmas
        );
    }
    private List<UUID> extrairIdsTurmas(UserEntity entity) {
        List<TurmaEntity> turmas = switch (entity.getTipo()) {
            case ALUNO -> entity.getTurmasComoAluno();
            case PROFESSOR -> entity.getTurmasComoProfessor();
            default -> null;
        };

        if (turmas == null) return new ArrayList<>();

        return turmas.stream()
                .map(TurmaEntity::getId)
                .toList();
    }
    public UserEntity toEntity(User domain) {
        UserEntity entity = new UserEntity();

        String senhaOriginal = domain.getSenha().password();
        String senhaFinal = (senhaOriginal != null && senhaOriginal.matches(BCRYPT_PATTERN))
                ? senhaOriginal
                : passwordEncoder.encode(senhaOriginal);

        entity.setId(domain.getId());
        entity.setNome(domain.getNome() != null ? domain.getNome().name() : null);
        entity.setEmail(domain.getEmail() != null ? domain.getEmail().email() : null);
        entity.setNumero(domain.getNumero() != null ? domain.getNumero().number() : null);
        entity.setSenha(senhaFinal);
        entity.setTipo(domain.getTipo());

        if (domain.getPasswordUpdater() != null) {
            entity.setPasswordUpdaterToken(domain.getPasswordUpdater().token());
            entity.setPasswordTokenExpiration(domain.getPasswordUpdater().expiration());
        }

        if (domain.getEmailUpdater() != null) {
            entity.setEmailUpdaterToken(domain.getEmailUpdater().token());
            entity.setEmailTokenExpiration(domain.getEmailUpdater().expiration());
        }

        List<TurmaEntity> turmas = domain.getTurmasIds() == null ? List.of() :
                domain.getTurmasIds().stream()
                        .flatMap(id -> turmaRepository.findById(id).stream())
                        .toList();

        switch (domain.getTipo()) {
            case ALUNO -> {
                entity.setTurmasComoAluno(turmas);
                entity.setTurmasComoProfessor(List.of());
            }
            case PROFESSOR -> {
                entity.setTurmasComoAluno(List.of());
                entity.setTurmasComoProfessor(turmas);
            }
            default -> {
                entity.setTurmasComoAluno(List.of());
                entity.setTurmasComoProfessor(List.of());
            }
        }

        return entity;
    }
}
