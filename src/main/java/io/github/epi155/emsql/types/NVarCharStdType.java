package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;

public final class NVarCharStdType implements SqlDataType {
    public static final NVarCharStdType INSTANCE = new NVarCharStdType();

    @Getter
    private final String primitive = "String";
    @Getter
    private final String wrapper = "String";

    private NVarCharStdType() {
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        ipw.printf("ps.setNString(++ki, %s);%n", source);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        ipw.printf("ps.setNString(%d, %s);%n", k, source);
    }

    @Override
    public void rsGetValue(PrintModel ipw, int k) {
        ipw.putf("rs.getNString(%d)", k);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name) {
        ipw.printf("ps.setNString(++ki, EmSQL.get(%s, \"%s\", String.class));%n", orig, name);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name, int k) {
        ipw.printf("ps.setNString(%d, EmSQL.get(%s, \"%s\", String.class));%n", k, orig, name);
    }

    @Override
    public void registerOut(PrintModel ipw) {
        ipw.printf("ps.registerOutParameter(++ki, Types.NVARCHAR);%n");
    }

    @Override
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.NVARCHAR);%n", k);
    }

    @Override
    public void csGetValue(PrintModel ipw, int k) {
        ipw.putf("ps.getNString(%d)", k);
    }
}