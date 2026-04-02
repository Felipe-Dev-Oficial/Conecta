//package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.Cache;
//
//import com.fasterxml.jackson.annotation.JsonTypeInfo;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import com.fasterxml.jackson.databind.json.JsonMapper;
//import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
//import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.cache.RedisCacheConfiguration;
//import org.springframework.data.redis.cache.RedisCacheManager;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.RedisSerializationContext;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
//import java.time.Duration;
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//@EnableCaching
//public class RedisConfig {
//
//    @Bean
//    public ObjectMapper redisObjectMapper() {
//        PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator
//                .builder()
//                .allowIfBaseType(Object.class)
//                .build();
//
//        return JsonMapper.builder()
//                .addModule(new JavaTimeModule())
//                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
//                .activateDefaultTyping(typeValidator, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY)
//                .build();
//    }
//
//    @Bean
//    public Jackson2JsonRedisSerializer<Object> jsonRedisSerializer(ObjectMapper redisObjectMapper) {
//        return new Jackson2JsonRedisSerializer<>(redisObjectMapper, Object.class);
//    }
//
//    @Bean
//    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory, Jackson2JsonRedisSerializer<Object> jsonRedisSerializer) {
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(factory);
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setValueSerializer(jsonRedisSerializer);
//        template.setHashValueSerializer(jsonRedisSerializer);
//        return template;
//    }
//
//    @Bean
//    public RedisCacheManager cacheManager(RedisConnectionFactory factory, Jackson2JsonRedisSerializer<Object> jsonRedisSerializer) {
//        RedisSerializationContext.SerializationPair<Object> serializationPair =
//                RedisSerializationContext.SerializationPair.fromSerializer(jsonRedisSerializer);
//
//        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
//                .serializeValuesWith(serializationPair)
//                .entryTtl(Duration.ofMinutes(30));
//
//        Map<String, RedisCacheConfiguration> specs = new HashMap<>();
//        specs.put("messages", config.entryTtl(Duration.ofHours(2)));
//        specs.put("statements", config.entryTtl(Duration.ofHours(4)));
//
//        return RedisCacheManager.builder(factory)
//                .cacheDefaults(config)
//                .withInitialCacheConfigurations(specs)
//                .build();
//    }
//}