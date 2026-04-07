package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;

import static io.github.epi155.emsql.commons.ClassContextImpl.RUNTIME_EMSQL;
import static io.github.epi155.emsql.commons.Contexts.cc;

public final class DoubleNilType implements SqlDataType {
    public static final DoubleNilType INSTANCE = new DoubleNilType();

    @Getter
    private final String primitive = "Double";
    @Getter
    private final String wrapper = "Double";

    private DoubleNilType() {
    }

    @Override
    public boolean isNullable() {
        return true;
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("EmSQL.setDouble(ps, ++ki, %s);%n", source);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("EmSQL.setDouble(ps, %d, %s);%n", k, source);
    }

    @Override
    public void rsGetValue(PrintModel ipw, int k) {
        cc.add(RUNTIME_EMSQL);
        ipw.putf("EmSQL.getDouble(rs,%d)", k);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name) {
        ipw.printf("EmSQL.setDouble(ps, ++ki, EmSQL.get(%s, \"%s\", Double.class));%n", orig, name);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name, int k) {
        ipw.printf("EmSQL.setDouble(ps, %d, EmSQL.get(%s, \"%s\", Double.class));%n", k, orig, name);
    }

    @Override
    public void registerOut(PrintModel ipw) {
        ipw.printf("ps.registerOutParameter(++ki, Types.DOUBLE);%n");
    }

    @Override
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.DOUBLE);%n", k);
    }

    @Override
    public void csGetValue(PrintModel ipw, int k) {
        cc.add(RUNTIME_EMSQL);
        ipw.putf("EmSQL.getDouble(ps,%d)", k);
    }
}