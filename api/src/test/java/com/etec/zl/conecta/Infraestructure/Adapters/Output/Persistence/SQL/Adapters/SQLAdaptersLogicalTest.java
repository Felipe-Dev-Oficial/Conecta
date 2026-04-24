package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Adapters;

import com.etec.zl.conecta.Domain.Entities.Turmas.Turma;
import com.etec.zl.conecta.Domain.Entities.Users.User;
import com.etec.zl.conecta.Domain.Exceptions.UserNotFoundException;
import com.etec.zl.conecta.Domain.ValueObjects.*;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Adapters.TurmaRepositoryAdapter;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Adapters.UserRepositoryAdapter;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Entities.TurmaEntity;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Entities.UserEntity;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Mappers.TurmaAdapterMapper;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Mappers.UserAdapterMapper;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Repositories.JpaTurmaRepository;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Repositories.JpaUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SQLAdaptersLogicalTest {

    @Nested
    @DisplayName("Testes do UserRepositoryAdapter")
    class UserTests {
        @Mock
        private JpaUserRepository jpaRepository;
        @Mock
        private UserAdapterMapper mapper;
        @InjectMocks
        private UserRepositoryAdapter adapter;

        @Test
        @DisplayName("save() deve delegar para o repository após mapeamento")
        void save_ShouldCallJpaRepository() {
            User user = mock(User.class);
            UserEntity entity = new UserEntity();
            when(mapper.toEntity(user)).thenReturn(entity);

            adapter.save(user);

            verify(jpaRepository).save(entity);
        }

        @Test
        @DisplayName("findByEmail() deve retornar o domínio mapeado")
        void findByEmail_ShouldReturnMappedUser() {
            Email email = new Email("teste@etec.sp.gov.br");
            UserEntity entity = new UserEntity();
            when(jpaRepository.findByEmail(email.email())).thenReturn(Optional.of(entity));
            when(mapper.toDomain(entity)).thenReturn(mock(User.class));

            Optional<User> result = adapter.findByEmail(email);

            assertThat(result).isPresent();
            verify(jpaRepository).findByEmail(email.email());
        }

        @Test
        @DisplayName("delete() deve invocar exclusão lógica")
        void delete_ShouldCallLogicDelete() {
            String id = UUID.randomUUID().toString();

            adapter.delete(id);

            verify(jpaRepository).logicDelete(id);
        }
        @Test
        @DisplayName("deleteNotificador() deve lançar UserNotFoundException quando usuário não existe")
        void deleteNotificador_ShouldThrowWhenUserNotFound() {
            when(jpaRepository.findById("id-inexistente")).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class,
                    () -> adapter.deleteNotificador("id-inexistente", "https://push.example.com/ep"));
        }

        @Test
        @DisplayName("deleteNotificador() não deve chamar save() — remoção é gerenciada pelo cascade da entidade")
        void deleteNotificador_ShouldNotCallSaveExplicitly() {
            var entity = new UserEntity();
            entity.setNotificadores(new ArrayList<>());
            when(jpaRepository.findById("user-1")).thenReturn(Optional.of(entity));

            adapter.deleteNotificador("user-1", "https://push.example.com/ep");

            verify(jpaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Testes do TurmaRepositoryAdapter")
    class TurmaTests {
        @Mock
        private JpaTurmaRepository jpaRepository;
        @Mock
        private TurmaAdapterMapper mapper;
        @InjectMocks
        private TurmaRepositoryAdapter adapter;

        @Test
        @DisplayName("passaModulo() deve invocar lógica de calendário no repositório")
        void passaModulo_ShouldInvokeRepositoryMethod() {
            adapter.passaModulo();
            verify(jpaRepository).passaModuloBaseadoNoCalendario();
        }

        @Test
        @DisplayName("findAllTurmasAtuais() deve filtrar por status ON")
        void findAllAtuais_ShouldFilterByStatus() {
            when(jpaRepository.findByStatus(eq(Status.ON), any())).thenReturn(Page.empty());

            adapter.findAllTurmasAtuais(new PageRequest(0, 10));

            verify(jpaRepository).findByStatus(eq(Status.ON), any());
        }

        @Test
        @DisplayName("findById() deve buscar e converter para domínio")
        void findById_ShouldCallJpaAndMapper() {
            String id = UUID.randomUUID().toString();
            TurmaEntity entity = new TurmaEntity();
            when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));
            when(mapper.toDomain(entity)).thenReturn(mock(Turma.class));

            Optional<Turma> result = adapter.findById(id);

            assertThat(result).isPresent();
            verify(mapper).toDomain(entity);
        }
    }
}