package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;

import static io.github.epi155.emsql.commons.ClassContextImpl.RUNTIME_EMSQL;
import static io.github.epi155.emsql.commons.ClassContextImpl.RUNTIME_J8TIME;
import static io.github.epi155.emsql.commons.Contexts.cc;

public final class LocalDateNilType implements SqlDataType {
    public static final LocalDateNilType INSTANCE = new LocalDateNilType();

    @Getter
    private final String primitive = "LocalDate";
    @Getter
    private final String wrapper = "LocalDate";

    private LocalDateNilType() {
    }

    @Override
    public boolean isNullable() {
        return true;
    }

    @Override
    public java.util.Collection<String> requires() {
        return java.util.Set.of("java.time.LocalDate", RUNTIME_J8TIME);
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        ipw.printf("J8Time.setDate(ps, ++ki, %s);%n", source);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        ipw.printf("J8Time.setDate(ps, %d, %s);%n", k, source);
    }

    @Override
    public void rsGetValue(PrintModel ipw, int k) {
        LocalDateStdType.INSTANCE.rsGetValue(ipw, k);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("J8Time.setDate(ps, ++ki, EmSQL.get(%s, \"%s\", LocalDate.class));%n", orig, name);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name, int k) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("J8Time.setDate(ps, %d, EmSQL.get(%s, \"%s\", LocalDate.class));%n", k, orig, name);
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
        LocalDateStdType.INSTANCE.csGetValue(ipw, k);
    }
}