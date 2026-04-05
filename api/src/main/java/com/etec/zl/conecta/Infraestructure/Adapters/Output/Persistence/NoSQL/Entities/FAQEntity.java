    package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Entities;

    import com.etec.zl.conecta.Domain.ValueObjects.Prioridade;
    import com.etec.zl.conecta.Domain.ValueObjects.StatusFAQ;
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


    @Document(collection = "faqs")
    @CompoundIndex(name = "priority_time_idx", def = "{'priority': -1}")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public class FAQEntity implements Serializable {

        @MongoId
        private UUID id;
        @Field("author_id")
        @Indexed
        private String authorId;
        @Field("faq_question")
        private String question;
        @Field("faq_answer")
        private String answer;
        @Field("status")
        private StatusFAQ statusFAQ;
        @Field("created_at")
        private Instant createdAt;
        @Field("updated_at")
        private Instant updatedAt;
        @Field("relevance")
        private Prioridade relevance;
    }
