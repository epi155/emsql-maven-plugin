package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;

import static io.github.epi155.emsql.commons.ClassContextImpl.RUNTIME_EMSQL;
import static io.github.epi155.emsql.commons.Contexts.cc;

public final class RefNilType implements SqlDataType {
    public static final RefNilType INSTANCE = new RefNilType();

    @Getter
    private final String primitive = "Ref";
    @Getter
    private final String wrapper = "Ref";

    private RefNilType() {
    }

    @Override
    public boolean isNullable() {
        return true;
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("EmSQL.setRef(ps, ++ki, %s);%n", source);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("EmSQL.setRef(ps, %d, %s);%n", k, source);
    }

    @Override
    public void rsGetValue(PrintModel ipw, int k) {
        ipw.putf("rs.getRef(%d)", k);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("EmSQL.setRef(ps, ++ki, EmSQL.get(%s, \"%s\", Ref.class));%n", orig, name);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name, int k) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("EmSQL.setRef(ps, %d, EmSQL.get(%s, \"%s\", Ref.class));%n", k, orig, name);
    }

    @Override
    public void registerOut(PrintModel ipw) {
        ipw.printf("ps.registerOutParameter(++ki, Types.REF);%n");
    }

    @Override
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.REF);%n", k);
    }

    @Override
    public void csGetValue(PrintModel ipw, int k) {
        ipw.putf("ps.getRef(%d)", k);
    }
}