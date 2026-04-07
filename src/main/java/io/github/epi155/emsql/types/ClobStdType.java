package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;

public final class ClobStdType implements SqlDataType {
    public static final ClobStdType INSTANCE = new ClobStdType();

    @Getter
    private final String primitive = "Clob";
    @Getter
    private final String wrapper = "Clob";

    private ClobStdType() {
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        ipw.printf("ps.setClob(++ki, %s);%n", source);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        ipw.printf("ps.setClob(%d, %s);%n", k, source);
    }

    @Override
    public void rsGetValue(PrintModel ipw, int k) {
        ipw.putf("rs.getClob(%d)", k);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name) {
        ipw.printf("ps.setClob(++ki, EmSQL.get(%s, \"%s\", Clob.class));%n", orig, name);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name, int k) {
        ipw.printf("ps.setClob(%d, EmSQL.get(%s, \"%s\", Clob.class));%n", k, orig, name);
    }

    @Override
    public void registerOut(PrintModel ipw) {
        ipw.printf("ps.registerOutParameter(++ki, Types.CLOB);%n");
    }

    @Override
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.CLOB);%n", k);
    }

    @Override
    public void csGetValue(PrintModel ipw, int k) {
        ipw.putf("ps.getClob(%d)", k);
    }
}