package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;

import static io.github.epi155.emsql.commons.ClassContextImpl.RUNTIME_EMSQL;
import static io.github.epi155.emsql.commons.Contexts.cc;

public final class TimestampNilType implements SqlDataType {
    public static final TimestampNilType INSTANCE = new TimestampNilType();

    @Getter
    private final String primitive = "Timestamp";
    @Getter
    private final String wrapper = "Timestamp";

    private TimestampNilType() {
    }

    @Override
    public boolean isNullable() {
        return true;
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("EmSQL.setTimestamp(ps, ++ki, %s);%n", source);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("EmSQL.setTimestamp(ps, %d, %s);%n", k, source);
    }

    @Override
    public void rsGetValue(PrintModel ipw, int k) {
        cc.add(RUNTIME_EMSQL);
        ipw.putf("EmSQL.getTimestamp(rs,%d)", k);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name) {
        ipw.printf("EmSQL.setTimestamp(ps, ++ki, EmSQL.get(%s, \"%s\", Timestamp.class));%n", orig, name);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name, int k) {
        ipw.printf("EmSQL.setTimestamp(ps, %d, EmSQL.get(%s, \"%s\", Timestamp.class));%n", k, orig, name);
    }

    @Override
    public void registerOut(PrintModel ipw) {
        ipw.printf("ps.registerOutParameter(++ki, Types.TIMESTAMP);%n");
    }

    @Override
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.TIMESTAMP);%n", k);
    }

    @Override
    public void csGetValue(PrintModel ipw, int k) {
        cc.add(RUNTIME_EMSQL);
        ipw.putf("EmSQL.getTimestamp(ps,%d)", k);
    }
}