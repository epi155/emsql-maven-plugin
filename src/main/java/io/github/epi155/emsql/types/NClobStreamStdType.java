package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;

import java.util.Set;

public final class NClobStreamStdType implements SqlDataType {
    public static final NClobStreamStdType INSTANCE = new NClobStreamStdType();

    @Getter
    private final String primitive = "Reader";
    @Getter
    private final String wrapper = "Reader";

    private NClobStreamStdType() {
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public java.util.Collection<String> requires() {
        return Set.of("java.io.Reader");
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
        ipw.putf("rs.getNCharacterStream(%d)", k);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name) {
        ipw.printf("ps.setNClob(++ki, EmSQL.get(%s, \"%s\", Reader.class));%n", orig, name);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name, int k) {
        ipw.printf("ps.setNClob(%d, EmSQL.get(%s, \"%s\", Reader.class));%n", k, orig, name);
    }

    @Override
    public void registerOut(PrintModel ipw) {
        ipw.printf("ps.registerOutParameter(++ki, Types.CLOB);%n");
    }

    @Override
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.CLOB);%n", k);
    }

    @Override
    public void csGetValue(PrintModel ipw, int k) {
        ipw.putf("ps.getNCharacterStream(%d)", k);
    }
}