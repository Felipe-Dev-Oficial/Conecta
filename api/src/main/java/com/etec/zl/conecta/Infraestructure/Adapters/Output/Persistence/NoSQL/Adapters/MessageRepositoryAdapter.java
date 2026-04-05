package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Adapters;

import com.etec.zl.conecta.Application.Ports.Output.Repositories.MessageRepository;
import com.etec.zl.conecta.Domain.Entities.Messages.Message;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import com.etec.zl.conecta.Domain.ValueObjects.SliceResult;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Mappers.MessageAdapterMapper;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Repositories.MongoMessageRepository;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.Services.PaginationAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MessageRepositoryAdapter implements MessageRepository {

    private final MongoMessageRepository externalRepository;
    private final MessageAdapterMapper mapper;

    @Override
    @Caching(evict = {
            @CacheEvict(value = "messages", allEntries = true)
    })
    public void save(Message message) {
        externalRepository.save(mapper.toEntity(message));
    }

    @Override
    @Cacheable(value = "messages", key = "(#id < #idReceiver ? #id + '-' + #idReceiver : #idReceiver + '-' + #id) + '-' + #pageable.page()")
    public PageResult<Message> ListarMensagens(String id, String idReceiver, PageRequest pageable) {
        return PaginationAdapter.toDomain(externalRepository.findChatBetween(id, idReceiver, PaginationAdapter.toSpring(pageable))
                .map(mapper::toDomain));
    }

    @Override
    @Cacheable(value = "messages", key = "(#idSender < #idReceiver ? #idSender + '-' + #idReceiver : #idReceiver + '-' + #idSender) + '-' + #pageable.page()")
    public PageResult<Message> ListarMensagensSecretaria(String idSender, String idReceiver, PageRequest pageable) {
        return PaginationAdapter.toDomain(externalRepository.findChatBetween(idSender, idReceiver, PaginationAdapter.toSpring(pageable))
                .map(mapper::toDomain));
    }

    @Override
    @Cacheable(value = "messages", key = "'contacts-' + #id + '-' + #pageable.page()")
    public SliceResult<String> contatos(String id, PageRequest pageable) {
        return PaginationAdapter.toDomain(externalRepository.findDistinctContactIds(id, PaginationAdapter.toSpring(pageable)));
    }
}
