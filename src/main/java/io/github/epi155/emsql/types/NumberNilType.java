package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;

import static io.github.epi155.emsql.commons.ClassContextImpl.RUNTIME_EMSQL;
import static io.github.epi155.emsql.commons.Contexts.cc;

public final class NumberNilType implements SqlDataType {
    public static final NumberNilType INSTANCE = new NumberNilType();

    @Getter
    private final String primitive = "BigInteger";
    @Getter
    private final String wrapper = "BigInteger";

    private NumberNilType() {
    }

    @Override
    public boolean isNullable() {
        return true;
    }

    @Override
    public java.util.Collection<String> requires() {
        return NumberStdType.INSTANCE.requires();
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        cc.add("java.math.BigDecimal");
        ipw.printf("ps.setBigDecimal(++ki, %1$s==null ? null : new BigDecimal(%1$s));%n", source);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        cc.add("java.math.BigDecimal");
        ipw.printf("ps.setBigDecimal(%d, %2$s==null ? null : new BigDecimal(%2$s));%n", k, source);
    }

    @Override
    public void rsGetValue(PrintModel ipw, int k) {
        cc.add(RUNTIME_EMSQL);
        ipw.putf("EmSQL.toBigInteger(rs.getBigDecimal(%d))", k);
    }



    @Override
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.NUMERIC);%n", k);
    }

    @Override
    public void csGetValue(PrintModel ipw, int k) {
        cc.add(RUNTIME_EMSQL);
        ipw.putf("EmSQL.toBigInteger(ps.getBigDecimal(%d))", k);
    }
}