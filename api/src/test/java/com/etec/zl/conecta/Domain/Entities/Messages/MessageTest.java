package com.etec.zl.conecta.Domain.Entities.Messages;

import com.etec.zl.conecta.Domain.Entities.Midia.Midia;
import com.etec.zl.conecta.Domain.ValueObjects.Content;
import com.etec.zl.conecta.Domain.ValueObjects.TipoMidia;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Message")
class MessageTest {

    private static final String SENDER_ID = "user-sender-1";
    private static final String RECEIVER_ID = "user-receiver-2";

    private Message messageSemMidia() {
        return new Message(SENDER_ID, RECEIVER_ID, new Content("Olá!"), null);
    }

    private Message messageComMidia() {
        return new Message(
                SENDER_ID, RECEIVER_ID,
                new Content("Veja essa foto"),
                new Midia(TipoMidia.FOTO, "https://img.com/foto.jpg")
        );
    }

    // ─── Criação via construtor simples ───────────────────────────────────────

    @Nested
    @DisplayName("Criação via construtor simples")
    class Criacao {

        @Test
        @DisplayName("deve gerar id UUID não nulo automaticamente")
        void idGerado() {
            assertNotNull(messageSemMidia().getId());
        }

        @Test
        @DisplayName("dois ids gerados por construtores diferentes devem ser únicos")
        void idsUnicos() {
            assertNotEquals(messageSemMidia().getId(), messageSemMidia().getId());
        }

        @Test
        @DisplayName("deve preencher timestamp automaticamente")
        void timestampPreenchido() {
            var antes = Instant.now();
            var msg = messageSemMidia();
            assertFalse(msg.getTimestamp().isBefore(antes));
        }

        @Test
        @DisplayName("deve preservar idSender corretamente")
        void idSenderPreservado() {
            assertEquals(SENDER_ID, messageSemMidia().getIdSender());
        }

        @Test
        @DisplayName("deve preservar idReceiver corretamente")
        void idReceiverPreservado() {
            assertEquals(RECEIVER_ID, messageSemMidia().getIdReceiver());
        }

        @Test
        @DisplayName("deve preservar conteúdo corretamente")
        void conteudoPreservado() {
            assertEquals("Olá!", messageSemMidia().getContent().content());
        }

        @Test
        @DisplayName("deve aceitar mídia nula")
        void midiaNula_aceita() {
            assertNull(messageSemMidia().getMidia());
        }

        @Test
        @DisplayName("deve preservar mídia quando fornecida")
        void midiaPreservada() {
            var msg = messageComMidia();
            assertNotNull(msg.getMidia());
            assertEquals(TipoMidia.FOTO, msg.getMidia().tipoMidia());
            assertEquals("https://img.com/foto.jpg", msg.getMidia().link());
        }
    }

    // ─── Construtor completo ──────────────────────────────────────────────────

    @Nested
    @DisplayName("Criação via construtor completo")
    class CriacaoCompleta {

        @Test
        @DisplayName("deve preservar o id fornecido")
        void idPreservado() {
            var id = UUID.randomUUID();
            var ts = Instant.now();
            var msg = new Message(id, SENDER_ID, RECEIVER_ID, ts, new Content("Texto"), null);
            assertEquals(id, msg.getId());
        }

        @Test
        @DisplayName("deve preservar o timestamp fornecido")
        void timestampPreservado() {
            var id = UUID.randomUUID();
            var ts = Instant.parse("2024-01-15T10:00:00Z");
            var msg = new Message(id, SENDER_ID, RECEIVER_ID, ts, new Content("Texto"), null);
            assertEquals(ts, msg.getTimestamp());
        }
    }
}