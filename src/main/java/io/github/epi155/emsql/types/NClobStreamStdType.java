package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlScalarType;
import lombok.Getter;

import java.util.Set;

public final class NClobStreamStdType implements SqlScalarType {
    public static final NClobStreamStdType INSTANCE = new NClobStreamStdType();

    @Getter
    private final String primitive = "Reader";
    @Getter
    private final String wrapper = "Reader";

    private NClobStreamStdType() {
    }

    @Override
    public java.util.Collection<String> requires() {
        return Set.of("java.io.Reader");
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        ipw.printf("ps.setNClob(++ki, %s);%n", source);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        ipw.printf("ps.setNClob(%d, %s);%n", k, source);
    }

    @Override
    public void rsGetValue(PrintModel ipw, int k) {
        ipw.putf("rs.getNCharacterStream(%d)", k);
    }

    @Override
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.CLOB);%n", k);
    }

    @Override
    public void csGetValue(PrintModel ipw, int k) {
        ipw.putf("ps.getNCharacterStream(%d)", k);
    }
}