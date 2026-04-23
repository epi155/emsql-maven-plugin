package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlScalarType;
import lombok.Getter;

public final class VarBinaryNilType implements SqlScalarType, SqlNullType {
    public static final VarBinaryNilType INSTANCE = new VarBinaryNilType();

    @Getter
    private final String primitive = "byte[]";
    @Getter
    private final String wrapper = "byte[]";

    private VarBinaryNilType() {
    }

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
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.VARBINARY);%n", k);
    }

    @Override
    public void csGetValue(PrintModel ipw, int k) {
        VarBinaryStdType.INSTANCE.csGetValue(ipw, k);
    }
}