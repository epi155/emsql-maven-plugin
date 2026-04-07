package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;

import java.util.Set;

import static io.github.epi155.emsql.commons.ClassContextImpl.RUNTIME_EMSQL;
import static io.github.epi155.emsql.commons.Contexts.cc;

public final class URLNilType implements SqlDataType {
    public static final URLNilType INSTANCE = new URLNilType();

    @Getter
    private final String primitive = "URL";
    @Getter
    private final String wrapper = "URL";

    private URLNilType() {
    }

    @Override
    public boolean isNullable() {
        return true;
    }

    @Override
    public java.util.Collection<String> requires() {
        return Set.of("java.net.URL");
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("EmSQL.setURL(ps, ++ki, %s);%n", source);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("EmSQL.setURL(ps, %d, %s);%n", k, source);
    }

    @Override
    public void rsGetValue(PrintModel ipw, int k) {
        ipw.putf("rs.getURL(%d)", k);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("EmSQL.setURL(ps, ++ki, EmSQL.get(%s, \"%s\", URL.class));%n", orig, name);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name, int k) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("EmSQL.setURL(ps, %d, EmSQL.get(%s, \"%s\", URL.class));%n", k, orig, name);
    }

    @Override
    public void registerOut(PrintModel ipw) {
        ipw.printf("ps.registerOutParameter(++ki, Types.DATALINK);%n");
    }

    @Override
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.DATALINK);%n", k);
    }

    @Override
    public void csGetValue(PrintModel ipw, int k) {
        ipw.putf("ps.getURL(%d)", k);
    }
}