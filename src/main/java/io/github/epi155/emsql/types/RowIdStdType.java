package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;

public final class RowIdStdType implements SqlDataType {
    public static final RowIdStdType INSTANCE = new RowIdStdType();

    @Getter
    private final String primitive = "RowId";
    @Getter
    private final String wrapper = "RowId";

    private RowIdStdType() {
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        ipw.printf("ps.setRowId(++ki, %s);%n", source);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        ipw.printf("ps.setRowId(%d, %s);%n", k, source);
    }

    @Override
    public void rsGetValue(PrintModel ipw, int k) {
        ipw.putf("rs.getRowId(%d)", k);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name) {
        ipw.printf("ps.setRowId(++ki, EmSQL.get(%s, \"%s\", RowId.class));%n", orig, name);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name, int k) {
        ipw.printf("ps.setRowId(%d, EmSQL.get(%s, \"%s\", RowId.class));%n", k, orig, name);
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