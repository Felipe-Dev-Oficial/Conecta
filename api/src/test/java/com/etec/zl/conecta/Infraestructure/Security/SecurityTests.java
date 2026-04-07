package com.etec.zl.conecta.Infraestructure.Security;

import com.etec.zl.conecta.Domain.Entities.Users.User;
import com.etec.zl.conecta.Domain.ValueObjects.*;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Entities.UserEntity;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Mappers.UserAdapterMapper;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Repositories.JpaTurmaRepository;
import com.etec.zl.conecta.Infraestructure.Security.Service.EncryptorService;
import com.etec.zl.conecta.Infraestructure.Security.Service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SecurityTests {

    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private UserAdapterMapper userMapper;

    private final EncryptorService encryptor = new EncryptorService();
    private final TokenService tokenService = new TokenService();

    private final String SENHA_FORTE = "Conecta@2026";
    private final String HASH_BCRYPT = "$2a$10$8K1p/a0v1A5p7X6H6p6e6uV9Z6Q5W4E3R2T1Y0U9I8O7P6A5S4D3F";

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(tokenService, "secret", "secret-de-teste-com-mais-de-32-caracteres-para-jwt");
    }

    @Nested
    @DisplayName("Testes de Criptografia e Proteção de Dados (AES)")
    class EncryptionTests {
        @Test
        void deveGarantirQueDadosSensiveisSaoCriptografadosEReversiveis() {
            String original = "CPF-123.456.789-00";
            String crypt = encryptor.convertToDatabaseColumn(original);

            assertAll("Integridade AES",
                    () -> assertNotNull(crypt),
                    () -> assertNotEquals(original, crypt),
                    () -> assertEquals(original, encryptor.convertToEntityAttribute(crypt))
            );
        }
    }

    @Nested
    @DisplayName("Testes de Mapeamento e Segurança de Senhas (BCrypt)")
    class MapperAndSecurityTests {

        @Test
        void deveCriptografarSenhaNovaAoMapearParaEntity() {
            given(passwordEncoder.encode(anyString())).willReturn("hash-gerado-pelo-encoder");

            User domain = new User(
                    "ID-01",
                    new Name("Arthur Silva"),
                    new Email("arthur@etec.sp.gov.br"),
                    new PhoneNumber("11999999999"),
                    new Password(SENHA_FORTE),
                    Tipo.ALUNO,
                    List.of()
            );

            UserEntity entity = userMapper.toEntity(domain);
            assertEquals("hash-gerado-pelo-encoder", entity.getSenha());
        }

        @Test
        void naoDeveRe加密SenhaSeJaForUmHashValido() {
            User domain = new User(
                    "ID-01",
                    new Name("Arthur Silva"),
                    new Email("arthur@etec.sp.gov.br"),
                    new PhoneNumber("11999999999"),
                    new Password(HASH_BCRYPT),
                    Tipo.ALUNO,
                    List.of()
            );

            UserEntity entity = userMapper.toEntity(domain);
            assertEquals(HASH_BCRYPT, entity.getSenha(), "O mapper deve manter o hash original se detectar o padrão BCrypt");
        }
    }

    @Nested
    @DisplayName("Testes de Token e Identidade (JWT)")
    class TokenTests {
        @Test
        void deveGerarEValidarTokenComSubjectID() {
            User domain = new User(
                    "user-uuid-123",
                    new Name("Arthur Silva"),
                    new Email("arthur@etec.sp.gov.br"),
                    new PhoneNumber("11999999999"),
                    new Password(HASH_BCRYPT),
                    Tipo.PROFESSOR,
                    List.of()
            );

            String token = tokenService.generateToken(domain);
            String subject = tokenService.validateToken(token);

            assertAll("Validação JWT",
                    () -> assertNotNull(token),
                    () -> assertEquals("user-uuid-123", subject)
            );
        }
    }
}