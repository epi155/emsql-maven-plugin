package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;

public final class IntegerStdType implements SqlDataType {
    public static final IntegerStdType INSTANCE = new IntegerStdType();

    @Getter
    private final String primitive = "int";
    @Getter
    private final String wrapper = "Integer";

    private IntegerStdType() {
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        ipw.printf("ps.setInt(++ki, %s);%n", source);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        ipw.printf("ps.setInt(%d, %s);%n", k, source);
    }

    @Override
    public void rsGetValue(PrintModel ipw, int k) {
        ipw.putf("rs.getInt(%d)", k);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name) {
        ipw.printf("ps.setInt(++ki, EmSQL.get(%s, \"%s\", Integer.class));%n", orig, name);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name, int k) {
        ipw.printf("ps.setInt(%d, EmSQL.get(%s, \"%s\", Integer.class));%n", k, orig, name);
    }

    @Override
    public void registerOut(PrintModel ipw) {
        ipw.printf("ps.registerOutParameter(++ki, Types.INTEGER);%n");
    }

    @Override
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.INTEGER);%n", k);
    }

    @Override
    public void csGetValue(PrintModel ipw, int k) {
        ipw.putf("ps.getInt(%d)", k);
    }
}