package com.etec.zl.conecta.Application.UseCases.Midia;

import com.etec.zl.conecta.Application.Ports.Output.Storage.MidiaStorage;
import com.etec.zl.conecta.Domain.Exceptions.InvalidDataException;
import com.etec.zl.conecta.Domain.ValueObjects.MediaFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UploadMidiaUseCaseTest {

    @Mock
    private MidiaStorage midiaStorage;

    @InjectMocks
    private UploadMidiaUseCase uploadMidiaUseCase;

    private MediaFile mediaFileValido;

    @BeforeEach
    void setUp() {
        mediaFileValido = new MediaFile(
                new ByteArrayInputStream("conteudo".getBytes()),
                "arquivo.jpg",
                "image/jpeg",
                100L
        );
    }

    @Nested
    @DisplayName("uploadMidia - casos de sucesso")
    class Sucesso {

        @Test
        @DisplayName("deve retornar URL ao fazer upload de arquivo válido")
        void deveRetornarUrlQuandoArquivoValido() {
            String urlEsperada = "http://localhost/media/arquivo.jpg";
            when(midiaStorage.store(any(MediaFile.class))).thenReturn(urlEsperada);

            String resultado = uploadMidiaUseCase.uploadMidia(mediaFileValido);

            assertThat(resultado).isEqualTo(urlEsperada);
            verify(midiaStorage, times(1)).store(mediaFileValido);
        }

        @ParameterizedTest
        @DisplayName("deve aceitar todos os tipos de mídia permitidos")
        @ValueSource(strings = {
                "image/jpeg", "image/png", "image/gif", "image/webp",
                "video/mp4", "video/webm", "video/ogg",
                "audio/mpeg", "audio/ogg", "audio/wav", "audio/webm",
                "application/pdf",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        })
        void deveAceitarTiposPermitidos(String contentType) {
            MediaFile arquivo = new MediaFile(
                    new ByteArrayInputStream("conteudo".getBytes()),
                    "arquivo",
                    contentType,
                    100L
            );
            when(midiaStorage.store(any())).thenReturn("http://localhost/media/arquivo");

            String resultado = uploadMidiaUseCase.uploadMidia(arquivo);

            assertThat(resultado).isNotNull();
        }

        @Test
        @DisplayName("deve aceitar arquivo com exatamente 50 MB")
        void deveAceitarArquivoComTamanhoMaximo() {
            long tamanhoMaximo = 50L * 1024 * 1024;
            MediaFile arquivo = new MediaFile(
                    new ByteArrayInputStream(new byte[0]),
                    "arquivo.mp4",
                    "video/mp4",
                    tamanhoMaximo
            );
            when(midiaStorage.store(any())).thenReturn("http://localhost/media/arquivo.mp4");

            String resultado = uploadMidiaUseCase.uploadMidia(arquivo);

            assertThat(resultado).isNotNull();
        }
    }

    @Nested
    @DisplayName("uploadMidia - validações de arquivo nulo/vazio")
    class ValidacaoNuloVazio {

        @Test
        @DisplayName("deve lançar InvalidDataException quando arquivo for null")
        void deveLancarExcecaoQuandoArquivoNull() {
            assertThatThrownBy(() -> uploadMidiaUseCase.uploadMidia(null))
                    .isInstanceOf(InvalidDataException.class)
                    .hasMessageContaining("Arquivo não pode ser vazio");

            verify(midiaStorage, never()).store(any());
        }

        @Test
        @DisplayName("deve lançar InvalidDataException quando inputStream for null")
        void deveLancarExcecaoQuandoInputStreamNull() {
            MediaFile arquivoSemStream = new MediaFile(null, "arquivo.jpg", "image/jpeg", 100L);

            assertThatThrownBy(() -> uploadMidiaUseCase.uploadMidia(arquivoSemStream))
                    .isInstanceOf(InvalidDataException.class)
                    .hasMessageContaining("Arquivo não pode ser vazio");

            verify(midiaStorage, never()).store(any());
        }

        @Test
        @DisplayName("deve lançar InvalidDataException quando tamanho for zero")
        void deveLancarExcecaoQuandoTamanhoZero() {
            MediaFile arquivoVazio = new MediaFile(
                    new ByteArrayInputStream(new byte[0]),
                    "arquivo.jpg",
                    "image/jpeg",
                    0L
            );

            assertThatThrownBy(() -> uploadMidiaUseCase.uploadMidia(arquivoVazio))
                    .isInstanceOf(InvalidDataException.class)
                    .hasMessageContaining("Arquivo não pode ser vazio");

            verify(midiaStorage, never()).store(any());
        }
    }

    @Nested
    @DisplayName("uploadMidia - validações de tamanho")
    class ValidacaoTamanho {

        @Test
        @DisplayName("deve lançar InvalidDataException quando arquivo exceder 50 MB")
        void deveLancarExcecaoQuandoArquivoMaiorQueMaximo() {
            long tamanhoExcedido = 50L * 1024 * 1024 + 1;
            MediaFile arquivoGrande = new MediaFile(
                    new ByteArrayInputStream(new byte[0]),
                    "arquivo.mp4",
                    "video/mp4",
                    tamanhoExcedido
            );

            assertThatThrownBy(() -> uploadMidiaUseCase.uploadMidia(arquivoGrande))
                    .isInstanceOf(InvalidDataException.class)
                    .hasMessageContaining("Arquivo excede o tamanho máximo de 50 MB")
                    .hasMessageContaining(String.valueOf(tamanhoExcedido));

            verify(midiaStorage, never()).store(any());
        }
    }

    @Nested
    @DisplayName("uploadMidia - validações de content type")
    class ValidacaoContentType {

        @Test
        @DisplayName("deve lançar InvalidDataException quando content type for null")
        void deveLancarExcecaoQuandoContentTypeNull() {
            MediaFile arquivo = new MediaFile(
                    new ByteArrayInputStream("conteudo".getBytes()),
                    "arquivo",
                    null,
                    100L
            );

            assertThatThrownBy(() -> uploadMidiaUseCase.uploadMidia(arquivo))
                    .isInstanceOf(InvalidDataException.class)
                    .hasMessageContaining("Tipo de mídia não suportado");

            verify(midiaStorage, never()).store(any());
        }

        @ParameterizedTest
        @DisplayName("deve lançar InvalidDataException para tipos não suportados")
        @ValueSource(strings = {
                "text/plain", "application/json", "application/zip",
                "image/bmp", "image/tiff", "video/avi", "audio/flac"
        })
        void deveLancarExcecaoParaTiposNaoSuportados(String contentType) {
            MediaFile arquivo = new MediaFile(
                    new ByteArrayInputStream("conteudo".getBytes()),
                    "arquivo",
                    contentType,
                    100L
            );

            assertThatThrownBy(() -> uploadMidiaUseCase.uploadMidia(arquivo))
                    .isInstanceOf(InvalidDataException.class)
                    .hasMessageContaining("Tipo de mídia não suportado")
                    .hasMessageContaining(contentType);

            verify(midiaStorage, never()).store(any());
        }
    }
}