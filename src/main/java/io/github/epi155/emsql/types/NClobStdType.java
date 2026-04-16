package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;

public final class NClobStdType implements SqlDataType {
    public static final NClobStdType INSTANCE = new NClobStdType();

    @Getter
    private final String primitive = "NClob";
    @Getter
    private final String wrapper = "NClob";

    private NClobStdType() {
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        ipw.printf("ps.setNClob(++ki, %s);%n", source);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        ipw.printf("ps.setNClob(%d, %s);%n", k, source);
    }

    @Override
    public void rsGetValue(PrintModel ipw, int k) {
        ipw.putf("rs.getNClob(%d)", k);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name) {
        ipw.printf("ps.setNClob(++ki, EmSQL.get(%s, \"%s\", NClob.class));%n", orig, name);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name, int k) {
        ipw.printf("ps.setNClob(%d, EmSQL.get(%s, \"%s\", NClob.class));%n", k, orig, name);
    }

    @Override
    public void registerOut(PrintModel ipw) {
        ipw.printf("ps.registerOutParameter(++ki, Types.NCLOB);%n");
    }

    @Override
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.NCLOB);%n", k);
    }

    @Override
    public void csGetValue(PrintModel ipw, int k) {
        ipw.putf("ps.getNClob(%d)", k);
    }
}