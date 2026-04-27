package com.etec.zl.conecta.Domain.Entities.Solicitations;

import com.etec.zl.conecta.Domain.Exceptions.InvalidDataException;
import com.etec.zl.conecta.Domain.ValueObjects.Email;
import com.etec.zl.conecta.Domain.ValueObjects.Name;
import com.etec.zl.conecta.Domain.ValueObjects.TypeRequirement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Solicitation")
class SolicitationTest {

    // ─── Fixtures ────────────────────────────────────────────────────────────

    private static final String ID_SOLICITER  = "user-123";
    private static final Name   NOME          = new Name("Arthur Henrique");
    private static final Email  EMAIL         = new Email("arthur@etec.sp.gov.br");
    private static final List<String> CURSOS  = List.of("curso-1", "curso-2");

    private Solicitation buildSolicitation(TypeRequirement type, String other) {
        return new Solicitation(type, other, ID_SOLICITER, NOME, EMAIL, CURSOS);
    }

    private Solicitation buildFullSolicitation(boolean solved) {
        return new Solicitation(
                UUID.randomUUID(),
                TypeRequirement.DECLARAÇÃO_DE_MATRICULA,
                null,
                solved,
                ID_SOLICITER,
                NOME,
                EMAIL,
                CURSOS,
                Instant.now()
        );
    }

    // ─── Constructor tests ───────────────────────────────────────────────────

    @Nested
    @DisplayName("Creation constructor")
    class CreationConstructor {

        @Test
        @DisplayName("should generate a random UUID on creation")
        void shouldGenerateId() {
            var s1 = buildSolicitation(TypeRequirement.CERTIFICADO_ATUAL, null);
            var s2 = buildSolicitation(TypeRequirement.CERTIFICADO_ATUAL, null);

            assertThat(s1.getId()).isNotNull();
            assertThat(s2.getId()).isNotNull();
            assertThat(s1.getId()).isNotEqualTo(s2.getId());
        }

        @Test
        @DisplayName("should start as unsolved")
        void shouldStartUnsolved() {
            var solicitation = buildSolicitation(TypeRequirement.DECLARAÇÃO_DE_MATRICULA, null);

            assertThat(solicitation.isSolved()).isFalse();
        }

        @Test
        @DisplayName("should set createdAt to now")
        void shouldSetCreatedAt() {
            var before = Instant.now().minusSeconds(1);
            var solicitation = buildSolicitation(TypeRequirement.DECLARAÇÃO_DE_MATRICULA, null);
            var after = Instant.now().plusSeconds(1);

            assertThat(solicitation.getCreatedAt()).isBetween(before, after);
        }

        @Test
        @DisplayName("should store otherRequirement when type is OUTRO")
        void shouldStoreOtherRequirementWhenTypeIsOutro() {
            var solicitation = buildSolicitation(TypeRequirement.OUTRO, "Preciso de atestado");

            assertThat(solicitation.getOtherRequirement()).isEqualTo("Preciso de atestado");
        }

        @Test
        @DisplayName("should map all fields correctly")
        void shouldMapAllFields() {
            var solicitation = buildSolicitation(TypeRequirement.CERTIFICADO_ATUAL, null);

            assertThat(solicitation.getIdSoliciter()).isEqualTo(ID_SOLICITER);
            assertThat(solicitation.getNome()).isEqualTo(NOME);
            assertThat(solicitation.getEmailSoliciter()).isEqualTo(EMAIL);
            assertThat(solicitation.getIdCursos()).containsExactlyElementsOf(CURSOS);
            assertThat(solicitation.getTypeRequirement()).isEqualTo(TypeRequirement.CERTIFICADO_ATUAL);
        }
    }

    // ─── Full constructor (reconstitution from persistence) ──────────────────

    @Nested
    @DisplayName("Reconstitution constructor")
    class ReconstitutionConstructor {

        @Test
        @DisplayName("should preserve all provided values")
        void shouldPreserveAllValues() {
            var id        = UUID.randomUUID();
            var createdAt = Instant.parse("2025-01-15T10:00:00Z");

            var solicitation = new Solicitation(
                    id,
                    TypeRequirement.OUTRO,
                    "Atestado de frequência",
                    false,
                    ID_SOLICITER,
                    NOME,
                    EMAIL,
                    CURSOS,
                    createdAt
            );

            assertThat(solicitation.getId()).isEqualTo(id);
            assertThat(solicitation.getTypeRequirement()).isEqualTo(TypeRequirement.OUTRO);
            assertThat(solicitation.getOtherRequirement()).isEqualTo("Atestado de frequência");
            assertThat(solicitation.isSolved()).isFalse();
            assertThat(solicitation.getCreatedAt()).isEqualTo(createdAt);
        }

        @Test
        @DisplayName("should null otherRequirement when type is not OUTRO")
        void shouldNullOtherRequirementWhenTypeIsNotOutro() {
            var solicitation = new Solicitation(
                    UUID.randomUUID(),
                    TypeRequirement.DECLARAÇÃO_DE_MATRICULA,
                    "isso não deveria ser salvo",
                    false,
                    ID_SOLICITER,
                    NOME,
                    EMAIL,
                    CURSOS,
                    Instant.now()
            );

            assertThat(solicitation.getOtherRequirement()).isNull();
        }
    }

    // ─── solveRequirement ────────────────────────────────────────────────────

    @Nested
    @DisplayName("solveRequirement()")
    class SolveRequirement {

        @Test
        @DisplayName("should mark solicitation as solved")
        void shouldMarkAsSolved() {
            var solicitation = buildFullSolicitation(false);

            solicitation.solveRequirement();

            assertThat(solicitation.isSolved()).isTrue();
        }

        @Test
        @DisplayName("should throw InvalidDataException when already solved")
        void shouldThrowWhenAlreadySolved() {
            var solicitation = buildFullSolicitation(true);

            assertThatThrownBy(solicitation::solveRequirement)
                    .isInstanceOf(InvalidDataException.class)
                    .hasMessageContaining("já resolvido");
        }

        @Test
        @DisplayName("should be idempotent only for the first call")
        void shouldFailOnDoubleResolve() {
            var solicitation = buildFullSolicitation(false);
            solicitation.solveRequirement();

            assertThatThrownBy(solicitation::solveRequirement)
                    .isInstanceOf(InvalidDataException.class);
        }
    }
}