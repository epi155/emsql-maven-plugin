package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;

import java.util.Collection;
import java.util.Set;

import static io.github.epi155.emsql.commons.Contexts.cc;

public final class NumberStdType implements SqlDataType {
    public static final NumberStdType INSTANCE = new NumberStdType();

    @Getter
    private final String primitive = "BigInteger";
    @Getter
    private final String wrapper = "BigInteger";

    private NumberStdType() {
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public Collection<String> requires() {
        return Set.of("java.math.BigInteger");
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        cc.add("java.math.BigDecimal");
        ipw.printf("ps.setBigDecimal(++ki, new BigDecimal(%s));%n", source);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        cc.add("java.math.BigDecimal");
        ipw.printf("ps.setBigDecimal(%d, new BigDecimal(%s));%n", k, source);
    }

    @Override
    public void rsGetValue(PrintModel ipw, int k) {
        ipw.putf("rs.getBigDecimal(%d).toBigInteger()", k);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name) {
        cc.add("java.math.BigDecimal");
        cc.add("io.github.epi155.emsql.runtime.EmSQL");
        ipw.printf("ps.setBigDecimal(++ki, new BigDecimal(EmSQL.get(%s, \"%s\", BigInteger.class)));%n", orig, name);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name, int k) {
        cc.add("java.math.BigDecimal");
        cc.add("io.github.epi155.emsql.runtime.EmSQL");
        ipw.printf("ps.setBigDecimal(%d, new BigDecimal(EmSQL.get(%s, \"%s\", BigInteger.class)));%n", k, orig, name);
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
        ipw.putf("ps.getBigDecimal(%d).toBigInteger()", k);
    }
}