package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Mappers;

import com.etec.zl.conecta.Domain.Entities.Messages.Message;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Entities.MessageEntity;
import org.springframework.stereotype.Component;

@Component
public class MessageAdapterMapper {

    public Message toDomain(MessageEntity entity){
        return new Message(
                entity.getId(),
                entity.getIdSender(),
                entity.getIdReceiver(),
                entity.getTimestamp(),
                entity.getContent(),
                entity.getMidia()
        );
    }
    public MessageEntity toEntity(Message domain){
        return new MessageEntity(
                domain.getId(),
                domain.getIdSender(),
                domain.getIdReceiver(),
                domain.getTimestamp(),
                domain.getContent(),
                domain.getMidia()
        );
    }
}
