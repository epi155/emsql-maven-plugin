package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;

import java.util.Collection;
import java.util.Set;

import static io.github.epi155.emsql.commons.Contexts.cc;

public enum SqlEnum implements SqlDataType {
    BooleanStd("Boolean", "BOOLEAN", "boolean", "Boolean") {
        public String getterPrefix() { return "is"; }
    },
    BooleanNil("Boolean", "BOOLEAN", "Boolean"),
    ByteStd("Byte", "TINYINT", "byte", "Byte"),
    ByteNil("Byte", "TINYINT", "Byte"),
    ShortStd("Short", "SMALLINT", "short", "Short"),
    ShortNil("Short", "SMALLINT", "Short"),
    IntegerStd("Int", "INTEGER", "int", "Integer"),
    IntegerNil("Int", "INTEGER", "Integer"),
    LongStd("Long", "BIGINT", "long", "Long"),
    LongNil("Long", "BIGINT", "Long"),
    DoubleStd("Double", "DOUBLE", "double", "Double"),
    DoubleNil("Double", "DOUBLE", "Double"),
    FloatStd("Float", "FLOAT", "float", "Float"),
    FloatNil("Float", "FLOAT", "Float"),
    VarCharStd("String", "VARCHAR"),
    VarCharNil("String", "VARCHAR") {
        @Override
        public void psSet(PrintModel ipw, String source) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setVarchar(ps, ++ki, %s);%n", source);
        }
        @Override
        public void psSet(PrintModel ipw, String source, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setVarchar(ps, %d, %s);%n", k, source);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name) {
            ipw.printf("EmSQL.setVarchar(ps, ++ki, EmSQL.get(%s, \"%s\", String.class));%n", orig, name);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name, int k) {
            ipw.printf("EmSQL.setVarchar(ps, %d, EmSQL.get(%s, \"%s\", String.class));%n", k, orig, name);
        }
    },
    CharStd("String", "CHAR"),
    CharNil("String", "CHAR") {
        @Override
        public void psSet(PrintModel ipw, String source) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setChar(ps, ++ki, %s);%n", source);
        }
        @Override
        public void psSet(PrintModel ipw, String source, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setChar(ps, %d, %s);%n", k, source);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name) {
            ipw.printf("EmSQL.setChar(ps, ++ki, EmSQL.get(%s, \"%s\", String.class));%n", orig, name);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name, int k) {
            ipw.printf("EmSQL.setChar(ps, %d, EmSQL.get(%s, \"%s\", String.class));%n", k, orig, name);
        }
    },
    DateStd("Date", "DATE"),
    DateNil("Date", "DATE"),
    TimestampStd("Timestamp", "TIMESTAMP"),
    TimestampNil("Timestamp", "TIMESTAMP"),
    TimeStd("Time", "TIME"),
    TimeNil("Time", "TIME"),
    NumericStd("BigDecimal", "NUMERIC") {
        @Override
        public Collection<String> requires() {
            return Set.of("java.math.BigDecimal");
        }
    },
    NumericNil("BigDecimal", "NUMERIC") {
        @Override
        public Collection<String> requires() {
            return NumericStd.requires();
        }
    },
    @Deprecated
    DecimalStd("BigDecimal", "DECIMAL") {
        @Override
        public Collection<String> requires() {
            return Set.of("java.math.BigDecimal");
        }
    },
    @Deprecated
    DecimalNil("BigDecimal", "DECIMAL") {
        @Override
        public Collection<String> requires() {
            return NumericStd.requires();
        }
    },
    BinaryStd("Bytes", "BINARY", "byte[]"),
    BinaryNil("Bytes", "BINARY", "byte[]") {
        public void rsGetValue(PrintModel ipw, int k) { BinaryStd.rsGetValue(ipw, k); }
    },
    VarBinaryStd("Bytes", "VARBINARY", "byte[]"),
    VarBinaryNil("Bytes", "VARBINARY", "byte[]") {
        public void rsGetValue(PrintModel ipw, int k) { VarBinaryStd.rsGetValue(ipw, k); }
    },
    NumBoolStd("Boolean", "TINYINT", "boolean", "Boolean") {
        public String getterPrefix() { return "is"; }
        @Override
        public void psSet(PrintModel ipw, String source) {
            ipw.printf("ps.setByte(++ki, (byte) (%s ? 1 : 0));%n", source);
        }
        public void psSet(PrintModel ipw, String source, int k) {
            ipw.printf("ps.setByte(%d, (byte) (%s ? 1 : 0));%n", k, source);
        }
        @Override
        public void rsGetValue(PrintModel ipw, int k) {
            ipw.putf("rs.getByte(%d)==1", k);
        }
        @Override
        public void xPsPush(PrintModel ipw, String orig, String name) {
            ipw.printf("ps.setByte(++ki, (byte) (EmSQL.get(%s, \"%s\", Boolean.class) ? 1 : 0));%n", orig, name);
        }
        @Override
        public void xPsPush(PrintModel ipw, String orig, String name, int k) {
            ipw.printf("ps.setByte(%d, (byte) (EmSQL.get(%s, \"%s\", Boolean.class) ? 1 : 0));%n", k, orig, name);
        }
        @Override
        public void csGetValue(PrintModel ipw, int k) {
            ipw.putf("ps.getByte(%d)==1", k);
        }
    },
    NumBoolNil("Boolean", "TINYINT", "Boolean"){
        @Override
        public Collection<String> requires() {
            return Set.of(ClassContextImpl.RUNTIME_EMSQL);
        }
        public void psSet(PrintModel ipw, String source) {
            ipw.printf("EmSQL.setNumBool(ps, ++ki, %s);%n", source);
        }
        public void psSet(PrintModel ipw, String source, int k) {
            ipw.printf("EmSQL.setNumBool(ps, %d, %s);%n", k, source);
        }

        public void rsGetValue(PrintModel ipw, int k) {
            ipw.putf("EmSQL.getNumBool(rs,%d)", k);
        }
        public  void xPsPush(PrintModel ipw, String orig, String name) {
            ipw.printf("EmSQL.setNumBool(ps, ++ki, EmSQL.get(%s, \"%s\", Boolean.class));%n", orig, name);
        }
        public  void xPsPush(PrintModel ipw, String orig, String name, int k) {
            ipw.printf("EmSQL.setNumBool(ps, %d, EmSQL.get(%s, \"%s\", Boolean.class));%n", k, orig, name);
        }
        public void csGetValue(PrintModel ipw, int k) {
            ipw.putf("EmSQL.getNumBool(ps,%d)", k);
        }
    },
    LocalDateStd("Date", "DATE", "LocalDate") {
        @Override
        public Collection<String> requires() {
            return Set.of("java.time.LocalDate");
        }
        @Override
        public void psSet(PrintModel ipw, String source) {
            ipw.printf("ps.setDate(++ki, Date.valueOf(%s));%n", source);
        }
        @Override
        public void psSet(PrintModel ipw, String source, int k) {
            ipw.printf("ps.setDate(%d, Date.valueOf(%s));%n", k, source);
        }
        @Override
        public void rsGetValue(PrintModel ipw, int k) {
            ipw.putf("rs.getDate(%d).toLocalDate()", k);
        }
        @Override
        public void xPsPush(PrintModel ipw, String orig, String name) {
            ipw.printf("ps.setDate(++ki, Date.valueOf(EmSQL.get(%s, \"%s\", LocalDate.class)));%n", orig, name);
        }
        @Override
        public void xPsPush(PrintModel ipw, String orig, String name, int k) {
            ipw.printf("ps.setDate(%d, Date.valueOf(EmSQL.get(%s, \"%s\", LocalDate.class)));%n", k, orig, name);
        }
        @Override
        public void csGetValue(PrintModel ipw, int k) {
            ipw.putf("ps.getDate(%d).toLocalDate()", k);
        }
    },
    LocalDateNil("Date", "DATE", "LocalDate") {
        @Override
        public Collection<String> requires() {
            return Set.of("java.time.LocalDate", ClassContextImpl.RUNTIME_J8TIME);
        }
        @Override
        public void psSet(PrintModel ipw, String source) {
            ipw.printf("J8Time.setDate(ps, ++ki, %s);%n", source);
        }
        @Override
        public void psSet(PrintModel ipw, String source, int k) {
            ipw.printf("J8Time.setDate(ps, %d, %s);%n", k, source);
        }
        @Override
        public void rsGetValue(PrintModel ipw, int k) {
            ipw.putf("J8Time.toLocalDate(rs.getDate(%d))", k);
        }
        @Override
        public void xPsPush(PrintModel ipw, String orig, String name) {
            ipw.printf("J8Time.setDate(ps, ++ki, EmSQL.get(%s, \"%s\", LocalDate.class));%n", orig, name);
        }
        @Override
        public void xPsPush(PrintModel ipw, String orig, String name, int k) {
            ipw.printf("J8Time.setDate(ps, %d, EmSQL.get(%s, \"%s\", LocalDate.class));%n", k, orig, name);
        }
        @Override
        public void csGetValue(PrintModel ipw, int k) {
            ipw.putf("J8Time.toLocalDate(ps.getDate(%d))", k);
        }
    },
    LocalDateTimeStd("Timestamp", "TIMESTAMP", "LocalDateTime") {
        @Override
        public Collection<String> requires() {
            return Set.of("java.time.LocalDateTime");
        }
        @Override
        public void psSet(PrintModel ipw, String source) {
            ipw.printf("ps.setTimestamp(++ki, Timestamp.valueOf(%s));%n", source);
        }
        @Override
        public void psSet(PrintModel ipw, String source, int k) {
            ipw.printf("ps.setTimestamp(%d, Timestamp.valueOf(%s));%n", k, source);
        }
        @Override
        public void rsGetValue(PrintModel ipw, int k) {
            ipw.putf("rs.getTimestamp(%d).toLocalDateTime()", k);
        }
        @Override
        public void xPsPush(PrintModel ipw, String orig, String name) {
            ipw.printf("ps.setTimestamp(++ki, Timestamp.valueOf(EmSQL.get(%s, \"%s\", LocalDateTime.class)));%n", orig, name);
        }
        @Override
        public void xPsPush(PrintModel ipw, String orig, String name, int k) {
            ipw.printf("ps.setTimestamp(%d, Timestamp.valueOf(EmSQL.get(%s, \"%s\", LocalDateTime.class)));%n", k, orig, name);
        }
        @Override
        public void csGetValue(PrintModel ipw, int k) {
            ipw.putf("ps.getTimestamp(%d).toLocalDateTime()", k);
        }
    },
    LocalDateTimeNil("Timestamp", "TIMESTAMP", "LocalDateTime") {
        @Override
        public Collection<String> requires() {
            return Set.of("java.time.LocalDateTime", ClassContextImpl.RUNTIME_J8TIME);
        }
        @Override
        public void psSet(PrintModel ipw, String so) {
            ipw.printf("J8Time.setTimestamp(ps, ++ki, %s);%n", so);
        }
        @Override
        public void psSet(PrintModel ipw, String so, int k) {
            ipw.printf("J8Time.setTimestamp(ps, %d, %s);%n", k, so);
        }
        @Override
        public void rsGetValue(PrintModel ipw, int k) {
            ipw.putf("J8Time.toLocalDateTime(rs.getTimestamp(%d))", k);
        }
        @Override
        public void xPsPush(PrintModel ipw, String orig, String name) {
            ipw.printf("J8Time.setTimestamp(ps, ++ki, EmSQL.get(%s, \"%s\", LocalDateTime.class));%n", orig, name);
        }
        @Override
        public void xPsPush(PrintModel ipw, String orig, String name, int k) {
            ipw.printf("J8Time.setTimestamp(ps, %d, EmSQL.get(%s, \"%s\", LocalDateTime.class));%n", k, orig, name);
        }
        @Override
        public void csGetValue(PrintModel ipw, int k) {
            ipw.putf("J8Time.toLocalDateTime(ps.getTimestamp(%d))", k);
        }
    },
    LocalTimeStd("Time", "TIME", "LocalTime") {
        @Override
        public Collection<String> requires() {
            return Set.of("java.time.LocalTime");
        }
        @Override
        public void psSet(PrintModel ipw, String source) {
            ipw.printf("ps.setTime(++ki, Time.valueOf(%s));%n", source);
        }
        @Override
        public void psSet(PrintModel ipw, String source, int k) {
            ipw.printf("ps.setTime(%d, Time.valueOf(%s));%n", k, source);
        }
        @Override
        public void rsGetValue(PrintModel ipw, int k) {
            ipw.putf("rs.getTime(%d).toLocalTime()", k);
        }
        @Override
        public void xPsPush(PrintModel ipw, String orig, String name) {
            ipw.printf("ps.setTime(++ki, Time.valueOf(EmSQL.get(%s, \"%s\", LocalTime.class)));%n", orig, name);
        }
        @Override
        public void xPsPush(PrintModel ipw, String orig, String name, int k) {
            ipw.printf("ps.setTime(%d, Time.valueOf(EmSQL.get(%s, \"%s\", LocalTime.class)));%n", k, orig, name);
        }
        @Override
        public void csGetValue(PrintModel ipw, int k) {
            ipw.putf("ps.getTime(%d).toLocalTime()", k);
        }
    },
    LocalTimeNil("Time", "TIME", "LocalTime") {
        @Override
        public Collection<String> requires() {
            return Set.of("java.time.LocalTime", ClassContextImpl.RUNTIME_J8TIME);
        }
        @Override
        public void psSet(PrintModel ipw, String so) {
            ipw.printf("J8Time.setTime(ps, ++ki, %s);%n", so);
        }
        @Override
        public void psSet(PrintModel ipw, String so, int k) {
            ipw.printf("J8Time.setTime(ps, %d, %s);%n", k, so);
        }
        @Override
        public void rsGetValue(PrintModel ipw, int k) {
            ipw.putf("J8Time.toLocalTime(rs.getTime(%d))", k);
        }
        @Override
        public void xPsPush(PrintModel ipw, String orig, String name) {
            ipw.printf("J8Time.setTime(ps, ++ki, EmSQL.get(%s, \"%s\", LocalTime.class));%n", orig, name);
        }
        @Override
        public void xPsPush(PrintModel ipw, String orig, String name, int k) {
            ipw.printf("J8Time.setTime(ps, %d, EmSQL.get(%s, \"%s\", LocalTime.class));%n", k, orig, name);
        }
        @Override
        public void csGetValue(PrintModel ipw, int k) {
            ipw.putf("J8Time.toLocalTime(ps.getTime(%d))", k);
        }
    };
    private final String jdbc;
    private final String sql;
    private final boolean isPlainClass;
    @Getter
    private final String primitive;
    @Getter
    private final String wrapper;
    SqlEnum(String jdbc, String sql, String primitive, String wrapper) {
        this.jdbc = jdbc;
        this.sql = sql;
        if (primitive.equals(wrapper))
            throw new IllegalArgumentException("primitive and wrapper are equals "+primitive);
        this.primitive = primitive;
        this.wrapper = wrapper;
        this.isPlainClass = false;
    }
    SqlEnum(String jdbc, String sql, String wrapper) {
        this.jdbc = jdbc;
        this.sql = sql;
        this.primitive = wrapper;
        this.wrapper = wrapper;
        this.isPlainClass = false;
    }
    SqlEnum(String jdbc, String sql) {
        this.jdbc = jdbc;
        this.sql = sql;
        this.primitive = jdbc;
        this.wrapper = jdbc;
        this.isPlainClass = true;
    }
    public void psSet(PrintModel ipw, String source) {
        if (!isNullable()) {
            ipw.printf("ps.set%s(++ki, %s);%n", jdbc, source);
        } else {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.set%s(ps, ++ki, %s);%n", jdbc, source);
        }
    }
    public void psSet(PrintModel ipw, String source, int k) {
        if (!isNullable()) {
            ipw.printf("ps.set%s(%d, %s);%n", jdbc, k, source);
        } else {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.set%s(ps, %d, %s);%n", jdbc, k, source);
        }
    }
    public void rsGetValue(PrintModel ipw, int k) {
        if (!isNullable() || isPlainClass) {
            ipw.putf("rs.get%s(%d)", jdbc, k);
        } else {
            ipw.putf("EmSQL.get%s(rs,%d)", jdbc, k);
        }
    }
    public  void xPsPush(PrintModel ipw, String orig, String name) {
        if (!isNullable()) {
            ipw.printf("ps.set%s(++ki, EmSQL.get(%s, \"%s\", %s.class));%n", jdbc, orig, name, wrapper);
        } else {
            ipw.printf("EmSQL.set%s(ps, ++ki, EmSQL.get(%s, \"%s\", %s.class));%n", jdbc, orig, name, wrapper);
        }
    }
    public  void xPsPush(PrintModel ipw, String orig, String name, int k) {
        if (!isNullable()) {
            ipw.printf("ps.set%s(%d, EmSQL.get(%s, \"%s\", %s.class));%n", jdbc, k, orig, name, wrapper);
        } else {
            ipw.printf("EmSQL.set%s(ps, %d, EmSQL.get(%s, \"%s\", %s.class));%n", jdbc, k, orig, name, wrapper);
        }
    }
    public void registerOut(PrintModel ipw) {
        ipw.printf("ps.registerOutParameter(++ki, Types.%s);%n", sql);
    }
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.%s);%n", k, sql);
    }
    public void csGetValue(PrintModel ipw, int k) {
        if (!isNullable() || isPlainClass) {
            ipw.putf("ps.get%s(%d)", jdbc, k);
        } else {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.putf("EmSQL.get%s(ps,%d)", jdbc, k);
        }
    }
    @Override
    public boolean isNullable() {
        return name().endsWith("Nil");
    }
}
