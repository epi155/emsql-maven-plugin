package io.github.epi155.emsql.plugin.sql;

import io.github.epi155.emsql.plugin.ClassContext;
import io.github.epi155.emsql.plugin.IndentPrintWriter;
import lombok.Getter;

import java.util.Collection;
import java.util.Set;
public enum SqlEnum {
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
    VarCharNil("String", "VARCHAR"),
    CharStd("String", "CHAR"),
    CharNil("String", "CHAR"),
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
        public void psSet(IndentPrintWriter ipw, int k, String source) {
            ipw.printf("ps.setDate(%d, Date.valueOf(%s));%n", k, source);
        }
        @Override
        public void rsGet(IndentPrintWriter ipw, int k, String target, ClassContext cc) {
            ipw.printf("%s(rs.getDate(%d).toLocalDate());%n", target, k);
        }
        @Override
        public void setValue(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("ps.setDate(%d, Date.valueOf(%s));%n", k, name);
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
        public void psPush(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("ps.setDate(%d, Date.valueOf(EmSQL.get(i, \"%s\", LocalDate.class)));%n", k, name);
        }
        @Override
        public void psGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            ipw.printf("return ps.getDate(%d).toLocalDate();%n", k);
        }
        @Override
        public void psGet(IndentPrintWriter ipw, int k, String cName, ClassContext cc) {
            ipw.printf("o.set%s(ps.getDate(%d).toLocalDate());%n", cName, k);
        }
    },
    LocalDateNil("Date", "DATE", "LocalDate") {
        @Override
        public void psSet(IndentPrintWriter ipw, int k, String source) {
            ipw.printf("{ LocalDate it = %s; if (it==null) ps.setNull(%2$d, Types.DATE); else ps.setDate(%2$d, Date.valueOf(it)); }%n", source, k);
        }
        @Override
        public void rsGet(IndentPrintWriter ipw, int k, String target, ClassContext cc) {
            cc.add("io.github.epi155.emsql.runtime.EmSQL");
            ipw.printf("%s(EmSQL.toLocalDate(rs.getDate(%d)));%n", target, k);
        }
        @Override
        public void setValue(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("if (%s == null) {%n", name);
            ipw.more();
            ipw.printf("ps.setNull(%d, Types.DATE);%n", k);
            ipw.orElse();
            ipw.printf("ps.setDate(%d, Date.valueOf(%s));%n", k, name);
            ipw.ends();
        }
        @Override
        public void rsGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            cc.add("io.github.epi155.emsql.runtime.EmSQL");
            ipw.printf("LocalDate o = EmSQL.toLocalDate(rs.getDate(%d));%n", k);
        }
        @Override
        public void psPush(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("{ LocalDate it = EmSQL.get(i, \"%s\", LocalDate.class); if (it==null) ps.setNull(%2$d, Types.DATE); else ps.setDate(%2$d, Date.valueOf(it)); };%n", name, k);
        }
        @Override
        public void psGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            cc.add("io.github.epi155.emsql.runtime.EmSQL");
            ipw.printf("return EmSQL.toLocalDate(ps.getDate(%d));%n", k);
        }
        @Override
        public void psGet(IndentPrintWriter ipw, int k, String cName, ClassContext cc) {
            cc.add("io.github.epi155.emsql.runtime.EmSQL");
            ipw.printf("o.set%s(EmSQL.toLocalDate(ps.getDate(%d)));%n", cName, k);
        }
        @Override
        public void rsPull(IndentPrintWriter ipw, Integer k, String name) {
            ipw.printf("EmSQL.set(o, \"%s\", EmSQL.toLocalDate(rs.getDate(%d)));%n", name, k);
        }
    },
    LocalDateTimeStd("Timestamp", "TIMESTAMP", "LocalDateTime") {
        @Override
        public void psSet(IndentPrintWriter ipw, int k, String source) {
            ipw.printf("ps.setTimestamp(%d, Timestamp.valueOf(%s));%n", k, source);
        }
        @Override
        public void rsGet(IndentPrintWriter ipw, int k, String target, ClassContext cc) {
            ipw.printf("%s(rs.getTimestamp(%d).toLocalDateTime());%n", target, k);
        }
        @Override
        public void setValue(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("ps.setTimestamp(%d, Timestamp.valueOf(%s));%n", k, name);
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
        public void psPush(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("ps.setTimestamp(%d, Timestamp.valueOf(EmSQL.get(i, \"%s\", LocalDateTime.class)));%n", k, name);
        }
        @Override
        public void psGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            ipw.printf("return ps.getTimestamp(%d).toLocalDateTime();%n", k);
        }
        @Override
        public void psGet(IndentPrintWriter ipw, int k, String cName, ClassContext cc) {
            ipw.printf("o.set%s(ps.getTimestamp(%d).toLocalDateTime());%n", cName, k);
        }
    },
    LocalDateTimeNil("Timestamp", "TIMESTAMP", "LocalDateTime") {
        @Override
        public void psSet(IndentPrintWriter ipw, int k, String so) {
            ipw.printf("{ LocalDateTime it = %s; if (it==null) ps.setNull(%2$d, Types.TIMESTAMP); else ps.setTimestamp(%2$d, Timestamp.valueOf(it)); }%n", so, k);
        }
        @Override
        public void rsGet(IndentPrintWriter ipw, int k, String target, ClassContext cc) {
            cc.add("io.github.epi155.emsql.runtime.EmSQL");
            ipw.printf("%s(EmSQL.toLocalDateTime(rs.getTimestamp(%d)));%n", target, k);
        }
        @Override
        public void setValue(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("if (%s == null) {%n", name);
            ipw.more();
            ipw.printf("ps.setNull(%d, Types.TIMESTAMP);%n", k);
            ipw.orElse();
            ipw.printf("ps.setTimestamp(%d, Timestamp.valueOf(%s));%n", k, name);
            ipw.ends();
        }
        @Override
        public void rsGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            cc.add("io.github.epi155.emsql.runtime.EmSQL");
            ipw.printf("LocalDateTime o = EmSQL.toLocalDateTime(rs.getTimestamp(%d));%n", k);
        }
        @Override
        public void psPush(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("{ LocalDateTime it = EmSQL.get(i, \"%s\", LocalDateTime.class); if (it==null) ps.setNull(%2$d, Types.TIMESTAMP); else ps.setTimestamp(%2$d, Timestamp.valueOf(it)); };%n", name, k);
        }
        @Override
        public void psGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            cc.add("io.github.epi155.emsql.runtime.EmSQL");
            ipw.printf("return EmSQL.toLocalDateTime(ps.getTimestamp(%d));%n", k);
        }
        @Override
        public void psGet(IndentPrintWriter ipw, int k, String cName, ClassContext cc) {
            cc.add("io.github.epi155.emsql.runtime.EmSQL");
            ipw.printf("o.set%s(EmSQL.toLocalDateTime(ps.getTimestamp(%d)));%n", cName, k);
        }
        @Override
        public void rsPull(IndentPrintWriter ipw, Integer k, String name) {
            ipw.printf("EmSQL.set(o, \"%s\", EmSQL.toLocalDateTime(rs.getTimestamp(%d)));%n", name, k);
        }
    },
    LocalTimeStd("Time", "TIME", "LocalTime") {
        @Override
        public void psSet(IndentPrintWriter ipw, int k, String source) {
            ipw.printf("ps.setTime(%d, Time.valueOf(%s));%n", k, source);
        }
        @Override
        public void rsGet(IndentPrintWriter ipw, int k, String target, ClassContext cc) {
            ipw.printf("%s(rs.getTime(%d).toLocalTime());%n", target, k);
        }
        @Override
        public void setValue(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("ps.setTime(%d, Time.valueOf(%s));%n", k, name);
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
        public void psPush(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("ps.setTime(%d, Time.valueOf(EmSQL.get(i, \"%s\", LocalTime.class)));%n", k, name);
        }
        @Override
        public void psGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            ipw.printf("return ps.getTime(%d).toLocalTime();%n", k);
        }
        @Override
        public void psGet(IndentPrintWriter ipw, int k, String cName, ClassContext cc) {
            ipw.printf("o.set%s(ps.getTime(%d).toLocalTime());%n", cName, k);
        }
    },
    LocalTimeNil("Time", "TIME", "LocalTime") {
        @Override
        public void psSet(IndentPrintWriter ipw, int k, String so) {
            ipw.printf("{ LocalTime it = %s; if (it==null) ps.setNull(%2$d, Types.TIME); else ps.setTime(%2$d, Time.valueOf(it)); }%n", so, k);
        }
        @Override
        public void rsGet(IndentPrintWriter ipw, int k, String target, ClassContext cc) {
            cc.add("io.github.epi155.emsql.runtime.EmSQL");
            ipw.printf("%s(EmSQL.toLocalTime(rs.getTime(%d)));%n", target, k);
        }
        @Override
        public void setValue(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("if (%s == null) {%n", name);
            ipw.more();
            ipw.printf("ps.setNull(%d, Types.TIME);%n", k);
            ipw.orElse();
            ipw.printf("ps.setTime(%d, Time.valueOf(%s));%n", k, name);
            ipw.ends();
        }
        @Override
        public void rsGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            cc.add("io.github.epi155.emsql.runtime.EmSQL");
            ipw.printf("LocalTime o = EmSQL.toLocalTime(rs.getTime(%d));%n", k);
        }
        @Override
        public void psPush(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("{ LocalTime it = EmSQL.get(i, \"%s\", LocalTime.class); if (it==null) ps.setNull(%2$d, Types.TIME); else ps.setTime(%2$d, Time.valueOf(it)); };%n", name, k);
        }
        @Override
        public void psGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            cc.add("io.github.epi155.emsql.runtime.EmSQL");
            ipw.printf("return EmSQL.toLocalTime(ps.getTime(%d));%n", k);
        }
        @Override
        public void psGet(IndentPrintWriter ipw, int k, String cName, ClassContext cc) {
            cc.add("io.github.epi155.emsql.runtime.EmSQL");
            ipw.printf("o.set%s(EmSQL.toLocalTime(ps.getTime(%d)));%n", cName, k);
        }
        @Override
        public void rsPull(IndentPrintWriter ipw, Integer k, String name) {
            ipw.printf("EmSQL.set(o, \"%s\", EmSQL.toLocalTime(rs.getTime(%d)));%n", name, k);
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
    public void psSet(IndentPrintWriter ipw, int k, String source) {
        if (name().endsWith("Std")) {
            ipw.printf("ps.set%s(%d, %s);%n", jdbc, k, source);
        } else {
            ipw.printf("{ %1$s it = %2$s; if (it==null) ps.setNull(%3$d, Types.%4$s); else ps.set%5$s(%3$d, it); }%n", wrapper, source, k, sql, jdbc);
        }
    }
    public  void rsGet(IndentPrintWriter ipw, int k, String target, ClassContext cc) {
        if (name().endsWith("Std") || isPlainClass) {
            ipw.printf("%s(rs.get%s(%d));%n", target, jdbc, k);
        } else {
            ipw.printf("{ %s it=rs.get%s(%d); %s(rs.wasNull() ? null : it); }%n", wrapper, jdbc, k, target);
//
//            cc.add("io.github.epi155.emsql.runtime.EmSQL");
//            ipw.printf("%s(EmSQL.box(rs.get%s(%d), rs.wasNull()));%n", target, jdbc, k);
        }
    }
    public void setValue(IndentPrintWriter ipw, int k, String name) {
        if (name().endsWith("Std")) {
            ipw.printf("ps.set%s(%d, %s);%n", jdbc, k, name);
        } else {
            ipw.printf("if (%1$s == null) ps.setNull(%2$d, Types.%3$s); else ps.set%4$s(%2$d, %1$s);%n", name, k, sql, jdbc);
        }
    }
    public void rsGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
        if (name().endsWith("Std") || isPlainClass) {
            ipw.printf("%s o =  rs.get%s(%d);%n", primitive, jdbc, k);
        } else {
            ipw.printf("%1$s o; { %1$s it=rs.get%2$s(%3$d); o=rs.wasNull() ? null : it; }%n", wrapper, jdbc, k);
//            cc.add("io.github.epi155.emsql.runtime.EmSQL");
//            ipw.printf("%s o = EmSQL.box(rs.get%s(%d), rs.wasNull());%n", wrapper, jdbc, k);
        }
    }
    public  void psPush(IndentPrintWriter ipw, int k, String name) {
        if (name().endsWith("Std")) {
            ipw.printf("ps.set%s(%d, EmSQL.get(i, \"%s\", %s.class));%n", jdbc, k, name, wrapper);
        } else {
            ipw.printf("{ %1$s it = EmSQL.get(i, \"%2$s\", %1$s.class); if (it==null) ps.setNull(%3$d, Types.%4$s); else ps.set%5$s(%3$d, it); };%n", wrapper, name, k ,sql, jdbc);
        }
    }
    public void rsPull(IndentPrintWriter ipw, Integer k, String name) {
        if (name().endsWith("Std") || isPlainClass) {
            ipw.printf("EmSQL.set(o, \"%s\", rs.get%s(%d));%n", name, jdbc, k);
        } else {
            ipw.printf("EmSQL.set(o, \"%s\", EmSQL.box(rs.get%s(%d), rs.wasNull()));%n", name, jdbc, k);
        }
    }
    public final void registerOut(IndentPrintWriter ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.%s);%n", k, sql);
    }
    public void psGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
        if (name().endsWith("Std") || isPlainClass) {
            ipw.printf("return ps.get%s(%d);%n", jdbc, k);
        } else {
            ipw.printf("{ %s it=ps.get%s(%d); return ps.wasNull() ? null : it; }%n", wrapper, jdbc, k);
        }
    }
    public void psGet(IndentPrintWriter ipw, int k, String cName, ClassContext cc) {
        if (name().endsWith("Std") || isPlainClass) {
            ipw.printf("o.set%s(ps.get%s(%d));%n", cName, jdbc, k);
        } else {
            ipw.printf("{ %s it=ps.get%s(%d); o.set%s(ps.wasNull() ? null : it); }%n", wrapper, jdbc, k, cName);
        }
    }
    public Collection<String> requires() {
        return Set.of();
    }
}
