package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;

import static io.github.epi155.emsql.commons.ClassContextImpl.RUNTIME_EMSQL;
import static io.github.epi155.emsql.commons.ClassContextImpl.RUNTIME_J8TIME;
import static io.github.epi155.emsql.commons.Contexts.cc;

public final class LocalTimeNilType implements SqlDataType {
    public static final LocalTimeNilType INSTANCE = new LocalTimeNilType();

    @Getter
    private final String primitive = "LocalTime";
    @Getter
    private final String wrapper = "LocalTime";

    private LocalTimeNilType() {
    }

    @Override
    public boolean isNullable() {
        return true;
    }

    @Override
    public java.util.Collection<String> requires() {
        return java.util.Set.of("java.time.LocalTime", RUNTIME_J8TIME);
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        ipw.printf("J8Time.setTime(ps, ++ki, %s);%n", source);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        ipw.printf("J8Time.setTime(ps, %d, %s);%n", k, source);
    }

    @Override
    public void rsGetValue(PrintModel ipw, int k) {
        LocalTimeStdType.INSTANCE.rsGetValue(ipw, k);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("J8Time.setTime(ps, ++ki, EmSQL.get(%s, \"%s\", LocalTime.class));%n", orig, name);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name, int k) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("J8Time.setTime(ps, %d, EmSQL.get(%s, \"%s\", LocalTime.class));%n", k, orig, name);
    }

    @Override
    public void registerOut(PrintModel ipw) {
        ipw.printf("ps.registerOutParameter(++ki, Types.TIME);%n");
    }

    @Override
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.TIME);%n", k);
    }

    @Override
    public void csGetValue(PrintModel ipw, int k) {
        LocalTimeStdType.INSTANCE.csGetValue(ipw, k);
    }
}