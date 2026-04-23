package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlScalarType;
import lombok.Getter;

import java.util.Set;

public final class LongVarBinaryStreamStdType implements SqlScalarType {
    public static final LongVarBinaryStreamStdType INSTANCE = new LongVarBinaryStreamStdType();

    @Getter
    private final String primitive = "InputStream";
    @Getter
    private final String wrapper = "InputStream";

    private LongVarBinaryStreamStdType() {
    }

    @Override
    public java.util.Collection<String> requires() {
        return Set.of("java.io.InputStream");
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        ipw.printf("ps.setBinaryStream(++ki, %s);%n", source);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        ipw.printf("ps.setBinaryStream(%d, %s);%n", k, source);
    }

    @Override
    public void rsGetValue(PrintModel ipw, int k) {
        ipw.putf("rs.getBinaryStream(%d)", k);
    }

    @Override
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.LONGVARBINARY);%n", k);
    }

    @Override
    public void csGetValue(PrintModel ipw, int k) {
        ipw.putf("ps.getBlob(%d).getBinaryStream()", k);
    }
}