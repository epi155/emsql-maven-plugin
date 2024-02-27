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
        public  void xPsPush(PrintModel ipw, String orig, String name) {
            ipw.printf("EmSQL.setVarchar(ps, ++ki, EmSQL.get(%s, \"%s\", String.class));%n", orig, name);
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
        public  void xPsPush(PrintModel ipw, String orig, String name) {
            ipw.printf("EmSQL.setChar(ps, ++ki, EmSQL.get(%s, \"%s\", String.class));%n", orig, name);
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
    NumBoolStd("Boolean", "TINYINT", "boolean", "Boolean") {
        public String getterPrefix() { return "is"; }
        @Override
        public void psSet(PrintModel ipw, String source) {
            ipw.printf("ps.setByte(++ki, (byte) (%s ? 1 : 0));%n", source);
        }
        @Override
        public void rsGet(PrintModel ipw, int k, String target) {
            ipw.printf("%s(rs.getByte(%d)==1);%n", target, k);
        }
        @Override
        public void rsGetValue(PrintModel ipw, int k) {
            ipw.printf("boolean o = rs.getByte(%d)==1;%n", k);
        }
        @Override
        public void rsPull(PrintModel ipw, Integer k, String name) {
            ipw.printf("EmSQL.set(o, \"%s\", rs.getByte(%d)==1);%n", name, k);
        }
        @Override
        public void xPsPush(PrintModel ipw, String orig, String name) {
            ipw.printf("ps.setByte(++ki, (byte) (EmSQL.get(%s, \"%s\", Boolean.class) ? 1 : 0));%n", orig, name);
        }
        @Override
        public void csGetValue(PrintModel ipw, int k) {
            ipw.printf("return ps.getByte(%d)==1;%n", k);
        }
        @Override
        public void csGet(PrintModel ipw, int k, String setter) {
            ipw.printf("o.%s(ps.getByte(%d)==1);%n", setter, k);
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
        public  void rsGet(PrintModel ipw, int k, String target) {
            ipw.printf("%s(EmSQL.getNumBool(rs,%d));%n", target, k);
        }
        public void rsGetValue(PrintModel ipw, int k) {
            ipw.printf("Boolean o = EmSQL.getNumBool(rs,%d);%n", k);
        }
        public void rsPull(PrintModel ipw, Integer k, String name) {
            ipw.printf("EmSQL.set(o, \"%s\", EmSQL.getNumBool(rs,%d));%n", name, k);
        }
        public  void xPsPush(PrintModel ipw, String orig, String name) {
            ipw.printf("EmSQL.setNumBool(ps, ++ki, EmSQL.get(%s, \"%s\", Boolean.class));%n", orig, name);
        }
        public void csGetValue(PrintModel ipw, int k) {
            ipw.printf("return EmSQL.getNumBool(ps,%d)%n", k);
        }
        public void csGet(PrintModel ipw, int k, String setter) {
            ipw.printf("o.%s(EmSQL.getNumBool(ps, %d));%n", setter, k);
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
        public void rsGet(PrintModel ipw, int k, String target) {
            ipw.printf("%s(rs.getDate(%d).toLocalDate());%n", target, k);
        }
        @Override
        public void rsGetValue(PrintModel ipw, int k) {
            ipw.printf("LocalDate o = rs.getDate(%d).toLocalDate();%n", k);
        }
        @Override
        public void rsPull(PrintModel ipw, Integer k, String name) {
            ipw.printf("EmSQL.set(o, \"%s\", rs.getDate(%d).toLocalDate());%n", name, k);
        }
        @Override
        public void xPsPush(PrintModel ipw, String orig, String name) {
            ipw.printf("ps.setDate(++ki, Date.valueOf(EmSQL.get(%s, \"%s\", LocalDate.class)));%n", orig, name);
        }
        @Override
        public void csGetValue(PrintModel ipw, int k) {
            ipw.printf("return ps.getDate(%d).toLocalDate();%n", k);
        }
        @Override
        public void csGet(PrintModel ipw, int k, String setter) {
            ipw.printf("o.%s(ps.getDate(%d).toLocalDate());%n", setter, k);
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
        public void rsGet(PrintModel ipw, int k, String target) {
            ipw.printf("%s(J8Time.toLocalDate(rs.getDate(%d)));%n", target, k);
        }
        @Override
        public void rsGetValue(PrintModel ipw, int k) {
            ipw.printf("LocalDate o = J8Time.toLocalDate(rs.getDate(%d));%n", k);
        }
        @Override
        public void xPsPush(PrintModel ipw, String orig, String name) {
            ipw.printf("J8Time.setDate(ps, ++ki, EmSQL.get(%s, \"%s\", LocalDate.class));%n", orig, name);
        }
        @Override
        public void csGetValue(PrintModel ipw, int k) {
            ipw.printf("return J8Time.toLocalDate(ps.getDate(%d));%n", k);
        }
        @Override
        public void csGet(PrintModel ipw, int k, String setter) {
            ipw.printf("o.%s(J8Time.toLocalDate(ps.getDate(%d)));%n", setter, k);
        }
        @Override
        public void rsPull(PrintModel ipw, Integer k, String name) {
            ipw.printf("EmSQL.set(o, \"%s\", J8Time.toLocalDate(rs.getDate(%d)));%n", name, k);
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
        public void rsGet(PrintModel ipw, int k, String target) {
            ipw.printf("%s(rs.getTimestamp(%d).toLocalDateTime());%n", target, k);
        }
        @Override
        public void rsGetValue(PrintModel ipw, int k) {
            ipw.printf("LocalDateTime o = rs.getTimestamp(%d).toLocalDateTime();%n", k);
        }
        @Override
        public void rsPull(PrintModel ipw, Integer k, String name) {
            ipw.printf("EmSQL.set(o, \"%s\", rs.getTimestamp(%d).toLocalDateTime());%n", name, k);
        }
        @Override
        public void xPsPush(PrintModel ipw, String orig, String name) {
            ipw.printf("ps.setTimestamp(++ki, Timestamp.valueOf(EmSQL.get(%s, \"%s\", LocalDateTime.class)));%n", orig, name);
        }
        @Override
        public void csGetValue(PrintModel ipw, int k) {
            ipw.printf("return ps.getTimestamp(%d).toLocalDateTime();%n", k);
        }
        @Override
        public void csGet(PrintModel ipw, int k, String setter) {
            ipw.printf("o.%s(ps.getTimestamp(%d).toLocalDateTime());%n", setter, k);
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
        public void rsGet(PrintModel ipw, int k, String target) {
            ipw.printf("%s(J8Time.toLocalDateTime(rs.getTimestamp(%d)));%n", target, k);
        }
        @Override
        public void rsGetValue(PrintModel ipw, int k) {
            ipw.printf("LocalDateTime o = J8Time.toLocalDateTime(rs.getTimestamp(%d));%n", k);
        }
        @Override
        public void xPsPush(PrintModel ipw, String orig, String name) {
            ipw.printf("J8Time.setTimestamp(ps, ++ki, EmSQL.get(%s, \"%s\", LocalDateTime.class));%n", orig, name);
        }
        @Override
        public void csGetValue(PrintModel ipw, int k) {
            ipw.printf("return J8Time.toLocalDateTime(ps.getTimestamp(%d));%n", k);
        }
        @Override
        public void csGet(PrintModel ipw, int k, String setter) {
            ipw.printf("o.%s(J8Time.toLocalDateTime(ps.getTimestamp(%d)));%n", setter, k);
        }
        @Override
        public void rsPull(PrintModel ipw, Integer k, String name) {
            ipw.printf("EmSQL.set(o, \"%s\", J8Time.toLocalDateTime(rs.getTimestamp(%d)));%n", name, k);
        }
    },
    LocalTimeStd("Time", "TIME", "LocalTime") {
        @Override
        public Collection<String> requires() {
            return Set.of("java.time.LocalTime");
        }
        @Override
        public void psSet(PrintModel ipw, String source) {
            ipw.printf("ps.setTime(++ki, Time.valueOf(%s));%n",  source);
        }
        @Override
        public void rsGet(PrintModel ipw, int k, String target) {
            ipw.printf("%s(rs.getTime(%d).toLocalTime());%n", target, k);
        }
        @Override
        public void rsGetValue(PrintModel ipw, int k) {
            ipw.printf("LocalTime o = rs.getTime(%d).toLocalTime();%n", k);
        }
        @Override
        public void rsPull(PrintModel ipw, Integer k, String name) {
            ipw.printf("EmSQL.set(o, \"%s\", rs.getTime(%d).toLocalTime());%n", name, k);
        }
        @Override
        public void xPsPush(PrintModel ipw, String orig, String name) {
            ipw.printf("ps.setTime(++ki, Time.valueOf(EmSQL.get(%s, \"%s\", LocalTime.class)));%n", orig, name);
        }
        @Override
        public void csGetValue(PrintModel ipw, int k) {
            ipw.printf("return ps.getTime(%d).toLocalTime();%n", k);
        }
        @Override
        public void csGet(PrintModel ipw, int k, String setter) {
            ipw.printf("o.%s(ps.getTime(%d).toLocalTime());%n", setter, k);
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
        public void rsGet(PrintModel ipw, int k, String target) {
            ipw.printf("%s(J8Time.toLocalTime(rs.getTime(%d)));%n", target, k);
        }
        @Override
        public void rsGetValue(PrintModel ipw, int k) {
            ipw.printf("LocalTime o = J8Time.toLocalTime(rs.getTime(%d));%n", k);
        }
        @Override
        public void xPsPush(PrintModel ipw, String orig, String name) {
            ipw.printf("J8Time.setTime(ps, ++ki, EmSQL.get(%s, \"%s\", LocalTime.class));%n", orig, name);
        }
        @Override
        public void csGetValue(PrintModel ipw, int k) {
            ipw.printf("return J8Time.toLocalTime(ps.getTime(%d));%n", k);
        }
        @Override
        public void csGet(PrintModel ipw, int k, String setter) {
            ipw.printf("o.%s(J8Time.toLocalTime(ps.getTime(%d)));%n", setter, k);
        }
        @Override
        public void rsPull(PrintModel ipw, Integer k, String name) {
            ipw.printf("EmSQL.set(o, \"%s\", J8Time.toLocalTime(rs.getTime(%d)));%n", name, k);
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
    public  void rsGet(PrintModel ipw, int k, String target) {
        if (!isNullable() || isPlainClass) {
            ipw.printf("%s(rs.get%s(%d));%n", target, jdbc, k);
        } else {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("%s(EmSQL.get%s(rs,%d));%n", target, jdbc, k);
        }
    }
    public void rsGetValue(PrintModel ipw, int k) {
        if (!isNullable() || isPlainClass) {
            ipw.printf("%s o =  rs.get%s(%d);%n", primitive, jdbc, k);
        } else {
            ipw.printf("%1$s o = EmSQL.get%2$s(rs,%3$d);%n", wrapper, jdbc, k);
        }
    }
    public  void xPsPush(PrintModel ipw, String orig, String name) {
        if (!isNullable()) {
            ipw.printf("ps.set%s(++ki, EmSQL.get(%s, \"%s\", %s.class));%n", jdbc, orig, name, wrapper);
        } else {
            ipw.printf("EmSQL.set%s(ps, ++ki, EmSQL.get(%s, \"%s\", %s.class));%n", jdbc, orig, name, wrapper);
        }
    }
    public void rsPull(PrintModel ipw, Integer k, String name) {
        if (!isNullable() || isPlainClass) {
            ipw.printf("EmSQL.set(o, \"%s\", rs.get%s(%d));%n", name, jdbc, k);
        } else {
            ipw.printf("EmSQL.set(o, \"%s\", EmSQL.get%s(rs,%d));%n", name, jdbc, k);
        }
    }
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.%s);%n", k, sql);
    }
    public void csGetValue(PrintModel ipw, int k) {
        if (!isNullable() || isPlainClass) {
            ipw.printf("return ps.get%s(%d);%n", jdbc, k);
        } else {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("return EmSQL.get%s(ps,%d)%n", jdbc, k);
        }
    }
    public void csGet(PrintModel ipw, int k, String setter) {
        if (!isNullable() || isPlainClass) {
            ipw.printf("o.%s(ps.get%s(%d));%n", setter, jdbc, k);
        } else {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("o.%s(EmSQL.get%s(ps, %d));%n", setter, jdbc, k);
        }
    }
    @Override
    public boolean isNullable() {
        return name().endsWith("Nil");
    }
}
