package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;

public final class TimeZStdType implements SqlDataType {
    public static final TimeZStdType INSTANCE = new TimeZStdType();

    @Getter
    private final String primitive = "OffsetTime";
    @Getter
    private final String wrapper = "OffsetTime";

    private TimeZStdType() {
    }

    @Override
    public java.util.Collection<String> requires() {
        return java.util.Set.of("java.time.OffsetTime");
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
        ipw.putf("rs.getObject(%d, OffsetTime.class)", k);
    }

    @Override
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.TIME_WITH_TIMEZONE);%n", k);
    }

    @Override
    public void csGetValue(PrintModel ipw, int k) {
        ipw.putf("ps.getObject(%d, OffsetTime.class)", k);
    }
}