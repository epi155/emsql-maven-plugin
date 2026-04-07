package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;

public final class BinaryNilType implements SqlDataType {
    public static final BinaryNilType INSTANCE = new BinaryNilType();

    @Getter
    private final String primitive = "byte[]";
    @Getter
    private final String wrapper = "byte[]";

    private BinaryNilType() {
    }

    @Override
    public boolean isNullable() {
        return true;
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        ipw.printf("ps.setBytes(++ki, %s);%n", source);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        ipw.printf("ps.setBytes(%d, %s);%n", k, source);
    }

    @Override
    public void rsGetValue(PrintModel ipw, int k) {
        BinaryStdType.INSTANCE.rsGetValue(ipw, k);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name) {
        ipw.printf("ps.setBytes(++ki, EmSQL.get(%s, \"%s\", byte[].class));%n", orig, name);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name, int k) {
        ipw.printf("ps.setBytes(%d, EmSQL.get(%s, \"%s\", byte[].class));%n", k, orig, name);
    }

    @Override
    public void registerOut(PrintModel ipw) {
        ipw.printf("ps.registerOutParameter(++ki, Types.BINARY);%n");
    }

    @Override
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.BINARY);%n", k);
    }

    @Override
    public void csGetValue(PrintModel ipw, int k) {
        BinaryStdType.INSTANCE.csGetValue(ipw, k);
    }
}