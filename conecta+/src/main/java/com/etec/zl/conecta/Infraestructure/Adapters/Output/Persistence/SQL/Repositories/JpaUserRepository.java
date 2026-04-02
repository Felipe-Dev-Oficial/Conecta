package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Repositories;

import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Entities.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaUserRepository extends JpaRepository<UserEntity, String> {

    @Query(value = """
        SELECT CAST(turma_id AS TEXT) FROM aluno_turmas WHERE aluno_id = :userId
        UNION
        SELECT CAST(turma_id AS TEXT) FROM professor_turmas WHERE professor_id = :userId
        """, nativeQuery = true)
    List<String> findRelatedTurmasIds(@Param("userId") String userId);

    @Query(value = """
        SELECT u.* FROM users u
        INNER JOIN aluno_turmas at ON u.id = at.aluno_id
        INNER JOIN turmas t ON at.turma_id = t.id
        WHERE t.status = 'ON' AND u.tipo = 'ALUNO'
        """,
            countQuery = """
        SELECT count(*) FROM users u
        INNER JOIN aluno_turmas at ON u.id = at.aluno_id
        INNER JOIN turmas t ON at.turma_id = t.id
        WHERE t.status = 'ON' AND u.tipo = 'ALUNO'
        """,
            nativeQuery = true)
    Page<UserEntity> findAllCursantes(Pageable pageable);

    @Query(value = """
    SELECT u.* FROM users u
    INNER JOIN aluno_turmas at ON u.id = at.aluno_id
    WHERE at.turma_id = :idTurma
    UNION
    SELECT u.* FROM users u
    INNER JOIN professor_turmas pt ON u.id = pt.professor_id
    WHERE pt.turma_id = :idTurma
    """,
            countQuery = """
    SELECT (
        (SELECT count(*) FROM aluno_turmas WHERE turma_id = :idTurma) +
        (SELECT count(*) FROM professor_turmas WHERE turma_id = :idTurma)
    )
    """,
            nativeQuery = true)
    Page<UserEntity> findAllPeopleByTurma(@Param("idTurma") UUID idTurma, Pageable pageable);

    @Query(value = "SELECT * FROM users WHERE nome ILIKE %:nome%",
            countQuery = "SELECT count(*) FROM users WHERE nome ILIKE %:nome%",
            nativeQuery = true)
    Page<UserEntity> findByNomeContaining(@Param("nome") String nome, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users SET tipo = 'DESATIVADO' WHERE id = :id", nativeQuery = true)
    void logicDelete(@Param("id") String id);

    @Query(value = """
    SELECT u.* FROM users u
    INNER JOIN aluno_turmas at ON u.id = at.aluno_id
    WHERE u.id = :idAluno
    AND at.turma_id IN (
        SELECT pt.turma_id
        FROM professor_turmas pt
        WHERE pt.professor_id = :idProfessor
    )
    """, nativeQuery = true)
    Optional<UserEntity> findAlunoByProfessor(@Param("idProfessor") String idProfessor, @Param("idAluno") String idAluno);

    @Query(value = """
        SELECT DISTINCT u.* FROM users u
        INNER JOIN aluno_turmas at ON u.id = at.aluno_id
        INNER JOIN professor_turmas pt ON at.turma_id = pt.turma_id
        WHERE pt.professor_id = :idProfessor
        AND u.tipo = 'ALUNO'
        """,
            countQuery = """
        SELECT count(DISTINCT at.aluno_id)
        FROM aluno_turmas at
        INNER JOIN professor_turmas pt ON at.turma_id = pt.turma_id
        WHERE pt.professor_id = :idProfessor
        """,
            nativeQuery = true)
    Page<UserEntity> findAllAlunosByProfessor(@Param("idProfessor") String idProfessor, Pageable pageable);

    @Query(value = """
        SELECT u.* FROM users u
        INNER JOIN aluno_turmas at ON u.id = at.aluno_id
        WHERE at.turma_id = :idTurma
        AND EXISTS (
            SELECT 1 FROM professor_turmas pt
            WHERE pt.turma_id = :idTurma
            AND pt.professor_id = :idProfessor
        )
        """,
            countQuery = """
        SELECT count(*) FROM aluno_turmas at
        WHERE at.turma_id = :idTurma
        AND EXISTS (
            SELECT 1 FROM professor_turmas pt
            WHERE pt.turma_id = :idTurma
            AND pt.professor_id = :idProfessor
        )
        """,
            nativeQuery = true)
    Page<UserEntity> findAlunosByTurmaWhereProfessorHasAccess(
            @Param("idProfessor") String idProfessor,
            @Param("idTurma") UUID idTurma,
            Pageable pageable
    );

    @Query(value = """
        SELECT DISTINCT u.* FROM users u
        INNER JOIN aluno_turmas at ON u.id = at.aluno_id
        INNER JOIN professor_turmas pt ON at.turma_id = pt.turma_id
        WHERE pt.professor_id = :idProfessor
        AND u.nome ILIKE %:nome%
        AND u.tipo = 'ALUNO'
        """,
            countQuery = """
        SELECT count(DISTINCT u.id) FROM users u
        INNER JOIN aluno_turmas at ON u.id = at.aluno_id
        INNER JOIN professor_turmas pt ON at.turma_id = pt.turma_id
        WHERE pt.professor_id = :idProfessor
        AND u.nome ILIKE %:nome%
        AND u.tipo = 'ALUNO'
        """,
            nativeQuery = true)
    Page<UserEntity> findAlunosByNameAndProfessor(
            @Param("idProfessor") String idProfessor,
            @Param("nome") String nome,
            Pageable pageable
    );

    @Query(value = """
        SELECT u.* FROM users u
        INNER JOIN professor_turmas pt ON u.id = pt.professor_id
        WHERE u.id = :idProfessor
        AND u.tipo = 'PROFESSOR'
        AND EXISTS (
            SELECT 1 FROM aluno_turmas at
            WHERE at.turma_id = pt.turma_id
            AND at.aluno_id = :idAluno
        )
        """, nativeQuery = true)
    Optional<UserEntity> findProfessorIfIsFromStudentTurma(
            @Param("idAluno") String idAluno,
            @Param("idProfessor") String idProfessor
    );

    @Query(value = """
        SELECT DISTINCT u.* FROM users u
        INNER JOIN professor_turmas pt ON u.id = pt.professor_id
        INNER JOIN aluno_turmas at ON pt.turma_id = at.turma_id
        WHERE at.aluno_id = :idAluno
        AND u.tipo = 'PROFESSOR'
        """,
            countQuery = """
        SELECT count(DISTINCT pt.professor_id)
        FROM professor_turmas pt
        INNER JOIN aluno_turmas at ON pt.turma_id = at.turma_id
        WHERE at.aluno_id = :idAluno
        """,
            nativeQuery = true)
    Page<UserEntity> findAllProfessoresByAluno(@Param("idAluno") String idAluno, Pageable pageable);

    Page<UserEntity> findByTipoIn(List<String> tipos, Pageable pageable);

    Optional<UserEntity> findByEmail(String email);
}