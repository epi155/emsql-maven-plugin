package io.github.epi155.emsql.types;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;

public final class TimestampZStdType implements SqlDataType {
    public static final TimestampZStdType INSTANCE = new TimestampZStdType();

    @Getter
    private final String primitive = "OffsetDateTime";
    @Getter
    private final String wrapper = "OffsetDateTime";

    private TimestampZStdType() {
    }

    @Override
    public java.util.Collection<String> requires() {
        return java.util.Set.of("java.time.OffsetDateTime");
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
        ipw.putf("rs.getObject(%d, OffsetDateTime.class)", k);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name) {
        ipw.printf("ps.setObject(++ki, EmSQL.get(%s, \"%s\", OffsetDateTime.class));%n", orig, name);
    }

    @Override
    public void xPsPush(PrintModel ipw, String orig, String name, int k) {
        ipw.printf("ps.setObject(%d, EmSQL.get(%s, \"%s\", OffsetDateTime.class));%n", k, orig, name);
    }

    @Override
    public void registerOut(PrintModel ipw) {
        ipw.printf("ps.registerOutParameter(++ki, Types.TIMESTAMP_WITH_TIMEZONE);%n");
    }

    @Override
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.TIMESTAMP_WITH_TIMEZONE);%n", k);
    }

    @Override
    public void csGetValue(PrintModel ipw, int k) {
        ipw.putf("ps.getObject(%d, OffsetDateTime.class)", k);
    }
}