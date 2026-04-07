package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;

import java.util.Set;

public final class BlobStreamStdType implements SqlDataType {
    public static final BlobStreamStdType INSTANCE = new BlobStreamStdType();

    @Getter
    private final String primitive = "InputStream";
    @Getter
    private final String wrapper = "InputStream";

    private BlobStreamStdType() {
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public java.util.Collection<String> requires() {
        return Set.of("java.io.InputStream");
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        ipw.printf("ps.setBlob(++ki, %s);%n", source);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        ipw.printf("ps.setBlob(%d, %s);%n", k, source);
    }

    @Override
    public void rsGetValue(PrintModel ipw, int k) {
        ipw.putf("rs.getBinaryStream(%d)", k);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name) {
        ipw.printf("ps.setBlob(++ki, EmSQL.get(%s, \"%s\", InputStream.class));%n", orig, name);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name, int k) {
        ipw.printf("ps.setBlob(%d, EmSQL.get(%s, \"%s\", InputStream.class));%n", k, orig, name);
    }

    @Override
    public void registerOut(PrintModel ipw) {
        ipw.printf("ps.registerOutParameter(++ki, Types.BLOB);%n");
    }

    @Override
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.BLOB);%n", k);
    }

    @Override
    public void csGetValue(PrintModel ipw, int k) {
        throw new IllegalArgumentException("Output BinaryStream not present in CallableStatement");
    }
}