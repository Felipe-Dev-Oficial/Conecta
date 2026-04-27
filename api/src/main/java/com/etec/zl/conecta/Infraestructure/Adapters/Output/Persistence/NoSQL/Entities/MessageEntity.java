package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Entities;

import com.etec.zl.conecta.Domain.Entities.Midia.Midia;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Document(collection = "messages")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageEntity implements Serializable {

    @MongoId
    private UUID id;
    @Field("sender_id")
    @Indexed
    private String idSender;

    @Field("receiver_id")
    @Indexed
    private String idReceiver;

    @Field("sent_at")
    @Indexed(direction = IndexDirection.DESCENDING)
    private Instant timestamp;

    @Field("message_content")
    private String content;

    @Field("attachments")
    private Midia midia;
}
