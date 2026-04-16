package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;

public final class NumBoolStdType implements SqlDataType {
    public static final NumBoolStdType INSTANCE = new NumBoolStdType();

    @Getter
    private final String primitive = "boolean";
    @Getter
    private final String wrapper = "Boolean";

    private NumBoolStdType() {
    }

    @Override
    public String getterPrefix() {
        return "is";
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        ipw.printf("ps.setByte(++ki, (byte) (%s ? 1 : 0));%n", source);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        ipw.printf("ps.setByte(%d, (byte) (%s ? 1 : 0));%n", k, source);
    }

    @Override
    public void rsGetValue(PrintModel ipw, int k) {
        ipw.putf("rs.getByte(%d)==1", k);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name) {
        ipw.printf("ps.setByte(++ki, (byte) (EmSQL.get(%s, \"%s\", Boolean.class) ? 1 : 0));%n", orig, name);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name, int k) {
        ipw.printf("ps.setByte(%d, (byte) (EmSQL.get(%s, \"%s\", Boolean.class) ? 1 : 0));%n", k, orig, name);
    }

    @Override
    public void registerOut(PrintModel ipw) {
        ipw.printf("ps.registerOutParameter(++ki, Types.TINYINT);%n");
    }

    @Override
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.TINYINT);%n", k);
    }

    @Override
    public void csGetValue(PrintModel ipw, int k) {
        ipw.putf("ps.getByte(%d)==1", k);
    }
}