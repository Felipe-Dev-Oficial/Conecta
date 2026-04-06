package com.etec.zl.conecta.Infraestructure.Adapters.Input.Controllers;

import com.etec.zl.conecta.Application.Ports.Input.Midia.UploadMidiaPort;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.Storage.Mappers.MidiaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping("/conecta/arquivos")
@RequiredArgsConstructor
public class MidiaController {

    private final UploadMidiaPort uploadMidiaPort;
    private final MidiaMapper  mapper;

    @PostMapping(value = "/upload", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        String link = uploadMidiaPort.uploadMidia(mapper.toMediaFile(file));
        return ResponseEntity.ok(link);
    }
}
