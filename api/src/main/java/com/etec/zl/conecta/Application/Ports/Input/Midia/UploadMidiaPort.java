package com.etec.zl.conecta.Application.Ports.Input.Midia;

import com.etec.zl.conecta.Domain.ValueObjects.MediaFile;

public interface UploadMidiaPort {

    String uploadMidia(MediaFile file);
}
