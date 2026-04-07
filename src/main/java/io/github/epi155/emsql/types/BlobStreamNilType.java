package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;

import java.util.Set;

import static io.github.epi155.emsql.commons.ClassContextImpl.RUNTIME_EMSQL;
import static io.github.epi155.emsql.commons.Contexts.cc;

public final class BlobStreamNilType implements SqlDataType {
    public static final BlobStreamNilType INSTANCE = new BlobStreamNilType();

    @Getter
    private final String primitive = "InputStream";
    @Getter
    private final String wrapper = "InputStream";

    private BlobStreamNilType() {
    }

    @Override
    public boolean isNullable() {
        return true;
    }

    @Override
    public java.util.Collection<String> requires() {
        return Set.of("java.io.InputStream");
    }

    @Override
    public void rsGetValue(PrintModel ipw, int k) {
        BlobStreamStdType.INSTANCE.rsGetValue(ipw, k);
    }

    @Override
    public void csGetValue(PrintModel ipw, int k) {
        BlobStreamStdType.INSTANCE.csGetValue(ipw, k);
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("EmSQL.setBlobStream(ps, ++ki, %s);%n", source);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("EmSQL.setBlobStream(ps, %d, %s);%n", k, source);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("EmSQL.setBlobStream(ps, ++ki, EmSQL.get(%s, \"%s\", InputStream.class));%n", orig, name);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name, int k) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("EmSQL.setBlobStream(ps, %d, EmSQL.get(%s, \"%s\", InputStream.class));%n", k, orig, name);
    }

    @Override
    public void registerOut(PrintModel ipw) {
        ipw.printf("ps.registerOutParameter(++ki, Types.BLOB);%n");
    }

    @Override
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.BLOB);%n", k);
    }
}