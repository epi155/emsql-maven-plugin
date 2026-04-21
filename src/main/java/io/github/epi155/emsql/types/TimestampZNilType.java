package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;

import static io.github.epi155.emsql.commons.ClassContextImpl.RUNTIME_J8TIME;
import static io.github.epi155.emsql.commons.Contexts.cc;

public final class TimestampZNilType implements SqlDataType {
    public static final TimestampZNilType INSTANCE = new TimestampZNilType();

    @Getter
    private final String primitive = "OffsetDateTime";
    @Getter
    private final String wrapper = "OffsetDateTime";

    private TimestampZNilType() {
    }

    @Override
    public boolean isNullable() {
        return true;
    }

    @Override
    public java.util.Collection<String> requires() {
        return TimestampZStdType.INSTANCE.requires();
    }

    @Override
    public void rsGetValue(PrintModel ipw, int k) {
        TimestampZStdType.INSTANCE.rsGetValue(ipw, k);
    }

    @Override
    public void psSet(PrintModel ipw, String source) {
        cc.add(RUNTIME_J8TIME);
        ipw.printf("J8Time.setOffsetDateTime(ps, ++ki, %s);%n", source);
    }

    @Override
    public void psSet(PrintModel ipw, String source, int k) {
        cc.add(RUNTIME_J8TIME);
        ipw.printf("J8Time.setOffsetDateTime(ps, %d, %s);%n", k, source);
    }

    @Override
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.TIMESTAMP_WITH_TIMEZONE);%n", k);
    }

    @Override
    public void csGetValue(PrintModel ipw, int k) {
        TimestampZStdType.INSTANCE.csGetValue(ipw, k);
    }
}