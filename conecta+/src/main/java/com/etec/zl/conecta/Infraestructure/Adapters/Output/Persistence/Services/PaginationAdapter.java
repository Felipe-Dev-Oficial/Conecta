package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.Services;

import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import com.etec.zl.conecta.Domain.ValueObjects.SliceResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public class PaginationAdapter {

    public static Pageable toSpring(PageRequest req) {
        return org.springframework.data.domain.PageRequest.of(req.page(), req.size());
    }

    public static <T> PageResult<T> toDomain(Page<T> page) {
        return new PageResult<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    public static <T> SliceResult<T> toDomain(Slice<T> slice) {
        return new SliceResult<>(
                slice.getContent(),
                slice.getNumber(),
                slice.getSize(),
                slice.hasNext()
        );
    }
}