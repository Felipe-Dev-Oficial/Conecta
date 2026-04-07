package com.etec.zl.conecta.Application.Ports.Output.Repositories;

import com.etec.zl.conecta.Domain.Entities.Users.User;
import com.etec.zl.conecta.Domain.ValueObjects.Email;
import com.etec.zl.conecta.Domain.ValueObjects.Name;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    void save(User user);
    Optional<User> findByEmail(Email email);
    List<UUID> retornarTurmasRelacionadas(String id);

    //secretaria
    Optional<User> findById(String id);
    PageResult<User> findAllCursantes(PageRequest pageable);
    PageResult<User> findAllFuncionarios(PageRequest pageable);
    PageResult<User> findAllByTurma(String idTurma, PageRequest pageable);
    PageResult<User> findAll(PageRequest pageable);
    PageResult<User> findAllByName(Name name, PageRequest pageable);
    PageResult<User> findAllSecretaria(PageRequest pageable);
    void delete(String id);

    //professores
    Optional<User> findByIdAlunos(String id, String id_aluno);
    PageResult<User> findAllAlunos(String id, PageRequest pageable);
    PageResult<User> findAllAlunosByTurma(String id, String idTurma, PageRequest pageable);
    PageResult<User> findAllAlunosByNome(String id, Name name, PageRequest pageable);

    //alunos
    Optional<User> findByIdProfessores(String id, String id_professor);
    PageResult<User> findAllProfessores(String id, PageRequest pageable);
}
