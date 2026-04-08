package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Entities;

import com.etec.zl.conecta.Domain.Entities.Midia.Midia;
import com.etec.zl.conecta.Domain.ValueObjects.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Document(collection = "statements")
@CompoundIndex(name = "priority_time_idx", def = "{'priority': -1, 'timestamp': -1}")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatementEntity implements Serializable {

    @MongoId
    private UUID id;
    @Field("sender_id")
    @Indexed
    private String idSender;
    @Field("title")
    private Content title;
    @Field("sent_at")
    private Instant timestamp;
    @Field("body")
    private Content content;
    @Field("attachment")
    private Midia midia;
    @Field("priority")
    private int priority;
    @Field("is_edited")
    private boolean edited;
    @Field("announcement_status")
    private Status status;
    @Field("target_info")
    private TargetVO targetVO;
}
