package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;

public final class ShortStdType implements SqlDataType {
    public static final ShortStdType INSTANCE = new ShortStdType();

    @Getter
    private final String primitive = "short";
    @Getter
    private final String wrapper = "Short";

    private ShortStdType() {
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        ipw.printf("ps.setShort(++ki, %s);%n", source);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        ipw.printf("ps.setShort(%d, %s);%n", k, source);
    }

    @Override
    public void rsGetValue(PrintModel ipw, int k) {
        ipw.putf("rs.getShort(%d)", k);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name) {
        ipw.printf("ps.setShort(++ki, EmSQL.get(%s, \"%s\", Short.class));%n", orig, name);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name, int k) {
        ipw.printf("ps.setShort(%d, EmSQL.get(%s, \"%s\", Short.class));%n", k, orig, name);
    }

    @Override
    public void registerOut(PrintModel ipw) {
        ipw.printf("ps.registerOutParameter(++ki, Types.SMALLINT);%n");
    }

    @Override
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.SMALLINT);%n", k);
    }

    @Override
    public void csGetValue(PrintModel ipw, int k) {
        ipw.putf("ps.getShort(%d)", k);
    }
}