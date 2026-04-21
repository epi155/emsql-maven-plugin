package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

import static io.github.epi155.emsql.commons.ClassContextImpl.RUNTIME_EMSQL;
import static io.github.epi155.emsql.commons.Contexts.cc;

public class FixedCharNilType extends CharNilType {
    private static final Map<Integer, FixedCharNilType> CACHE = new HashMap<>();

    @Getter
    private final int length;

    private FixedCharNilType(int length) {
        this.length = length;
    }

    public static FixedCharNilType getInstance(int length) {
        return CACHE.computeIfAbsent(length, FixedCharNilType::new);
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("EmSQL.setChar(ps, ++ki, EmSQL.rpads(%s, %d));%n", source, length);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("EmSQL.setChar(ps, %d, EmSQL.rpads(%s, %d));%n", k, source, length);
    }


}