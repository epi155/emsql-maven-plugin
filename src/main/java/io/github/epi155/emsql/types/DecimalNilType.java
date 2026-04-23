package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlScalarType;
import lombok.Getter;

import java.util.Collection;

public final class DecimalNilType implements SqlScalarType, SqlNullType {
    public static final DecimalNilType INSTANCE = new DecimalNilType();

    @Getter
    private final String primitive = "BigDecimal";
    @Getter
    private final String wrapper = "BigDecimal";

    private DecimalNilType() {
    }

    public Collection<String> requires() {
        return DecimalStdType.INSTANCE.requires();
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        ipw.printf("ps.setBigDecimal(++ki, %s);%n", source);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        ipw.printf("ps.setBigDecimal(%d, %s);%n", k, source);
    }

    @Override
    public void rsGetValue(PrintModel ipw, int k) {
        ipw.putf("rs.getBigDecimal(%d)", k);
    }

    @Override
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.NUMERIC);%n", k);
    }

    @Override
    public void csGetValue(PrintModel ipw, int k) {
        ipw.putf("ps.getBigDecimal(%d)", k);
    }
}