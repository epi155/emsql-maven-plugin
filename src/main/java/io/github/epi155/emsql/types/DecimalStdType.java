package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlScalarType;
import lombok.Getter;

import java.util.Collection;
import java.util.Set;

public final class DecimalStdType implements SqlScalarType {
    public static final DecimalStdType INSTANCE = new DecimalStdType();

    @Getter
    private final String primitive = "BigDecimal";
    @Getter
    private final String wrapper = "BigDecimal";

    private DecimalStdType() {
    }

    @Override
    public Collection<String> requires() {
        return Set.of("java.math.BigDecimal");
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