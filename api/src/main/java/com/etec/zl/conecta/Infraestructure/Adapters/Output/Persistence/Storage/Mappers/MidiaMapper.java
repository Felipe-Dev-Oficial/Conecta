package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.Storage.Mappers;

import com.etec.zl.conecta.Domain.Exceptions.ProcessingErrorException;
import com.etec.zl.conecta.Domain.ValueObjects.MediaFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class MidiaMapper {

    public MediaFile toMediaFile(MultipartFile file) {
        try {
            return new MediaFile(
                file.getInputStream(),
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize()
            );
        } catch (IOException e) {
            throw new ProcessingErrorException("Erro ao processar arquivo.");
        }
    }
}