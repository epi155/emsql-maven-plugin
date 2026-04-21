package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;

import java.util.Set;

public final class URLStdType implements SqlDataType {
    public static final URLStdType INSTANCE = new URLStdType();

    @Getter
    private final String primitive = "URL";
    @Getter
    private final String wrapper = "URL";

    private URLStdType() {
    }

    @Override
    public java.util.Collection<String> requires() {
        return Set.of("java.net.URL");
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        ipw.printf("ps.setURL(++ki, %s);%n", source);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        ipw.printf("ps.setURL(%d, %s);%n", k, source);
    }

    @Override
    public void rsGetValue(PrintModel ipw, int k) {
        ipw.putf("rs.getURL(%d)", k);
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