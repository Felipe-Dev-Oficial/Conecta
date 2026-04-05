package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.Cache;

import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import com.etec.zl.conecta.Domain.ValueObjects.SliceResult;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig {

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
    abstract static class PageResultMixin {}

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
    abstract static class SliceResultMixin {}

    private ObjectMapper buildRedisObjectMapper() {
        PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator
                .builder()
                .allowIfBaseType(Object.class)
                .build();

        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .activateDefaultTyping(typeValidator, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY)
                .build();

        // Registra os mixins para forçar @class nos records
        mapper.addMixIn(PageResult.class, PageResultMixin.class);
        mapper.addMixIn(SliceResult.class, SliceResultMixin.class);

        return mapper;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        var serializer = new GenericJackson2JsonRedisSerializer(buildRedisObjectMapper());
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);
        return template;
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        var serializer = new GenericJackson2JsonRedisSerializer(buildRedisObjectMapper());

        RedisSerializationContext.SerializationPair<Object> pair =
                RedisSerializationContext.SerializationPair.fromSerializer(serializer);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(pair)
                .disableCachingNullValues()
                .entryTtl(Duration.ofMinutes(30));

        Map<String, RedisCacheConfiguration> specs = new HashMap<>();
        specs.put("users",         defaultConfig.entryTtl(Duration.ofMinutes(30)));
        specs.put("users-lists",   defaultConfig.entryTtl(Duration.ofMinutes(15)));
        specs.put("users-context", defaultConfig.entryTtl(Duration.ofMinutes(20)));
        specs.put("turmas",        defaultConfig.entryTtl(Duration.ofHours(2)));
        specs.put("faqs",          defaultConfig.entryTtl(Duration.ofHours(4)));
        specs.put("faq-single",    defaultConfig.entryTtl(Duration.ofHours(4)));
        specs.put("statements",    defaultConfig.entryTtl(Duration.ofHours(4)));
        specs.put("messages",      defaultConfig.entryTtl(Duration.ofHours(2)));

        return RedisCacheManager.builder(factory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(specs)
                .build();
    }
}