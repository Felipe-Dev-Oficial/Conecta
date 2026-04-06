package com.etec.zl.conecta.Application.Ports.Output.Storage;

import com.etec.zl.conecta.Domain.ValueObjects.MediaFile;

public interface MidiaStorage {

    String store(MediaFile file);
}
