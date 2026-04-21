package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;

import static io.github.epi155.emsql.commons.ClassContextImpl.RUNTIME_J8TIME;
import static io.github.epi155.emsql.commons.Contexts.cc;

public final class TimeZNilType implements SqlDataType {
    public static final TimeZNilType INSTANCE = new TimeZNilType();

    @Getter
    private final String primitive = "OffsetTime";
    @Getter
    private final String wrapper = "OffsetTime";

    private TimeZNilType() {
    }

    @Override
    public boolean isNullable() {
        return true;
    }

    @Override
    public java.util.Collection<String> requires() {
        return TimeZStdType.INSTANCE.requires();
    }

    @Override
    public void rsGetValue(PrintModel ipw, int k) {
        TimeZStdType.INSTANCE.rsGetValue(ipw, k);
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        cc.add(RUNTIME_J8TIME);
        ipw.printf("J8Time.setOffsetTime(ps, ++ki, %s);%n", source);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        cc.add(RUNTIME_J8TIME);
        ipw.printf("J8Time.setOffsetTime(ps, %d, %s);%n", k, source);
    }



    @Override
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.TIME_WITH_TIMEZONE);%n", k);
    }

    @Override
    public void csGetValue(PrintModel ipw, int k) {
        TimeZStdType.INSTANCE.csGetValue(ipw, k);
    }
}