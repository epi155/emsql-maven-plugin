package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;

public final class LocalTimeStdType implements SqlDataType {
    public static final LocalTimeStdType INSTANCE = new LocalTimeStdType();

    @Getter
    private final String primitive = "LocalTime";
    @Getter
    private final String wrapper = "LocalTime";

    private LocalTimeStdType() {
    }

    @Override
    public java.util.Collection<String> requires() {
        return java.util.Set.of("java.time.LocalTime");
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        ipw.printf("ps.setObject(++ki, %s);%n", source);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        ipw.printf("ps.setObject(%d, %s);%n", k, source);
    }

    @Override
    public void rsGetValue(PrintModel ipw, int k) {
        ipw.putf("rs.getObject(%d, LocalTime.class)", k);
    }



    @Override
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.TIME);%n", k);
    }

    @Override
    public void csGetValue(PrintModel ipw, int k) {
        ipw.putf("ps.getObject(%d, LocalTime.class)", k);
    }
}