package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;

import static io.github.epi155.emsql.commons.ClassContextImpl.RUNTIME_EMSQL;
import static io.github.epi155.emsql.commons.Contexts.cc;

public final class LongNVarCharNilType implements SqlDataType {
    public static final LongNVarCharNilType INSTANCE = new LongNVarCharNilType();

    @Getter
    private final String primitive = "String";
    @Getter
    private final String wrapper = "String";

    private LongNVarCharNilType() {
    }

    @Override
    public boolean isNullable() {
        return true;
    }

    @Override
    public void rsGetValue(PrintModel ipw, int k) {
        ipw.putf("rs.getNString(%d)", k);
    }

    @Override
    public void csGetValue(PrintModel ipw, int k) {
        ipw.putf("ps.getNString(%d)", k);
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("EmSQL.setLongNVarChar(ps, ++ki, %s);%n", source);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("EmSQL.setLongNVarChar(ps, %d, %s);%n", k, source);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("EmSQL.setLongNVarChar(ps, ++ki, EmSQL.get(%s, \"%s\", String.class));%n", orig, name);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name, int k) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("EmSQL.setLongNVarChar(ps, %d, EmSQL.get(%s, \"%s\", String.class));%n", k, orig, name);
    }

    @Override
    public void registerOut(PrintModel ipw) {
        ipw.printf("ps.registerOutParameter(++ki, Types.LONGNVARCHAR);%n");
    }

    @Override
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.LONGNVARCHAR);%n", k);
    }
}