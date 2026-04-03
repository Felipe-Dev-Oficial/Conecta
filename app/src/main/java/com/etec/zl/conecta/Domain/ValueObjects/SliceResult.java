package com.etec.zl.conecta.Domain.ValueObjects;

import java.util.List;
import java.util.function.Function;

public record SliceResult<T>(
        List<T> content,
        int page,
        int size,
        boolean hasNext
) {
    public <R> SliceResult<R> map(Function<T, R> mapper) {
        return new SliceResult<>(
                content.stream().map(mapper).toList(),
                page, size, hasNext
        );
    }
}