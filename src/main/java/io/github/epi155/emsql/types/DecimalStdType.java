package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;

import java.util.Collection;
import java.util.Set;

public final class DecimalStdType implements SqlDataType {
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
    public void xPsPush(PrintModel ipw, String orig, String name) {
        ipw.printf("ps.setBigDecimal(++ki, EmSQL.get(%s, \"%s\", BigDecimal.class));%n", orig, name);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name, int k) {
        ipw.printf("ps.setBigDecimal(%d, EmSQL.get(%s, \"%s\", BigDecimal.class));%n", k, orig, name);
    }

    @Override
    public void registerOut(PrintModel ipw) {
        ipw.printf("ps.registerOutParameter(++ki, Types.NUMERIC);%n");
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