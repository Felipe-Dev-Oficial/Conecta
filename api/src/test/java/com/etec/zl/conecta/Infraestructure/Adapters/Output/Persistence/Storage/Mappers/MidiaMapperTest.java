package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.Storage.Mappers;

import com.etec.zl.conecta.Domain.Exceptions.ProcessingErrorException;
import com.etec.zl.conecta.Domain.ValueObjects.MediaFile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MidiaMapperTest {

    @InjectMocks
    private MidiaMapper midiaMapper;

    @Nested
    @DisplayName("toMediaFile - casos de sucesso")
    class Sucesso {

        @Test
        @DisplayName("deve mapear MultipartFile para MediaFile corretamente")
        void deveMapearMultipartFileParaMediaFile() throws IOException {
            InputStream stream = new ByteArrayInputStream("conteudo".getBytes());
            MultipartFile multipartFile = mock(MultipartFile.class);

            when(multipartFile.getInputStream()).thenReturn(stream);
            when(multipartFile.getOriginalFilename()).thenReturn("foto.jpg");
            when(multipartFile.getContentType()).thenReturn("image/jpeg");
            when(multipartFile.getSize()).thenReturn(100L);

            MediaFile resultado = midiaMapper.toMediaFile(multipartFile);

            assertThat(resultado).isNotNull();
            assertThat(resultado.inputStream()).isEqualTo(stream);
            assertThat(resultado.originalFilename()).isEqualTo("foto.jpg");
            assertThat(resultado.contentType()).isEqualTo("image/jpeg");
            assertThat(resultado.size()).isEqualTo(100L);
        }

        @Test
        @DisplayName("deve mapear arquivo com originalFilename null")
        void deveMapearComOriginalFilenameNull() throws IOException {
            MultipartFile multipartFile = mock(MultipartFile.class);
            when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
            when(multipartFile.getOriginalFilename()).thenReturn(null);
            when(multipartFile.getContentType()).thenReturn("application/pdf");
            when(multipartFile.getSize()).thenReturn(50L);

            MediaFile resultado = midiaMapper.toMediaFile(multipartFile);

            assertThat(resultado.originalFilename()).isNull();
            assertThat(resultado.contentType()).isEqualTo("application/pdf");
        }

        @Test
        @DisplayName("deve mapear arquivo com contentType null")
        void deveMapearComContentTypeNull() throws IOException {
            MultipartFile multipartFile = mock(MultipartFile.class);
            when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
            when(multipartFile.getOriginalFilename()).thenReturn("arquivo");
            when(multipartFile.getContentType()).thenReturn(null);
            when(multipartFile.getSize()).thenReturn(10L);

            MediaFile resultado = midiaMapper.toMediaFile(multipartFile);

            assertThat(resultado.contentType()).isNull();
        }

        @Test
        @DisplayName("deve mapear arquivo com tamanho zero")
        void deveMapearComTamanhoZero() throws IOException {
            MultipartFile multipartFile = mock(MultipartFile.class);
            when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
            when(multipartFile.getOriginalFilename()).thenReturn("vazio.txt");
            when(multipartFile.getContentType()).thenReturn("text/plain");
            when(multipartFile.getSize()).thenReturn(0L);

            MediaFile resultado = midiaMapper.toMediaFile(multipartFile);

            assertThat(resultado.size()).isZero();
            assertThat(resultado.isEmpty()).isTrue();
        }
    }

    @Nested
    @DisplayName("toMediaFile - casos de erro")
    class Erros {

        @Test
        @DisplayName("deve lançar ProcessingErrorException quando getInputStream lançar IOException")
        void deveLancarExcecaoQuandoIOException() throws IOException {
            MultipartFile multipartFile = mock(MultipartFile.class);
            when(multipartFile.getInputStream()).thenThrow(new IOException("Erro de leitura"));

            assertThatThrownBy(() -> midiaMapper.toMediaFile(multipartFile))
                    .isInstanceOf(ProcessingErrorException.class)
                    .hasMessageContaining("Erro ao processar arquivo");
        }
    }
}