package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlScalarType;
import lombok.Getter;

import java.util.Set;

import static io.github.epi155.emsql.commons.ClassContextImpl.RUNTIME_EMSQL;

public final class NumBoolNilType implements SqlScalarType, SqlNullType {
    public static final NumBoolNilType INSTANCE = new NumBoolNilType();

    @Getter
    private final String primitive = "Boolean";
    @Getter
    private final String wrapper = "Boolean";

    private NumBoolNilType() {
    }

    public java.util.Collection<String> requires() {
        return Set.of(RUNTIME_EMSQL);
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        ipw.printf("EmSQL.setNumBool(ps, ++ki, %s);%n", source);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        ipw.printf("EmSQL.setNumBool(ps, %d, %s);%n", k, source);
    }

    @Override
    public void rsGetValue(PrintModel ipw, int k) {
        ipw.putf("EmSQL.getNumBool(rs,%d)", k);
    }

    @Override
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.TINYINT);%n", k);
    }

    @Override
    public void csGetValue(PrintModel ipw, int k) {
        ipw.putf("EmSQL.getNumBool(ps,%d)", k);
    }
}