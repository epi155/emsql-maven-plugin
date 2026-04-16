package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;

public final class BooleanStdType implements SqlDataType {
    public static final BooleanStdType INSTANCE = new BooleanStdType();

    @Getter
    private final String primitive = "boolean";
    @Getter
    private final String wrapper = "Boolean";

    private BooleanStdType() {
    }

    @Override
    public String getterPrefix() {
        return "is";
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        ipw.printf("ps.setBoolean(++ki, %s);%n", source);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        ipw.printf("ps.setBoolean(%d, %s);%n", k, source);
    }

    @Override
    public void rsGetValue(PrintModel ipw, int k) {
        ipw.putf("rs.getBoolean(%d)", k);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name) {
        ipw.printf("ps.setBoolean(++ki, EmSQL.get(%s, \"%s\", Boolean.class));%n", orig, name);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name, int k) {
        ipw.printf("ps.setBoolean(%d, EmSQL.get(%s, \"%s\", Boolean.class));%n", k, orig, name);
    }

    @Override
    public void registerOut(PrintModel ipw) {
        ipw.printf("ps.registerOutParameter(++ki, Types.BOOLEAN);%n");
    }

    @Override
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.BOOLEAN);%n", k);
    }

    @Override
    public void csGetValue(PrintModel ipw, int k) {
        ipw.putf("ps.getBoolean(%d)", k);
    }
}