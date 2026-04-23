package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlScalarType;
import lombok.Getter;

import static io.github.epi155.emsql.commons.ClassContextImpl.RUNTIME_EMSQL;
import static io.github.epi155.emsql.commons.Contexts.cc;

public final class NVarCharNilType implements SqlScalarType, SqlNullType {
    public static final NVarCharNilType INSTANCE = new NVarCharNilType();

    @Getter
    private final String primitive = "String";
    @Getter
    private final String wrapper = "String";

    private NVarCharNilType() {
    }

    public void rsGetValue(PrintModel ipw, int k) {
        ipw.putf("rs.getNString(%d)", k);
    }

    @Override
    public void csGetValue(PrintModel ipw, int k) {
        ipw.putf("ps.getNString(%d)", k);
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("EmSQL.setNVarChar(ps, ++ki, %s);%n", source);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("EmSQL.setNVarChar(ps, %d, %s);%n", k, source);
    }



    @Override
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.NVARCHAR);%n", k);
    }
}