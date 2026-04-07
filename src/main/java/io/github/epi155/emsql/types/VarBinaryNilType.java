package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;

public final class VarBinaryNilType implements SqlDataType {
    public static final VarBinaryNilType INSTANCE = new VarBinaryNilType();

    @Getter
    private final String primitive = "byte[]";
    @Getter
    private final String wrapper = "byte[]";

    private VarBinaryNilType() {
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
        VarBinaryStdType.INSTANCE.rsGetValue(ipw, k);
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
        ipw.printf("ps.registerOutParameter(++ki, Types.VARBINARY);%n");
    }

    @Override
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.VARBINARY);%n", k);
    }

    @Override
    public void csGetValue(PrintModel ipw, int k) {
        VarBinaryStdType.INSTANCE.csGetValue(ipw, k);
    }
}