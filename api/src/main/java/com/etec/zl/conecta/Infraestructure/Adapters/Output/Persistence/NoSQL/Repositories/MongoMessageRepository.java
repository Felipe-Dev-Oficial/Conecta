package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Repositories;

import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Entities.MessageEntity;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Entities.StatementEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface MongoMessageRepository extends MongoRepository<MessageEntity, UUID> {
    @Query("""
            { 
                    '$or': [ 
                    { 'sender_id': ?0, 'receiver_id': ?1 }, 
                    { 'sender_id': ?1, 'receiver_id': ?0 } 
                    ] 
            }
            """)
    Page<MessageEntity> findChatBetween(String id1, String id2, Pageable pageable);

    @Aggregation(pipeline = {
            "{ '$match': { '$or': [ { 'sender_id': ?0 }, { 'receiver_id': ?0 } ] } }",
            "{ '$project': { " +
                    "    'contact': { " +
                    "        '$cond': { " +
                    "            if: { '$eq': ['$sender_id', ?0] }, " +
                    "            then: '$receiver_id', " +
                    "            else: '$sender_id' " +
                    "        } " +
                    "    } " +
                    "} }",
            "{ '$group': { '_id': '$contact' } }"
    })
    Slice<String> findDistinctContactIds(String userId, Pageable pageable);
}
