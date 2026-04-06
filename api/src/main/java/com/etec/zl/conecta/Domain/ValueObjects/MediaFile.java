package com.etec.zl.conecta.Domain.ValueObjects;

import java.io.InputStream;

public record MediaFile(
    InputStream inputStream,
    String originalFilename,
    String contentType,
    long size
) {
    public boolean isEmpty() {
        return inputStream == null || size == 0;
    }

    public long getSize() {
        return size;
    }

    public String getContentType() {
        return contentType;
    }
}