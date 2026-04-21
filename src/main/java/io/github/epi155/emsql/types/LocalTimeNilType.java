package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;

import static io.github.epi155.emsql.commons.ClassContextImpl.RUNTIME_J8TIME;

public final class LocalTimeNilType implements SqlDataType {
    public static final LocalTimeNilType INSTANCE = new LocalTimeNilType();

    @Getter
    private final String primitive = "LocalTime";
    @Getter
    private final String wrapper = "LocalTime";

    private LocalTimeNilType() {
    }

    @Override
    public boolean isNullable() {
        return true;
    }

    @Override
    public java.util.Collection<String> requires() {
        return java.util.Set.of("java.time.LocalTime", RUNTIME_J8TIME);
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        ipw.printf("J8Time.setTime(ps, ++ki, %s);%n", source);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        ipw.printf("J8Time.setTime(ps, %d, %s);%n", k, source);
    }

    @Override
    public void rsGetValue(PrintModel ipw, int k) {
        LocalTimeStdType.INSTANCE.rsGetValue(ipw, k);
    }



    @Override
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.TIME);%n", k);
    }

    @Override
    public void csGetValue(PrintModel ipw, int k) {
        LocalTimeStdType.INSTANCE.csGetValue(ipw, k);
    }
}