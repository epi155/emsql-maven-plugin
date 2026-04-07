package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;

import static io.github.epi155.emsql.commons.ClassContextImpl.RUNTIME_EMSQL;
import static io.github.epi155.emsql.commons.Contexts.cc;

public final class RowIdNilType implements SqlDataType {
    public static final RowIdNilType INSTANCE = new RowIdNilType();

    @Getter
    private final String primitive = "RowId";
    @Getter
    private final String wrapper = "RowId";

    private RowIdNilType() {
    }

    @Override
    public boolean isNullable() {
        return true;
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("EmSQL.setRowId(ps, ++ki, %s);%n", source);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("EmSQL.setRowId(ps, %d, %s);%n", k, source);
    }

    @Override
    public void rsGetValue(PrintModel ipw, int k) {
        ipw.putf("rs.getRowId(%d)", k);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("EmSQL.setRowId(ps, ++ki, EmSQL.get(%s, \"%s\", RowId.class));%n", orig, name);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name, int k) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("EmSQL.setRowId(ps, %d, EmSQL.get(%s, \"%s\", RowId.class));%n", k, orig, name);
    }

    @Override
    public void registerOut(PrintModel ipw) {
        ipw.printf("ps.registerOutParameter(++ki, Types.ROWID);%n");
    }

    @Override
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.ROWID);%n", k);
    }

    @Override
    public void csGetValue(PrintModel ipw, int k) {
        ipw.putf("ps.getRowId(%d)", k);
    }
}