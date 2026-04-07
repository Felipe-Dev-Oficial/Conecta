package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Adapters;

import com.etec.zl.conecta.Application.DTOs.Statements.DTOLeitura;
import com.etec.zl.conecta.Domain.Entities.FAQs.FAQ;
import com.etec.zl.conecta.Domain.Entities.Messages.Message;
import com.etec.zl.conecta.Domain.Entities.Statements.Statement;
import com.etec.zl.conecta.Domain.ValueObjects.*;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Adapters.FAQRepositoryAdapter;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Adapters.MessageRepositoryAdapter;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Adapters.StatementRepositoryAdapter;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Entities.FAQEntity;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Entities.MessageEntity;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Entities.StatementEntity;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Mappers.FAQAdapterMapper;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Mappers.MessageAdapterMapper;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Mappers.StatementAdapterMapper;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Repositories.MongoFAQRepository;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Repositories.MongoMessageRepository;
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

//        @Test
//        @DisplayName("Deve usar o caso EX_ALUNO quando o tipo for DESATIVADO")
//        void findStatements_ForDesativado_ShouldUseExAlunoMethod() {
//            // Este teste valida se o seu switch lida com a divergência de nomes
//            DTOLeitura dto = new DTOLeitura(Tipo.DESATIVADO, null);
//            when(mongoRepository.findStatementsForExAluno(any())).thenReturn(Page.empty());
//
//            adapter.findStatements(dto, new PageRequest(0, 10));
//
//            verify(mongoRepository).findStatementsForExAluno(any());
//        }
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
}