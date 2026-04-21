package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;

import static io.github.epi155.emsql.commons.ClassContextImpl.RUNTIME_J8TIME;

public final class LocalDateTimeNilType implements SqlDataType {
    public static final LocalDateTimeNilType INSTANCE = new LocalDateTimeNilType();

    @Getter
    private final String primitive = "LocalDateTime";
    @Getter
    private final String wrapper = "LocalDateTime";

    private LocalDateTimeNilType() {
    }

    @Override
    public boolean isNullable() {
        return true;
    }

    @Override
    public java.util.Collection<String> requires() {
        return java.util.Set.of("java.time.LocalDateTime", RUNTIME_J8TIME);
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        ipw.printf("J8Time.setTimestamp(ps, ++ki, %s);%n", source);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        ipw.printf("J8Time.setTimestamp(ps, %d, %s);%n", k, source);
    }

    @Override
    public void rsGetValue(PrintModel ipw, int k) {
        LocalDateTimeStdType.INSTANCE.rsGetValue(ipw, k);
    }



    @Override
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.TIMESTAMP);%n", k);
    }

    @Override
    public void csGetValue(PrintModel ipw, int k) {
        LocalDateTimeStdType.INSTANCE.csGetValue(ipw, k);
    }
}