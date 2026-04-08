package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.Storage.Adapters;

import com.etec.zl.conecta.Domain.Exceptions.ProcessingErrorException;
import com.etec.zl.conecta.Domain.ValueObjects.MediaFile;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocalDiskMediaStorageAdapterTest {

    @TempDir
    Path tempDir;

    private LocalDiskMediaStorageAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new LocalDiskMediaStorageAdapter();
        ReflectionTestUtils.setField(adapter, "storagePath", tempDir.toString());
        ReflectionTestUtils.setField(adapter, "publicBaseUrl", "http://localhost");
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Nested
    @DisplayName("store - casos de sucesso")
    class Sucesso {

        @Test
        @DisplayName("deve salvar arquivo e retornar URL com base no request HTTP")
        void deveSalvarArquivoERetornarUrlDoRequest() {
            MediaFile file = new MediaFile(
                    new ByteArrayInputStream("conteudo".getBytes()),
                    "foto.jpg",
                    "image/jpeg",
                    8L
            );

            HttpServletRequest mockRequest = mock(HttpServletRequest.class);
            when(mockRequest.getHeader("X-Forwarded-Proto")).thenReturn(null);
            when(mockRequest.getScheme()).thenReturn("http");
            when(mockRequest.getHeader("X-Forwarded-Host")).thenReturn(null);
            when(mockRequest.getHeader("Host")).thenReturn("meuservidor.com");

            ServletRequestAttributes attrs = new ServletRequestAttributes(mockRequest);
            RequestContextHolder.setRequestAttributes(attrs);

            String url = adapter.store(file);

            assertThat(url).startsWith("http://meuservidor.com/media/");
            assertThat(url).endsWith(".jpg");
        }

        @Test
        @DisplayName("deve usar X-Forwarded-Proto e X-Forwarded-Host quando presentes")
        void deveUsarHeadersForwardados() {
            MediaFile file = new MediaFile(
                    new ByteArrayInputStream("conteudo".getBytes()),
                    "video.mp4",
                    "video/mp4",
                    8L
            );

            HttpServletRequest mockRequest = mock(HttpServletRequest.class);
            when(mockRequest.getHeader("X-Forwarded-Proto")).thenReturn("https");
            when(mockRequest.getHeader("X-Forwarded-Host")).thenReturn("app.producao.com");

            ServletRequestAttributes attrs = new ServletRequestAttributes(mockRequest);
            RequestContextHolder.setRequestAttributes(attrs);

            String url = adapter.store(file);

            assertThat(url).startsWith("https://app.producao.com/media/");
            assertThat(url).endsWith(".mp4");
        }

        @Test
        @DisplayName("deve usar publicBaseUrl quando não há contexto de request")
        void deveUsarPublicBaseUrlSemContextoHttp() {
            MediaFile file = new MediaFile(
                    new ByteArrayInputStream("conteudo".getBytes()),
                    "doc.pdf",
                    "application/pdf",
                    8L
            );

            RequestContextHolder.resetRequestAttributes();

            String url = adapter.store(file);

            assertThat(url).startsWith("http://localhost/media/");
            assertThat(url).endsWith(".pdf");
        }

        @Test
        @DisplayName("deve gerar nome de arquivo único (UUID) para cada upload")
        void deveGerarNomeUnicoParaCadaUpload() {
            MediaFile file1 = new MediaFile(new ByteArrayInputStream("a".getBytes()), "img.png", "image/png", 1L);
            MediaFile file2 = new MediaFile(new ByteArrayInputStream("b".getBytes()), "img.png", "image/png", 1L);

            RequestContextHolder.resetRequestAttributes();

            String url1 = adapter.store(file1);
            String url2 = adapter.store(file2);

            assertThat(url1).isNotEqualTo(url2);
        }

        @Test
        @DisplayName("deve salvar arquivo sem extensão quando originalFilename não tiver ponto")
        void deveSalvarSemExtensaoQuandoSemPonto() {
            MediaFile file = new MediaFile(
                    new ByteArrayInputStream("conteudo".getBytes()),
                    "arquivo-sem-extensao",
                    "image/jpeg",
                    8L
            );
            RequestContextHolder.resetRequestAttributes();

            String url = adapter.store(file);

            // UUID puro sem extensão — o path termina após o UUID
            String filename = url.substring(url.lastIndexOf('/') + 1);
            assertThat(filename).doesNotContain(".");
        }

        @Test
        @DisplayName("deve sanitizar extensão removendo caracteres especiais")
        void deveSanitizarExtensao() {
            MediaFile file = new MediaFile(
                    new ByteArrayInputStream("conteudo".getBytes()),
                    "arquivo.jp<>g",
                    "image/jpeg",
                    8L
            );
            RequestContextHolder.resetRequestAttributes();

            String url = adapter.store(file);

            assertThat(url).doesNotContain("<").doesNotContain(">");
        }

        @Test
        @DisplayName("deve usar serverName quando header Host for null")
        void deveUsarServerNameQuandoHostNull() {
            MediaFile file = new MediaFile(
                    new ByteArrayInputStream("conteudo".getBytes()),
                    "img.png",
                    "image/png",
                    8L
            );

            HttpServletRequest mockRequest = mock(HttpServletRequest.class);
            when(mockRequest.getHeader("X-Forwarded-Proto")).thenReturn(null);
            when(mockRequest.getScheme()).thenReturn("http");
            when(mockRequest.getHeader("X-Forwarded-Host")).thenReturn(null);
            when(mockRequest.getHeader("Host")).thenReturn(null);
            when(mockRequest.getServerName()).thenReturn("192.168.0.1");

            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));

            String url = adapter.store(file);

            assertThat(url).startsWith("http://192.168.0.1/media/");
        }
    }

    @Nested
    @DisplayName("store - casos de erro")
    class Erros {

        @Test
        @DisplayName("deve lançar ProcessingErrorException quando ocorrer IOException ao salvar")
        void deveLancarExcecaoQuandoIOException() {
            InputStream streamQueFalha = new InputStream() {
                @Override
                public int read() throws IOException {
                    throw new IOException("Disco cheio");
                }

                @Override
                public int read(byte[] b, int off, int len) throws IOException {
                    throw new IOException("Disco cheio");
                }

                @Override
                public long transferTo(java.io.OutputStream out) throws IOException {
                    throw new IOException("Disco cheio");
                }
            };

            MediaFile file = new MediaFile(streamQueFalha, "arquivo.jpg", "image/jpeg", 100L);

            RequestContextHolder.resetRequestAttributes();

            assertThatThrownBy(() -> adapter.store(file))
                    .isInstanceOf(ProcessingErrorException.class)
                    .hasMessageContaining("Não foi possível armazenar o arquivo");
        }
    }
}