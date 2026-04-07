package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Mappers;

import com.etec.zl.conecta.Domain.Entities.Turmas.Turma;
import com.etec.zl.conecta.Domain.Entities.Users.User;
import com.etec.zl.conecta.Domain.ValueObjects.*;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Entities.TurmaEntity;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Entities.UserEntity;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Repositories.JpaTurmaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SQLAdaptersMappersTests {

    @Mock private PasswordEncoder encoder;

    @InjectMocks private UserAdapterMapper userMapper;
    private final TurmaAdapterMapper turmaMapper = new TurmaAdapterMapper();

    private final String SENHA_VALIDA_BRUTA = "Senha@123";
    private final String HASH_BCRYPT_VALIDO = "$2a$10$8K1p/a0v1A5p7X6H6p6e6uV9Z6Q5W4E3R2T1Y0U9I8O7P6A5S4D3F";

    @Test
    @DisplayName("Deve mapear TurmaEntity para Domain e vice-versa")
    void deveMapearTurma() {
        TurmaEntity entity = new TurmaEntity("DS-2024", Cursos.DESENVOLVIMENTO_DE_SISTEMAS, 3, 1, Status.ON);
        Turma domain = turmaMapper.toDomain(entity);

        assertAll("Mapeamento de Turma",
                () -> assertEquals(entity.getId(), domain.getId()),
                () -> assertEquals(entity.getCurso(), domain.getCurso()),
                () -> assertEquals(entity.getStatus(), domain.getStatus())
        );

        TurmaEntity back = turmaMapper.toEntity(domain);
        assertEquals(domain.getId(), back.getId());
    }

    @Test
    @DisplayName("Deve mapear UserEntity para Domain (Senha já é Hash)")
    void deveMapearUserEntityParaDomain() {
        UserEntity entity = new UserEntity();
        entity.setId("123");
        entity.setNome("Arthur Silva");
        entity.setEmail("arthur@etec.sp.gov.br");
        entity.setNumero("11999999999");
        entity.setSenha(HASH_BCRYPT_VALIDO);
        entity.setTipo(Tipo.ALUNO);

        User domain = userMapper.toDomain(entity);

        assertEquals("Arthur Silva", domain.getNome().name());
        assertEquals(HASH_BCRYPT_VALIDO, domain.getSenha().password());
    }

    @Test
    @DisplayName("Deve criptografar senha ao mapear Domain para Entity se não for hash")
    void deveCriptografarSenhaAoMapearParaEntity() {
        // Given
        given(encoder.encode(anyString())).willReturn("new-hash-123");

        User domain = new User(
                "123",
                new Name("Arthur Silva"),
                new Email("arthur@etec.sp.gov.br"),
                new PhoneNumber("11999999999"),
                new Password(SENHA_VALIDA_BRUTA),
                Tipo.ALUNO,
                List.of()
        );

        UserEntity entity = userMapper.toEntity(domain);

        assertEquals("new-hash-123", entity.getSenha());
    }

    @Test
    @DisplayName("Não deve re-criptografar senha se ela já for um hash BCrypt")
    void naoDeveReCriptografarSeJaForHash() {
        User domain = new User(
                "123",
                new Name("Arthur Silva"),
                new Email("arthur@etec.sp.gov.br"),
                new PhoneNumber("11999999999"),
                new Password(HASH_BCRYPT_VALIDO),
                Tipo.ALUNO,
                List.of()
        );

        UserEntity entity = userMapper.toEntity(domain);

        assertEquals(HASH_BCRYPT_VALIDO, entity.getSenha());
    }
}