package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;

import static io.github.epi155.emsql.commons.ClassContextImpl.RUNTIME_EMSQL;
import static io.github.epi155.emsql.commons.Contexts.cc;

public final class LocalDateStdType implements SqlDataType {
    public static final LocalDateStdType INSTANCE = new LocalDateStdType();

    @Getter
    private final String primitive = "LocalDate";
    @Getter
    private final String wrapper = "LocalDate";

    private LocalDateStdType() {
    }

    @Override
    public java.util.Collection<String> requires() {
        return java.util.Set.of("java.time.LocalDate");
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        ipw.printf("ps.setObject(++ki, %s);%n", source);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        ipw.printf("ps.setObject(%d, %s);%n", k, source);
    }

    @Override
    public void rsGetValue(PrintModel ipw, int k) {
        ipw.putf("rs.getObject(%d, LocalDate.class)", k);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("ps.setObject(++ki, EmSQL.get(%s, \"%s\", LocalDate.class));%n", orig, name);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name, int k) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("ps.setObject(%d, EmSQL.get(%s, \"%s\", LocalDate.class));%n", k, orig, name);
    }

    @Override
    public void registerOut(PrintModel ipw) {
        ipw.printf("ps.registerOutParameter(++ki, Types.DATE);%n");
    }

    @Override
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.DATE);%n", k);
    }

    @Override
    public void csGetValue(PrintModel ipw, int k) {
        ipw.putf("ps.getObject(%d, LocalDate.class)", k);
    }
}