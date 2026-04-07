package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

import static io.github.epi155.emsql.commons.ClassContextImpl.RUNTIME_EMSQL;
import static io.github.epi155.emsql.commons.Contexts.cc;

public class FixedCharStdType extends CharStdType {
    private static final Map<Integer, FixedCharStdType> CACHE = new HashMap<>();

    @Getter
    private final int length;

    private FixedCharStdType(int length) {
        this.length = length;
    }

    public static FixedCharStdType getInstance(int length) {
        return CACHE.computeIfAbsent(length, FixedCharStdType::new);
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("ps.setString(++ki, EmSQL.rpads(%s, %d));%n", source, length);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("ps.setString(%d, EmSQL.rpads(%s, %d));%n", k, source, length);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("ps.setString(++ki, EmSQL.rpads(EmSQL.get(%s, \"%s\", String.class), %d));%n", orig, name, length);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name, int k) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("ps.setString(%d, EmSQL.rpads(EmSQL.get(%s, \"%s\", String.class), %d));%n", k, orig, name, length);
    }
}