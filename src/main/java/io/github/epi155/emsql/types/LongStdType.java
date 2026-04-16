package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;

public final class LongStdType implements SqlDataType {
    public static final LongStdType INSTANCE = new LongStdType();

    @Getter
    private final String primitive = "long";
    @Getter
    private final String wrapper = "Long";

    private LongStdType() {
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        ipw.printf("ps.setLong(++ki, %s);%n", source);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        ipw.printf("ps.setLong(%d, %s);%n", k, source);
    }

    @Override
    public void rsGetValue(PrintModel ipw, int k) {
        ipw.putf("rs.getLong(%d)", k);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name) {
        ipw.printf("ps.setLong(++ki, EmSQL.get(%s, \"%s\", Long.class));%n", orig, name);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name, int k) {
        ipw.printf("ps.setLong(%d, EmSQL.get(%s, \"%s\", Long.class));%n", k, orig, name);
    }

    @Override
    public void registerOut(PrintModel ipw) {
        ipw.printf("ps.registerOutParameter(++ki, Types.BIGINT);%n");
    }

    @Override
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.BIGINT);%n", k);
    }

    @Override
    public void csGetValue(PrintModel ipw, int k) {
        ipw.putf("ps.getLong(%d)", k);
    }
}