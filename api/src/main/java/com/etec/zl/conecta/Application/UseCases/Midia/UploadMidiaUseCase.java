package com.etec.zl.conecta.Application.UseCases.Midia;


import com.etec.zl.conecta.Application.Ports.Input.Midia.UploadMidiaPort;
import com.etec.zl.conecta.Application.Ports.Output.Storage.MidiaStorage;
import com.etec.zl.conecta.Domain.Exceptions.InvalidDataException;
import com.etec.zl.conecta.Domain.ValueObjects.MediaFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class UploadMidiaUseCase implements UploadMidiaPort {

    private static final Logger log = LoggerFactory.getLogger(UploadMidiaUseCase.class);

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp",
            "video/mp4", "video/webm", "video/ogg",
            "audio/mpeg", "audio/ogg", "audio/wav", "audio/webm",
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    private static final long MAX_SIZE_BYTES = 50L * 1024 * 1024;

    private final MidiaStorage storagePort;

    public UploadMidiaUseCase(MidiaStorage storagePort) {
        this.storagePort = storagePort;
    }

    @Override
    public String uploadMidia(MediaFile file) {
        validar(file);
        String url = storagePort.store(file);
        log.info("Mídia armazenada: {}", url);
        return url;
    }

    private void validar(MediaFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidDataException("Arquivo não pode ser vazio.");
        }

        if (file.size() > MAX_SIZE_BYTES) {
            throw new InvalidDataException(
                    "Arquivo excede o tamanho máximo de 50 MB (recebido: %d bytes).".formatted(file.size()));
        }

        String contentType = file.contentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new InvalidDataException(
                    "Tipo de mídia não suportado: %s. Use imagem, vídeo ou áudio.".formatted(contentType));
        }
    }
}