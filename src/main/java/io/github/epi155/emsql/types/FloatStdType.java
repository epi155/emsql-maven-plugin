package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;

public final class FloatStdType implements SqlDataType {
    public static final FloatStdType INSTANCE = new FloatStdType();

    @Getter
    private final String primitive = "float";
    @Getter
    private final String wrapper = "Float";

    private FloatStdType() {
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        ipw.printf("ps.setFloat(++ki, %s);%n", source);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        ipw.printf("ps.setFloat(%d, %s);%n", k, source);
    }

    @Override
    public void rsGetValue(PrintModel ipw, int k) {
        ipw.putf("rs.getFloat(%d)", k);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name) {
        ipw.printf("ps.setFloat(++ki, EmSQL.get(%s, \"%s\", Float.class));%n", orig, name);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name, int k) {
        ipw.printf("ps.setFloat(%d, EmSQL.get(%s, \"%s\", Float.class));%n", k, orig, name);
    }

    @Override
    public void registerOut(PrintModel ipw) {
        ipw.printf("ps.registerOutParameter(++ki, Types.REAL);%n");
    }

    @Override
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.REAL);%n", k);
    }

    @Override
    public void csGetValue(PrintModel ipw, int k) {
        ipw.putf("ps.getFloat(%d)", k);
    }
}