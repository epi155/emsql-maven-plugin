package io.github.epi155.emsql.plugin.sql;

import io.github.epi155.emsql.plugin.ClassContext;
import io.github.epi155.emsql.plugin.IndentPrintWriter;
import lombok.Getter;

import java.util.Collection;
import java.util.Set;
public enum SqlEnum implements SqlKind {
    BooleanStd("Boolean", "BOOLEAN", "boolean", "Boolean"),
    BooleanNil("Boolean", "BOOLEAN", "Boolean"),
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
        public void psSet(IndentPrintWriter ipw, String source, ClassContext cc) {
            cc.add(ClassContext.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setVarchar(ps, ++ki, %s);%n", source);
        }
        @Override
        public  void psPush(IndentPrintWriter ipw, String name, ClassContext cc) {
            ipw.printf("EmSQL.setVarchar(ps, ++ki, EmSQL.get(i, \"%s\", String.class));%n", name);
        }
    },
    CharStd("String", "CHAR"),
    CharNil("String", "CHAR") {
        @Override
        public void psSet(IndentPrintWriter ipw, String source, ClassContext cc) {
            cc.add(ClassContext.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setChar(ps, ++ki, %s);%n", source);
        }
        @Override
        public  void psPush(IndentPrintWriter ipw, String name, ClassContext cc) {
            ipw.printf("EmSQL.setChar(ps, ++ki, EmSQL.get(i, \"%s\", String.class));%n", name);
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
    LocalDateStd("Date", "DATE", "LocalDate") {
        @Override
        public void psSet(IndentPrintWriter ipw, String source, ClassContext cc) {
            ipw.printf("ps.setDate(++ki, Date.valueOf(%s));%n", source);
        }
        @Override
        public void rsGet(IndentPrintWriter ipw, int k, String target, ClassContext cc) {
            ipw.printf("%s(rs.getDate(%d).toLocalDate());%n", target, k);
        }
        @Override
        public void rsGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            ipw.printf("LocalDate o = rs.getDate(%d).toLocalDate();%n", k);
        }
        @Override
        public Collection<String> requires() {
            return Set.of("java.time.LocalDate");
        }
        @Override
        public void rsPull(IndentPrintWriter ipw, Integer k, String name) {
            ipw.printf("EmSQL.set(o, \"%s\", rs.getDate(%d).toLocalDate());%n", name, k);
        }
        @Override
        public void psPush(IndentPrintWriter ipw, String name, ClassContext cc) {
            ipw.printf("ps.setDate(++ki, Date.valueOf(EmSQL.get(i, \"%s\", LocalDate.class)));%n", name);
        }
        @Override
        public void csGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            ipw.printf("return ps.getDate(%d).toLocalDate();%n", k);
        }
        @Override
        public void csGet(IndentPrintWriter ipw, int k, String setter, ClassContext cc) {
            ipw.printf("o.%s(ps.getDate(%d).toLocalDate());%n", setter, k);
        }
    },
    LocalDateNil("Date", "DATE", "LocalDate") {
        @Override
        public void psSet(IndentPrintWriter ipw, String source, ClassContext cc) {
            cc.add(ClassContext.RUNTIME_J8TIME);
            ipw.printf("J8Time.setDate(ps, ++ki, %s);%n", source);
        }
        @Override
        public void rsGet(IndentPrintWriter ipw, int k, String target, ClassContext cc) {
            cc.add(ClassContext.RUNTIME_J8TIME);
            ipw.printf("%s(J8Time.toLocalDate(rs.getDate(%d)));%n", target, k);
        }
        @Override
        public void rsGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            cc.add(ClassContext.RUNTIME_J8TIME);
            ipw.printf("LocalDate o = J8Time.toLocalDate(rs.getDate(%d));%n", k);
        }
        @Override
        public void psPush(IndentPrintWriter ipw, String name, ClassContext cc) {
            cc.add(ClassContext.RUNTIME_J8TIME);
            ipw.printf("J8Time.setDate(ps, ++ki, EmSQL.get(i, \"%s\", LocalDate.class));%n", name);
        }
        @Override
        public void csGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            cc.add(ClassContext.RUNTIME_J8TIME);
            ipw.printf("return J8Time.toLocalDate(ps.getDate(%d));%n", k);
        }
        @Override
        public void csGet(IndentPrintWriter ipw, int k, String setter, ClassContext cc) {
            cc.add(ClassContext.RUNTIME_J8TIME);
            ipw.printf("o.%s(J8Time.toLocalDate(ps.getDate(%d)));%n", setter, k);
        }
        @Override
        public void rsPull(IndentPrintWriter ipw, Integer k, String name) {
            ipw.printf("EmSQL.set(o, \"%s\", J8Time.toLocalDate(rs.getDate(%d)));%n", name, k);
        }
    },
    LocalDateTimeStd("Timestamp", "TIMESTAMP", "LocalDateTime") {
        @Override
        public void psSet(IndentPrintWriter ipw, String source, ClassContext cc) {
            ipw.printf("ps.setTimestamp(++ki, Timestamp.valueOf(%s));%n", source);
        }
        @Override
        public void rsGet(IndentPrintWriter ipw, int k, String target, ClassContext cc) {
            ipw.printf("%s(rs.getTimestamp(%d).toLocalDateTime());%n", target, k);
        }
        @Override
        public void rsGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            ipw.printf("LocalDateTime o = rs.getTimestamp(%d).toLocalDateTime();%n", k);
        }
        @Override
        public Collection<String> requires() {
            return Set.of("java.time.LocalDateTime");
        }
        @Override
        public void rsPull(IndentPrintWriter ipw, Integer k, String name) {
            ipw.printf("EmSQL.set(o, \"%s\", rs.getTimestamp(%d).toLocalDateTime());%n", name, k);
        }
        @Override
        public void psPush(IndentPrintWriter ipw, String name, ClassContext cc) {
            ipw.printf("ps.setTimestamp(++ki, Timestamp.valueOf(EmSQL.get(i, \"%s\", LocalDateTime.class)));%n", name);
        }
        @Override
        public void csGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            ipw.printf("return ps.getTimestamp(%d).toLocalDateTime();%n", k);
        }
        @Override
        public void csGet(IndentPrintWriter ipw, int k, String setter, ClassContext cc) {
            ipw.printf("o.%s(ps.getTimestamp(%d).toLocalDateTime());%n", setter, k);
        }
    },
    LocalDateTimeNil("Timestamp", "TIMESTAMP", "LocalDateTime") {
        @Override
        public void psSet(IndentPrintWriter ipw, String so, ClassContext cc) {
            cc.add(ClassContext.RUNTIME_J8TIME);
            ipw.printf("J8Time.setTimestamp(ps, ++ki, %s);%n", so);
        }
        @Override
        public void rsGet(IndentPrintWriter ipw, int k, String target, ClassContext cc) {
            cc.add(ClassContext.RUNTIME_J8TIME);
            ipw.printf("%s(J8Time.toLocalDateTime(rs.getTimestamp(%d)));%n", target, k);
        }
        @Override
        public void rsGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            cc.add(ClassContext.RUNTIME_J8TIME);
            ipw.printf("LocalDateTime o = J8Time.toLocalDateTime(rs.getTimestamp(%d));%n", k);
        }
        @Override
        public void psPush(IndentPrintWriter ipw, String name, ClassContext cc) {
            cc.add(ClassContext.RUNTIME_J8TIME);
            ipw.printf("J8Time.setTimestamp(ps, ++ki, EmSQL.get(i, \"%s\", LocalDateTime.class));%n", name);
        }
        @Override
        public void csGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            cc.add(ClassContext.RUNTIME_J8TIME);
            ipw.printf("return J8Time.toLocalDateTime(ps.getTimestamp(%d));%n", k);
        }
        @Override
        public void csGet(IndentPrintWriter ipw, int k, String setter, ClassContext cc) {
            cc.add(ClassContext.RUNTIME_J8TIME);
            ipw.printf("o.%s(J8Time.toLocalDateTime(ps.getTimestamp(%d)));%n", setter, k);
        }
        @Override
        public void rsPull(IndentPrintWriter ipw, Integer k, String name) {
            ipw.printf("EmSQL.set(o, \"%s\", J8Time.toLocalDateTime(rs.getTimestamp(%d)));%n", name, k);
        }
    },
    LocalTimeStd("Time", "TIME", "LocalTime") {
        @Override
        public void psSet(IndentPrintWriter ipw, String source, ClassContext cc) {
            ipw.printf("ps.setTime(++ki, Time.valueOf(%s));%n",  source);
        }
        @Override
        public void rsGet(IndentPrintWriter ipw, int k, String target, ClassContext cc) {
            ipw.printf("%s(rs.getTime(%d).toLocalTime());%n", target, k);
        }
        @Override
        public void rsGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            ipw.printf("LocalTime o = rs.getTime(%d).toLocalTime();%n", k);
        }
        @Override
        public Collection<String> requires() {
            return Set.of("java.time.LocalTime");
        }
        @Override
        public void rsPull(IndentPrintWriter ipw, Integer k, String name) {
            ipw.printf("EmSQL.set(o, \"%s\", rs.getTime(%d).toLocalTime());%n", name, k);
        }
        @Override
        public void psPush(IndentPrintWriter ipw, String name, ClassContext cc) {
            ipw.printf("ps.setTime(++ki, Time.valueOf(EmSQL.get(i, \"%s\", LocalTime.class)));%n", name);
        }
        @Override
        public void csGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            ipw.printf("return ps.getTime(%d).toLocalTime();%n", k);
        }
        @Override
        public void csGet(IndentPrintWriter ipw, int k, String setter, ClassContext cc) {
            ipw.printf("o.%s(ps.getTime(%d).toLocalTime());%n", setter, k);
        }
    },
    LocalTimeNil("Time", "TIME", "LocalTime") {
        @Override
        public void psSet(IndentPrintWriter ipw, String so, ClassContext cc) {
            cc.add(ClassContext.RUNTIME_J8TIME);
            ipw.printf("J8Time.setTime(ps, ++ki, %s);%n", so);
        }
        @Override
        public void rsGet(IndentPrintWriter ipw, int k, String target, ClassContext cc) {
            cc.add(ClassContext.RUNTIME_J8TIME);
            ipw.printf("%s(J8Time.toLocalTime(rs.getTime(%d)));%n", target, k);
        }
        @Override
        public void rsGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            cc.add(ClassContext.RUNTIME_J8TIME);
            ipw.printf("LocalTime o = J8Time.toLocalTime(rs.getTime(%d));%n", k);
        }
        @Override
        public void psPush(IndentPrintWriter ipw, String name, ClassContext cc) {
            cc.add(ClassContext.RUNTIME_J8TIME);
            ipw.printf("J8Time.setTime(ps, ++ki, EmSQL.get(i, \"%s\", LocalTime.class));%n", name);
        }
        @Override
        public void csGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            cc.add(ClassContext.RUNTIME_J8TIME);
            ipw.printf("return J8Time.toLocalTime(ps.getTime(%d));%n", k);
        }
        @Override
        public void csGet(IndentPrintWriter ipw, int k, String setter, ClassContext cc) {
            cc.add(ClassContext.RUNTIME_J8TIME);
            ipw.printf("o.%s(J8Time.toLocalTime(ps.getTime(%d)));%n", setter, k);
        }
        @Override
        public void rsPull(IndentPrintWriter ipw, Integer k, String name) {
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
    public void psSet(IndentPrintWriter ipw, String source, ClassContext cc) {
        if (!isNullable()) {
            ipw.printf("ps.set%s(++ki, %s);%n", jdbc, source);
        } else {
            cc.add(ClassContext.RUNTIME_EMSQL);
            ipw.printf("EmSQL.set%s(ps, ++ki, %s);%n", jdbc, source);
        }
    }
    public  void rsGet(IndentPrintWriter ipw, int k, String target, ClassContext cc) {
        if (!isNullable() || isPlainClass) {
            ipw.printf("%s(rs.get%s(%d));%n", target, jdbc, k);
        } else {
            cc.add(ClassContext.RUNTIME_EMSQL);
            ipw.printf("%s(EmSQL.get%s(rs,%d));%n", target, jdbc, k);
        }
    }
    public void rsGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
        if (!isNullable() || isPlainClass) {
            ipw.printf("%s o =  rs.get%s(%d);%n", primitive, jdbc, k);
        } else {
            ipw.printf("%1$s o = EmSQL.get%2$s(rs,%3$d);%n", wrapper, jdbc, k);
        }
    }
    public  void psPush(IndentPrintWriter ipw, String name, ClassContext cc) {
        if (!isNullable()) {
            ipw.printf("ps.set%s(++ki, EmSQL.get(i, \"%s\", %s.class));%n", jdbc, name, wrapper);
        } else {
            ipw.printf("EmSQL.set%s(ps, ++ki, EmSQL.get(i, \"%s\", %s.class));%n", jdbc, name, wrapper);
        }
    }
    public void rsPull(IndentPrintWriter ipw, Integer k, String name) {
        if (!isNullable() || isPlainClass) {
            ipw.printf("EmSQL.set(o, \"%s\", rs.get%s(%d));%n", name, jdbc, k);
        } else {
            ipw.printf("EmSQL.set(o, \"%s\", EmSQL.get%s(rs,%d));%n", name, jdbc, k);
        }
    }
    public void registerOut(IndentPrintWriter ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.%s);%n", k, sql);
    }
    public void csGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
        if (!isNullable() || isPlainClass) {
            ipw.printf("return ps.get%s(%d);%n", jdbc, k);
        } else {
            cc.add(ClassContext.RUNTIME_EMSQL);
            ipw.printf("return EmSQL.get%s(ps,%d)%n", jdbc, k);
        }
    }
    public void csGet(IndentPrintWriter ipw, int k, String setter, ClassContext cc) {
        if (!isNullable() || isPlainClass) {
            ipw.printf("o.%s(ps.get%s(%d));%n", setter, jdbc, k);
        } else {
            cc.add(ClassContext.RUNTIME_EMSQL);
            ipw.printf("o.%s(EmSQL.get%s(ps, %d));%n", setter, jdbc, k);
        }
    }
    @Override
    public boolean isNullable() {
        return name().endsWith("Nil");
    }
}
