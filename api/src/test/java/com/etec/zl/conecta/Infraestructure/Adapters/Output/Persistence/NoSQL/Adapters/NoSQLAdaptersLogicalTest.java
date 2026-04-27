package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Adapters;

import com.etec.zl.conecta.Application.DTOs.Statements.DTOLeitura;
import com.etec.zl.conecta.Domain.Entities.FAQs.FAQ;
import com.etec.zl.conecta.Domain.Entities.Solicitations.Solicitation;
import com.etec.zl.conecta.Domain.Entities.Statements.Statement;
import com.etec.zl.conecta.Domain.ValueObjects.*;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Entities.FAQEntity;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Entities.SolicitationEntity;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Entities.StatementEntity;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Mappers.FAQAdapterMapper;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Mappers.MessageAdapterMapper;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Mappers.SolicitationAdapterMapper;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Mappers.StatementAdapterMapper;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Repositories.MongoFAQRepository;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Repositories.MongoMessageRepository;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Repositories.MongoSolicitationRepository;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Repositories.MongoStatementRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.SliceImpl;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoSQLAdaptersLogicalTest {

    @Nested
    @DisplayName("Testes do StatementRepositoryAdapter")
    class StatementTests {
        @Mock private MongoStatementRepository mongoRepository;
        @Mock private StatementAdapterMapper mapper;
        @InjectMocks private StatementRepositoryAdapter adapter;

        @Test
        @DisplayName("Deve salvar chamando o mapper e o repositório externo")
        void save_ShouldCallDependencies() {
            Statement domain = mock(Statement.class);
            StatementEntity entity = new StatementEntity();
            when(mapper.toEntity(domain)).thenReturn(entity);

            adapter.save(domain);

            verify(mapper).toEntity(domain);
            verify(mongoRepository).save(entity);
        }

        @Test
        @DisplayName("Deve filtrar corretamente para ALUNO no switch")
        void findStatements_ForAluno_ShouldUseCorrectMethod() {
            List<String> turmas = List.of("turma1");
            DTOLeitura dto = new DTOLeitura(Tipo.ALUNO, turmas);
            when(mongoRepository.findStatementsForAluno(eq(turmas), any())).thenReturn(Page.empty());

            adapter.findStatements(dto, new PageRequest(0, 10));

            verify(mongoRepository).findStatementsForAluno(eq(turmas), any());
        }
    }

    @Nested
    @DisplayName("Testes do FAQRepositoryAdapter")
    class FAQTests {
        @Mock private MongoFAQRepository mongoRepository;
        @Mock private FAQAdapterMapper mapper;
        @InjectMocks private FAQRepositoryAdapter adapter;

        @Test
        @DisplayName("Deve mapear lista de FAQs ativos")
        void getAllActives_ShouldMapResults() {
            FAQEntity entity = new FAQEntity();
            when(mongoRepository.findAllActives(any())).thenReturn(new PageImpl<>(List.of(entity)));
            when(mapper.toDomain(entity)).thenReturn(mock(FAQ.class));

            PageResult<FAQ> result = adapter.getAllActives(new PageRequest(0, 10));

            assertThat(result.content()).hasSize(1);
            verify(mapper).toDomain(any());
        }
    }

    @Nested
    @DisplayName("Testes do MessageRepositoryAdapter")
    class MessageTests {
        @Mock private MongoMessageRepository mongoRepository;
        @Mock private MessageAdapterMapper mapper;
        @InjectMocks private MessageRepositoryAdapter adapter;

        @Test
        @DisplayName("ListarMensagens() deve invocar consulta entre remetente e destinatário")
        void listarMensagens_ShouldQueryChatBetweenUsers() {
            String sender = "user-1";
            String receiver = "user-2";
            when(mongoRepository.findChatBetween(eq(sender), eq(receiver), any())).thenReturn(Page.empty());

            adapter.ListarMensagens(sender, receiver, new PageRequest(0, 10));

            verify(mongoRepository).findChatBetween(eq(sender), eq(receiver), any());
        }

        @Test
        @DisplayName("Deve retornar slice de contatos convertidos")
        void contatos_ShouldReturnSlice() {
            String id = "meu-id";
            when(mongoRepository.findDistinctContactIds(eq(id), any()))
                    .thenReturn(new SliceImpl<>(List.of("contato1", "contato2")));

            SliceResult<String> result = adapter.contatos(id, new PageRequest(0, 10));

            assertThat(result.content()).containsExactly("contato1", "contato2");
        }
    }

    // ─── SolicitationRepositoryAdapter ───────────────────────────────────────

    @Nested
    @DisplayName("Testes do SolicitationRepositoryAdapter")
    class SolicitationTests {

        @Mock private MongoSolicitationRepository mongoRepository;
        @Mock private SolicitationAdapterMapper mapper;
        @InjectMocks private SolicitationRepositoryAdapter adapter;

        private SolicitationEntity entityFactory() {
            return new SolicitationEntity(
                    UUID.randomUUID(),
                    TypeRequirement.DECLARAÇÃO_DE_MATRICULA,
                    null, false,
                    "user-1", "Arthur", "arthur@etec.sp.gov.br",
                    List.of("curso-1"),
                    Instant.now()
            );
        }

        private Solicitation domainFactory() {
            return new Solicitation(
                    UUID.randomUUID(),
                    TypeRequirement.DECLARAÇÃO_DE_MATRICULA,
                    null, false,
                    "user-1",
                    new Name("Arthur"),
                    new Email("arthur@etec.sp.gov.br"),
                    List.of("curso-1"),
                    Instant.now()
            );
        }

        @Test
        @DisplayName("saveSolicitation() deve converter domínio para entidade e delegar ao repositório")
        void saveSolicitation_ShouldConvertAndDelegate() {
            var domain = domainFactory();
            var entity = entityFactory();
            when(mapper.toEntity(domain)).thenReturn(entity);

            adapter.saveSolicitation(domain);

            verify(mapper).toEntity(domain);
            verify(mongoRepository).save(entity);
        }

        @Test
        @DisplayName("getSolicitationById() deve retornar Optional com domain quando encontrado")
        void getSolicitationById_ShouldReturnMappedDomain() {
            var id     = UUID.randomUUID();
            var entity = entityFactory();
            var domain = domainFactory();
            when(mongoRepository.findById(id)).thenReturn(Optional.of(entity));
            when(mapper.toDomain(entity)).thenReturn(domain);

            var result = adapter.getSolicitationById(id);

            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("getSolicitationById() deve retornar Optional vazio quando não encontrado")
        void getSolicitationById_ShouldReturnEmptyWhenNotFound() {
            var id = UUID.randomUUID();
            when(mongoRepository.findById(id)).thenReturn(Optional.empty());

            var result = adapter.getSolicitationById(id);

            assertThat(result).isEmpty();
            verify(mapper, never()).toDomain(any());
        }

        @Test
        @DisplayName("getSolicitationsByUser() deve invocar findBySolicitatorId com id e pageable corretos")
        void getSolicitationsByUser_ShouldQueryByUserId() {
            String userId = "user-1";
            when(mongoRepository.findBySolicitatorId(eq(userId), any())).thenReturn(Page.empty());

            adapter.getSolicitationsByUser(userId, new PageRequest(0, 10));

            verify(mongoRepository).findBySolicitatorId(eq(userId), any());
        }

        @Test
        @DisplayName("getSolicitationsByUser() deve retornar page mapeada corretamente")
        void getSolicitationsByUser_ShouldReturnMappedPage() {
            String userId = "user-1";
            var entity = entityFactory();
            var domain = domainFactory();
            when(mongoRepository.findBySolicitatorId(eq(userId), any()))
                    .thenReturn(new PageImpl<>(List.of(entity)));
            when(mapper.toDomain(entity)).thenReturn(domain);

            var result = adapter.getSolicitationsByUser(userId, new PageRequest(0, 10));

            assertThat(result.content()).hasSize(1);
            assertThat(result.content().get(0)).isEqualTo(domain);
        }

        /**
         * ATENÇÃO: getSolicitationsBySearch() chama findBySolicitatorId DUAS vezes
         * quando há resultado — uma para o .hasContent() e outra para iterar o map().
         * O mock precisa estar configurado para retornar conteúdo nas duas chamadas,
         * por isso usamos thenReturn encadeado ou um único stub consistente.
         *
         * Este comportamento é um candidato a refatoração no adapter: o ideal seria
         * armazenar o resultado da primeira chamada em uma variável local para evitar
         * a query duplicada ao banco.
         */
        @Test
        @DisplayName("getSolicitationsBySearch() deve usar findBySolicitatorId quando há resultado por id")
        void getSolicitationsBySearch_ShouldPreferIdSearchWhenItHasContent() {
            String search = "user-1";
            var entity    = entityFactory();
            var domain    = domainFactory();

            // Stub retorna conteúdo nas duas invocações de findBySolicitatorId
            // (1ª: checagem .hasContent(); 2ª: .map(mapper::toDomain))
            when(mongoRepository.findBySolicitatorId(eq(search), any()))
                    .thenReturn(new PageImpl<>(List.of(entity)));
            when(mapper.toDomain(entity)).thenReturn(domain);

            var result = adapter.getSolicitationsBySearch(search, new PageRequest(0, 10));

            assertThat(result.content()).hasSize(1);
            // findBySolicitatorId deve ser chamado 2x; findByNomeStartingWith nunca
            verify(mongoRepository, times(2)).findBySolicitatorId(eq(search), any());
            verify(mongoRepository, never()).findByNomeStartingWith(any(), any());
        }

        @Test
        @DisplayName("getSolicitationsBySearch() deve fallback para findByNomeStartingWith quando não há resultado por id")
        void getSolicitationsBySearch_ShouldFallbackToNameWhenIdReturnsEmpty() {
            String search = "Arthur";
            var entity    = entityFactory();
            var domain    = domainFactory();

            when(mongoRepository.findBySolicitatorId(eq(search), any())).thenReturn(Page.empty());
            when(mongoRepository.findByNomeStartingWith(eq(search), any()))
                    .thenReturn(new PageImpl<>(List.of(entity)));
            when(mapper.toDomain(entity)).thenReturn(domain);

            var result = adapter.getSolicitationsBySearch(search, new PageRequest(0, 10));

            assertThat(result.content()).hasSize(1);
            // findBySolicitatorId só 1x (checagem .hasContent()); fallback dispara o nome
            verify(mongoRepository, times(1)).findBySolicitatorId(eq(search), any());
            verify(mongoRepository).findByNomeStartingWith(eq(search), any());
        }

        @Test
        @DisplayName("getSolicitationsBySearch() deve retornar vazio quando nem id nem nome correspondem")
        void getSolicitationsBySearch_ShouldReturnEmpty_WhenNoMatch() {
            String search = "Xyz";
            when(mongoRepository.findBySolicitatorId(eq(search), any())).thenReturn(Page.empty());
            when(mongoRepository.findByNomeStartingWith(eq(search), any())).thenReturn(Page.empty());

            var result = adapter.getSolicitationsBySearch(search, new PageRequest(0, 10));

            assertThat(result.content()).isEmpty();
            verify(mongoRepository).findBySolicitatorId(eq(search), any());
            verify(mongoRepository).findByNomeStartingWith(eq(search), any());
        }
    }
}