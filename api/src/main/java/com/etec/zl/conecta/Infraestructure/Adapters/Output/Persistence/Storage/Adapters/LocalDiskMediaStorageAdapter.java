package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.Storage.Adapters;

import com.etec.zl.conecta.Application.Ports.Output.Storage.MidiaStorage;
import com.etec.zl.conecta.Domain.Exceptions.ProcessingErrorException;
import com.etec.zl.conecta.Domain.ValueObjects.MediaFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
public class LocalDiskMediaStorageAdapter implements MidiaStorage {

    private static final Logger log = LoggerFactory.getLogger(LocalDiskMediaStorageAdapter.class);

    @Value("${midia.storage.path:/var/www/media/}")
    private String storagePath;

    @Value("${midia.public.base-url:http://localhost}")
    private String publicBaseUrl;

    @Override
    public String store(MediaFile file) {
        try {
            Path directory = Paths.get(storagePath);
            Files.createDirectories(directory);

            String filename = buildFilename(file);
            Path destination = directory.resolve(filename);

            Files.copy(file.inputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            log.debug("Arquivo salvo em: {}", destination);

            return buildPublicUrl(filename);

        } catch (IOException e) {
            log.error("Falha ao salvar arquivo de mídia: {}", e.getMessage(), e);
            throw new ProcessingErrorException("Não foi possível armazenar o arquivo. Tente novamente.");
        }
    }

    private String buildFilename(MediaFile file) {
        String original = file.originalFilename();
        String extension = "";

        if (original != null && original.contains(".")) {
            extension = original.substring(original.lastIndexOf('.'));
            extension = extension.replaceAll("[^a-zA-Z0-9.]", "");
        }

        return UUID.randomUUID() + extension;
    }

    private String buildPublicUrl(String filename) {
        return publicBaseUrl.stripTrailing() + "/media/" + filename;
    }
}