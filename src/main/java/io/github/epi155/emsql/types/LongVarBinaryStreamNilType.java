package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlScalarType;
import lombok.Getter;

import java.util.Set;

import static io.github.epi155.emsql.commons.ClassContextImpl.RUNTIME_EMSQL;
import static io.github.epi155.emsql.commons.Contexts.cc;

public final class LongVarBinaryStreamNilType implements SqlScalarType, SqlNullType {
    public static final LongVarBinaryStreamNilType INSTANCE = new LongVarBinaryStreamNilType();

    @Getter
    private final String primitive = "InputStream";
    @Getter
    private final String wrapper = "InputStream";

    private LongVarBinaryStreamNilType() {
    }

    public java.util.Collection<String> requires() {
        return Set.of("java.io.InputStream");
    }

    @Override
    public void rsGetValue(PrintModel ipw, int k) {
        LongVarBinaryStreamStdType.INSTANCE.rsGetValue(ipw, k);
    }

    @Override
    public void csGetValue(PrintModel ipw, int k) {
        LongVarBinaryStreamStdType.INSTANCE.csGetValue(ipw, k);
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("EmSQL.setBinaryStream(ps, ++ki, %s);%n", source);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("EmSQL.setBinaryStream(ps, %d, %s);%n", k, source);
    }



    @Override
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.LONGVARBINARY);%n", k);
    }
}