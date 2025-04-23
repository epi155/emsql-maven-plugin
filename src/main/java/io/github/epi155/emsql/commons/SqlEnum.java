package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import lombok.Getter;

import java.util.Collection;
import java.util.Set;

import static io.github.epi155.emsql.commons.Contexts.cc;

public enum SqlEnum implements SqlDataType {
    BooleanStd("Boolean", "BOOLEAN", "boolean", "Boolean") {
        @Override
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
    FloatStd("Float", "REAL", "float", "Float"),
    FloatNil("Float", "REAL", "Float"),
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
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setVarchar(ps, ++ki, EmSQL.get(%s, \"%s\", String.class));%n", orig, name);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
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
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setChar(ps, ++ki, EmSQL.get(%s, \"%s\", String.class));%n", orig, name);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setChar(ps, %d, EmSQL.get(%s, \"%s\", String.class));%n", k, orig, name);
        }
    },
    DateStd("Date", "DATE"),
    DateNil("Date", "DATE"),
    TimestampStd("Timestamp", "TIMESTAMP"),
    TimestampNil("Timestamp", "TIMESTAMP"),
    TimeStd("Time", "TIME"),
    TimeNil("Time", "TIME"),
    NumberStd("BigDecimal", "NUMERIC", "BigInteger") {
        @Override
        public Collection<String> requires() {
            return Set.of("java.math.BigInteger");
        }
        @Override
        public void psSet(PrintModel ipw, String source) {
            cc.add("java.math.BigDecimal");
            ipw.printf("ps.setBigDecimal(++ki, new BigDecimal(%s));%n", source);
        }
        @Override
        public void psSet(PrintModel ipw, String source, int k) {
            cc.add("java.math.BigDecimal");
            ipw.printf("ps.setBigDecimal(%d, new BigDecimal(%s));%n", k, source);
        }
        @Override
        public void rsGetValue(PrintModel ipw, int k) {
            ipw.putf("rs.getBigDecimal(%d).toBigInteger()", k);
        }
        @Override
        public void xPsPush(PrintModel ipw, String orig, String name) {
            cc.add("java.math.BigDecimal");
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("ps.setBigDecimal(++ki, new BigDecimal(EmSQL.get(%s, \"%s\", BigInteger.class)));%n", orig, name);
        }
        @Override
        public void xPsPush(PrintModel ipw, String orig, String name, int k) {
            cc.add("java.math.BigDecimal");
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("ps.setBigDecimal(%d, new BigDecimal(EmSQL.get(%s, \"%s\", BigInteger.class)));%n", k, orig, name);
        }
        @Override
        public void csGetValue(PrintModel ipw, int k) {
            ipw.putf("ps.setBigDecimal(%d).toBigInteger()", k);
        }
    },
    NumberNil("BigDecimal", "NUMERIC", "BigInteger") {
        @Override
        public Collection<String> requires() {
            return NumberStd.requires();
        }
        @Override
        public void psSet(PrintModel ipw, String source) {
            cc.add("java.math.BigDecimal");
            ipw.printf("ps.setBigDecimal(++ki, %1$s==null ? null : new BigDecimal(%1$s));%n", source);
        }
        @Override
        public void psSet(PrintModel ipw, String source, int k) {
            cc.add("java.math.BigDecimal");
            ipw.printf("ps.setBigDecimal(%d, %2$s==null ? null : new BigDecimal(%2$s));%n", k, source);
        }
        @Override
        public void rsGetValue(PrintModel ipw, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.putf("EmSQL.toBigInteger(rs.getBigDecimal(%d))", k);
        }
        @Override
        public void xPsPush(PrintModel ipw, String orig, String name) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setBigInteger(ps, ++ki, EmSQL.get(%s, \"%s\", BigInteger.class));%n", orig, name);
        }
        @Override
        public void xPsPush(PrintModel ipw, String orig, String name, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setBigInteger(ps, %d, EmSQL.get(%s, \"%s\", BigInteger.class));%n", k, orig, name);
        }
        @Override
        public void csGetValue(PrintModel ipw, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.putf("EmSQL.toBigInteger(ps.getBigDecimal(%d))", k);
        }
    },
    /* NUMERIC 128-bit, DECIMAL 64-bit*/
    DecimalStd("BigDecimal", "NUMERIC") {
        @Override
        public Collection<String> requires() {
            return Set.of("java.math.BigDecimal");
        }
    },
    DecimalNil("BigDecimal", "NUMERIC") {
        @Override
        public Collection<String> requires() {
            return DecimalStd.requires();
        }
    },
    BinaryStd("Bytes", "BINARY", "byte[]"),
    BinaryNil("Bytes", "BINARY", "byte[]") {
        @Override
        public void rsGetValue(PrintModel ipw, int k) { BinaryStd.rsGetValue(ipw, k); }
        @Override
        public void csGetValue(PrintModel ipw, int k) { BinaryStd.csGetValue(ipw, k);}
    },
    VarBinaryStd("Bytes", "VARBINARY", "byte[]"),
    VarBinaryNil("Bytes", "VARBINARY", "byte[]") {
        @Override
        public void rsGetValue(PrintModel ipw, int k) { VarBinaryStd.rsGetValue(ipw, k); }
        @Override
        public void csGetValue(PrintModel ipw, int k) { VarBinaryStd.csGetValue(ipw, k); }
    },
    NumBoolStd("Boolean", "TINYINT", "boolean", "Boolean") {
        @Override
        public String getterPrefix() { return "is"; }
        @Override
        public void psSet(PrintModel ipw, String source) {
            ipw.printf("ps.setByte(++ki, (byte) (%s ? 1 : 0));%n", source);
        }
        @Override
        public void psSet(PrintModel ipw, String source, int k) {
            ipw.printf("ps.setByte(%d, (byte) (%s ? 1 : 0));%n", k, source);
        }
        @Override
        public void rsGetValue(PrintModel ipw, int k) {
            ipw.putf("rs.getByte(%d)==1", k);
        }
        @Override
        public void xPsPush(PrintModel ipw, String orig, String name) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("ps.setByte(++ki, (byte) (EmSQL.get(%s, \"%s\", Boolean.class) ? 1 : 0));%n", orig, name);
        }
        @Override
        public void xPsPush(PrintModel ipw, String orig, String name, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
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

        @Override
        public void psSet(PrintModel ipw, String source) {
            ipw.printf("EmSQL.setNumBool(ps, ++ki, %s);%n", source);
        }

        @Override
        public void psSet(PrintModel ipw, String source, int k) {
            ipw.printf("EmSQL.setNumBool(ps, %d, %s);%n", k, source);
        }

        @Override
        public void rsGetValue(PrintModel ipw, int k) {
            ipw.putf("EmSQL.getNumBool(rs,%d)", k);
        }

        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name) {
            ipw.printf("EmSQL.setNumBool(ps, ++ki, EmSQL.get(%s, \"%s\", Boolean.class));%n", orig, name);
        }

        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name, int k) {
            ipw.printf("EmSQL.setNumBool(ps, %d, EmSQL.get(%s, \"%s\", Boolean.class));%n", k, orig, name);
        }
        @Override
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
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("ps.setDate(++ki, Date.valueOf(EmSQL.get(%s, \"%s\", LocalDate.class)));%n", orig, name);
        }
        @Override
        public void xPsPush(PrintModel ipw, String orig, String name, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
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
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("J8Time.setDate(ps, ++ki, EmSQL.get(%s, \"%s\", LocalDate.class));%n", orig, name);
        }
        @Override
        public void xPsPush(PrintModel ipw, String orig, String name, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
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
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("ps.setTimestamp(++ki, Timestamp.valueOf(EmSQL.get(%s, \"%s\", LocalDateTime.class)));%n", orig, name);
        }
        @Override
        public void xPsPush(PrintModel ipw, String orig, String name, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
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
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("J8Time.setTimestamp(ps, ++ki, EmSQL.get(%s, \"%s\", LocalDateTime.class));%n", orig, name);
        }
        @Override
        public void xPsPush(PrintModel ipw, String orig, String name, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
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
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("ps.setTime(++ki, Time.valueOf(EmSQL.get(%s, \"%s\", LocalTime.class)));%n", orig, name);
        }
        @Override
        public void xPsPush(PrintModel ipw, String orig, String name, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
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
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("J8Time.setTime(ps, ++ki, EmSQL.get(%s, \"%s\", LocalTime.class));%n", orig, name);
        }
        @Override
        public void xPsPush(PrintModel ipw, String orig, String name, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("J8Time.setTime(ps, %d, EmSQL.get(%s, \"%s\", LocalTime.class));%n", k, orig, name);
        }
        @Override
        public void csGetValue(PrintModel ipw, int k) {
            ipw.putf("J8Time.toLocalTime(ps.getTime(%d))", k);
        }
    },
    TimestampZStd("Object", "TIMESTAMP_WITH_TIMEZONE", "OffsetDateTime") {
        @Override
        public Collection<String> requires() {
            return Set.of("java.time.OffsetDateTime");
        }
        @Override
        public void rsGetValue(PrintModel ipw, int k) {
            ipw.putf("rs.getObject(%d, OffsetDateTime.class)", k);
        }
        @Override
        public void csGetValue(PrintModel ipw, int k) {
            ipw.putf("ps.getObject(%d, OffsetDateTime.class)", k);
        }
    },
    TimestampZNil("Object", "TIMESTAMP_WITH_TIMEZONE", "OffsetDateTime") {
        @Override
        public Collection<String> requires() {
            return TimestampZStd.requires();
        }
        @Override
        public void rsGetValue(PrintModel ipw, int k) {
            TimestampZStd.rsGetValue(ipw, k);
        }
        @Override
        public void psSet(PrintModel ipw, String source) {
            cc.add(ClassContextImpl.RUNTIME_J8TIME);
            ipw.printf("J8Time.setOffsetDateTime(ps, ++ki, %s);%n", source);
        }
        @Override
        public void psSet(PrintModel ipw, String source, int k) {
            cc.add(ClassContextImpl.RUNTIME_J8TIME);
            ipw.printf("J8Time.setOffsetDateTime(ps, %d, %s);%n", k, source);
        }
        @Override
        public void xPsPush(PrintModel ipw, String orig, String name) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            cc.add(ClassContextImpl.RUNTIME_J8TIME);
            ipw.printf("J8Time.setOffsetDateTime(ps, ++ki, EmSQL.get(%s, \"%s\", OffsetDateTime.class));%n", orig, name);
        }
        @Override
        public void xPsPush(PrintModel ipw, String orig, String name, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            cc.add(ClassContextImpl.RUNTIME_J8TIME);
            ipw.printf("J8Time.setOffsetDateTime(ps, %d, EmSQL.get(%s, \"%s\", OffsetDateTime.class));%n", k, orig, name);
        }
        @Override
        public void csGetValue(PrintModel ipw, int k) {
            TimestampZStd.csGetValue(ipw, k);
        }
    },
    TimeZStd("Object", "TIME_WITH_TIMEZONE", "OffsetTime") {
        @Override
        public Collection<String> requires() {
            return Set.of("java.time.OffsetTime");
        }
        @Override
        public void rsGetValue(PrintModel ipw, int k) {
            ipw.putf("rs.getObject(%d, OffsetTime.class)", k);
        }
        @Override
        public void csGetValue(PrintModel ipw, int k) {
            ipw.putf("ps.getObject(%d, OffsetTime.class)", k);
        }
    },
    TimeZNil("Object", "TIME_WITH_TIMEZONE", "OffsetTime") {
        @Override
        public Collection<String> requires() {
            return TimeZStd.requires();
        }
        @Override
        public void rsGetValue(PrintModel ipw, int k) {
            TimeZStd.rsGetValue(ipw, k);
        }
        @Override
        public void psSet(PrintModel ipw, String source) {
            cc.add(ClassContextImpl.RUNTIME_J8TIME);
            ipw.printf("J8Time.setOffsetTime(ps, ++ki, %s);%n", source);
        }
        @Override
        public void psSet(PrintModel ipw, String source, int k) {
            cc.add(ClassContextImpl.RUNTIME_J8TIME);
            ipw.printf("J8Time.setOffsetTime(ps, %d, %s);%n", k, source);
        }
        @Override
        public void xPsPush(PrintModel ipw, String orig, String name) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("J8Time.setOffsetTime(ps, ++ki, EmSQL.get(%s, \"%s\", OffsetTime.class));%n", orig, name);
        }
        @Override
        public void xPsPush(PrintModel ipw, String orig, String name, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("J8Time.setOffsetTime(ps, %d, EmSQL.get(%s, \"%s\", OffsetTime.class));%n", k, orig, name);
        }
        @Override
        public void csGetValue(PrintModel ipw, int k) {
            TimeZStd.csGetValue(ipw, k);
        }
    },
    NVarCharStd("NString", "NVARCHAR", "String"),
    NVarCharNil("NString", "NVARCHAR", "String"){
        @Override
        public void rsGetValue(PrintModel ipw, int k) {
            ipw.putf("rs.getNString(%d)", k);
        }
        @Override
        public void csGetValue(PrintModel ipw, int k) {
            ipw.putf("ps.getNString(%d)", k);
        }
        @Override
        public void psSet(PrintModel ipw, String source) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setNVarChar(ps, ++ki, %s);%n", source);
        }
        @Override
        public void psSet(PrintModel ipw, String source, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setNVarChar(ps, %d, %s);%n", k, source);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setNVarChar(ps, ++ki, EmSQL.get(%s, \"%s\", String.class));%n", orig, name);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setNVarChar(ps, %d, EmSQL.get(%s, \"%s\", String.class));%n", k, orig, name);
        }
    },
    NCharStd("NString", "NCHAR", "String"),
    NCharNil("NString", "NCHAR", "String"){
        @Override
        public void rsGetValue(PrintModel ipw, int k) {
            ipw.putf("rs.getNString(%d)", k);
        }

        @Override
        public void csGetValue(PrintModel ipw, int k) {
            ipw.putf("ps.getNString(%d)", k);
        }
        @Override
        public void psSet(PrintModel ipw, String source) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setNChar(ps, ++ki, %s);%n", source);
        }
        @Override
        public void psSet(PrintModel ipw, String source, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setNChar(ps, %d, %s);%n", k, source);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setNChar(ps, ++ki, EmSQL.get(%s, \"%s\", String.class));%n", orig, name);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setNChar(ps, %d, EmSQL.get(%s, \"%s\", String.class));%n", k, orig, name);
        }
    },
    LongVarCharStd("String", "LONGVARCHAR"),
    LongVarCharNil("String", "LONGVARCHAR"){
        @Override
        public void psSet(PrintModel ipw, String source) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setLongVarChar(ps, ++ki, %s);%n", source);
        }
        @Override
        public void psSet(PrintModel ipw, String source, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setLongVarChar(ps, %d, %s);%n", k, source);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setLongVarChar(ps, ++ki, EmSQL.get(%s, \"%s\", String.class));%n", orig, name);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setLongVarChar(ps, %d, EmSQL.get(%s, \"%s\", String.class));%n", k, orig, name);
        }
    },
    LongNVarCharStd("NString", "LONGNVARCHAR", "String"),
    LongNVarCharNil("NString", "LONGNVARCHAR", "String"){
        @Override
        public void rsGetValue(PrintModel ipw, int k) {
            ipw.putf("rs.getNString(%d)", k);
        }

        @Override
        public void csGetValue(PrintModel ipw, int k) {
            ipw.putf("ps.getNString(%d)", k);
        }
        @Override
        public void psSet(PrintModel ipw, String source) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setLongNVarChar(ps, ++ki, %s);%n", source);
        }
        @Override
        public void psSet(PrintModel ipw, String source, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setLongNVarChar(ps, %d, %s);%n", k, source);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setLongNVarChar(ps, ++ki, EmSQL.get(%s, \"%s\", String.class));%n", orig, name);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setLongNVarChar(ps, %d, EmSQL.get(%s, \"%s\", String.class));%n", k, orig, name);
        }
    },
    LongVarBinaryStd("Bytes", "LONGVARBINARY", "byte[]"),
    LongVarBinaryNil("Bytes", "LONGVARBINARY", "byte[]"){
        @Override
        public void rsGetValue(PrintModel ipw, int k) {
            ipw.putf("rs.getBytes(%d)", k);
        }

        @Override
        public void csGetValue(PrintModel ipw, int k) {
            ipw.putf("ps.getBytes(%d)", k);
        }
        @Override
        public void psSet(PrintModel ipw, String source) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setLongVarBinary(ps, ++ki, %s);%n", source);
        }
        @Override
        public void psSet(PrintModel ipw, String source, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setLongVarBinary(ps, %d, %s);%n", k, source);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setLongVarBinary(ps, ++ki, EmSQL.get(%s, \"%s\", byte[].class));%n", orig, name);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setLongVarBinary(ps, %d, EmSQL.get(%s, \"%s\", byte[].class));%n", k, orig, name);
        }
    },
    LongVarBinaryStreamStd("BinaryStream", "LONGVARBINARY", "InputStream"){
        @Override public Collection<String> requires() { return Set.of("java.io.InputStream"); }

        @Override
        public void csGetValue(PrintModel ipw, int k) {
            throw new IllegalArgumentException("Output BinaryStream not present in CallableStatement");
        }
    },
    LongVarBinaryStreamNil("BinaryStream", "LONGVARBINARY", "InputStream"){
        @Override public Collection<String> requires() { return Set.of("java.io.InputStream"); }
        @Override
        public void rsGetValue(PrintModel ipw, int k) { LongVarBinaryStreamStd.rsGetValue(ipw, k); }

        @Override
        public void csGetValue(PrintModel ipw, int k) { LongVarBinaryStreamStd.csGetValue(ipw, k); }
        @Override
        public void psSet(PrintModel ipw, String source) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setBinaryStream(ps, ++ki, %s);%n", source);
        }
        @Override
        public void psSet(PrintModel ipw, String source, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setBinaryStream(ps, %d, %s);%n", k, source);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setBinaryStream(ps, ++ki, EmSQL.get(%s, \"%s\", InputStream.class));%n", orig, name);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setBinaryStream(ps, %d, EmSQL.get(%s, \"%s\", InputStream.class));%n", k, orig, name);
        }
    },
    LongVarCharStreamStd("CharacterStream", "LONGVARCHAR", "Reader"){
        @Override public Collection<String> requires() { return Set.of("java.io.Reader"); }
    },
    LongVarCharStreamNil("CharacterStream", "LONGVARCHAR", "Reader"){
        @Override public Collection<String> requires() { return Set.of("java.io.Reader"); }
        @Override
        public void rsGetValue(PrintModel ipw, int k) {
            ipw.putf("rs.getCharacterStream(%d)", k);
        }

        @Override
        public void csGetValue(PrintModel ipw, int k) {
            ipw.putf("ps.getCharacterStream(%d)", k);
        }
        @Override
        public void psSet(PrintModel ipw, String source) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setCharacterStream(ps, ++ki, %s);%n", source);
        }
        @Override
        public void psSet(PrintModel ipw, String source, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setCharacterStream(ps, %d, %s);%n", k, source);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setCharacterStream(ps, ++ki, EmSQL.get(%s, \"%s\", Reader.class));%n", orig, name);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setCharacterStream(ps, %d, EmSQL.get(%s, \"%s\", Reader.class));%n", k, orig, name);
        }
    },
    LongNVarCharStreamStd("NCharacterStream", "LONGNVARCHAR", "Reader"){
        @Override public Collection<String> requires() { return Set.of("java.io.Reader"); }
    },
    LongNVarCharStreamNil("NCharacterStream", "LONGNVARCHAR", "Reader"){
        @Override public Collection<String> requires() { return Set.of("java.io.Reader"); }
        @Override
        public void rsGetValue(PrintModel ipw, int k) {
            ipw.putf("rs.getNCharacterStream(%d)", k);
        }
        @Override
        public void csGetValue(PrintModel ipw, int k) {
            ipw.putf("ps.getNCharacterStream(%d)", k);
        }
        @Override
        public void psSet(PrintModel ipw, String source) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setNCharacterStream(ps, ++ki, %s);%n", source);
        }
        @Override
        public void psSet(PrintModel ipw, String source, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setNCharacterStream(ps, %d, %s);%n", k, source);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setNCharacterStream(ps, ++ki, EmSQL.get(%s, \"%s\", Reader.class));%n", orig, name);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setNCharacterStream(ps, %d, EmSQL.get(%s, \"%s\", Reader.class));%n", k, orig, name);
        }
    },
    BlobStreamStd("Blob", "BLOB", "InputStream") {
        @Override public Collection<String> requires() { return Set.of("java.io.InputStream"); }
        @Override
        public void rsGetValue(PrintModel ipw, int k) {
            ipw.putf("rs.getBinaryStream(%d)", k);
        }

        @Override
        public void csGetValue(PrintModel ipw, int k) {
            throw new IllegalArgumentException("Output BinaryStream not present in CallableStatement");
        }
    },
    BlobStreamNil("Blob", "BLOB", "InputStream") {
        @Override public Collection<String> requires() { return Set.of("java.io.InputStream"); }
        @Override
        public void rsGetValue(PrintModel ipw, int k) { BlobStreamStd.rsGetValue(ipw, k); }

        @Override
        public void csGetValue(PrintModel ipw, int k) { BlobStreamStd.csGetValue(ipw, k); }
        @Override
        public void psSet(PrintModel ipw, String source) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setBlobStream(ps, ++ki, %s);%n", source);
        }
        @Override
        public void psSet(PrintModel ipw, String source, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setBlobStream(ps, %d, %s);%n", k, source);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setBlobStream(ps, ++ki, EmSQL.get(%s, \"%s\", InputStream.class));%n", orig, name);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setBlobStream(ps, %d, EmSQL.get(%s, \"%s\", InputStream.class));%n", k, orig, name);
        }
    },
    ClobStreamStd("Clob", "CLOB", "Reader") {
        @Override public Collection<String> requires() { return Set.of("java.io.Reader"); }
        @Override
        public void rsGetValue(PrintModel ipw, int k) {
            ipw.putf("rs.getCharacterStream(%d)", k);
        }

        @Override
        public void csGetValue(PrintModel ipw, int k) {
            ipw.putf("ps.getCharacterStream(%d)", k);
        }
    },
    ClobStreamNil("Clob", "CLOB", "Reader") {
        @Override public Collection<String> requires() { return Set.of("java.io.Reader"); }
        @Override
        public void rsGetValue(PrintModel ipw, int k) {
            ipw.putf("rs.getCharacterStream(%d)", k);
        }

        @Override
        public void csGetValue(PrintModel ipw, int k) {
            ipw.putf("ps.getCharacterStream(%d)", k);
        }
        @Override
        public void psSet(PrintModel ipw, String source) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setClobStream(ps, ++ki, %s);%n", source);
        }
        @Override
        public void psSet(PrintModel ipw, String source, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setClobStream(ps, %d, %s);%n", k, source);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setClobStream(ps, ++ki, EmSQL.get(%s, \"%s\", Reader.class));%n", orig, name);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setClobStream(ps, %d, EmSQL.get(%s, \"%s\", Reader.class));%n", k, orig, name);
        }
    },
    NClobStreamStd("NClob", "CLOB", "Reader") {
        @Override public Collection<String> requires() { return Set.of("java.io.Reader"); }
        @Override
        public void rsGetValue(PrintModel ipw, int k) {
            ipw.putf("rs.getNCharacterStream(%d)", k);
        }

        @Override
        public void csGetValue(PrintModel ipw, int k) {
            ipw.putf("ps.getNCharacterStream(%d)", k);
        }
    },
    NClobStreamNil("NClob", "CLOB", "Reader") {
        @Override public Collection<String> requires() { return Set.of("java.io.Reader"); }
        @Override
        public void rsGetValue(PrintModel ipw, int k) {
            ipw.putf("rs.getNCharacterStream(%d)", k);
        }
        @Override
        public void csGetValue(PrintModel ipw, int k) {
            ipw.putf("ps.getNCharacterStream(%d)", k);
        }
        @Override
        public void psSet(PrintModel ipw, String source) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setNClobStream(ps, ++ki, %s);%n", source);
        }
        @Override
        public void psSet(PrintModel ipw, String source, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setNClobStream(ps, %d, %s);%n", k, source);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setNClobStream(ps, ++ki, EmSQL.get(%s, \"%s\", Reader.class));%n", orig, name);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setNClobStream(ps, %d, EmSQL.get(%s, \"%s\", Reader.class));%n", k, orig, name);
        }
    },
    BlobStd("Blob", "BLOB"),
    BlobNil("Blob", "BLOB"){
        @Override
        public void psSet(PrintModel ipw, String source) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setBlob(ps, ++ki, %s);%n", source);
        }
        @Override
        public void psSet(PrintModel ipw, String source, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setBlob(ps, %d, %s);%n", k, source);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setBlob(ps, ++ki, EmSQL.get(%s, \"%s\", Blob.class));%n", orig, name);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setBlob(ps, %d, EmSQL.get(%s, \"%s\", Blob.class));%n", k, orig, name);
        }
    },
    ClobStd("Clob", "CLOB"),
    ClobNil("Clob", "CLOB"){
        @Override
        public void psSet(PrintModel ipw, String source) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setClob(ps, ++ki, %s);%n", source);
        }
        @Override
        public void psSet(PrintModel ipw, String source, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setClob(ps, %d, %s);%n", k, source);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setClob(ps, ++ki, EmSQL.get(%s, \"%s\", Clob.class));%n", orig, name);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setClob(ps, %d, EmSQL.get(%s, \"%s\", Clob.class));%n", k, orig, name);
        }
    },
    NClobStd("NClob", "NCLOB"),
    NClobNil("NClob", "NCLOB"){
        @Override
        public void psSet(PrintModel ipw, String source) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setNClob(ps, ++ki, %s);%n", source);
        }
        @Override
        public void psSet(PrintModel ipw, String source, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setNClob(ps, %d, %s);%n", k, source);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setNClob(ps, ++ki, EmSQL.get(%s, \"%s\", NClob.class));%n", orig, name);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setNClob(ps, %d, EmSQL.get(%s, \"%s\", NClob.class));%n", k, orig, name);
        }
    },
    RefStd("Ref", "REF"),
    RefNil("Ref", "REF"){
        @Override
        public void psSet(PrintModel ipw, String source) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setRef(ps, ++ki, %s);%n", source);
        }
        @Override
        public void psSet(PrintModel ipw, String source, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setRef(ps, %d, %s);%n", k, source);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setRef(ps, ++ki, EmSQL.get(%s, \"%s\", Ref.class));%n", orig, name);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setRef(ps, %d, EmSQL.get(%s, \"%s\", Ref.class));%n", k, orig, name);
        }
    },
    RowIdStd("RowId", "ROWID"),
    RowIdNil("RowId", "ROWID"){
        @Override
        public void psSet(PrintModel ipw, String source) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setRowId(ps, ++ki, %s);%n", source);
        }
        @Override
        public void psSet(PrintModel ipw, String source, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setRowId(ps, %d, %s);%n", k, source);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setRowId(ps, ++ki, EmSQL.get(%s, \"%s\", RowId.class));%n", orig, name);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setRowId(ps, %d, EmSQL.get(%s, \"%s\", RowId.class));%n", k, orig, name);
        }
    },
    SQLXMLStd("SQLXML", "SQLXML"),
    SQLXMLNil("SQLXML", "SQLXML"){
        @Override
        public void psSet(PrintModel ipw, String source) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setSQLXML(ps, ++ki, %s);%n", source);
        }
        @Override
        public void psSet(PrintModel ipw, String source, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setSQLXML(ps, %d, %s);%n", k, source);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setSQLXML(ps, ++ki, EmSQL.get(%s, \"%s\", SQLXML.class));%n", orig, name);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setSQLXML(ps, %d, EmSQL.get(%s, \"%s\", SQLXML.class));%n", k, orig, name);
        }
    },
    ArrayStd("Array", "ARRAY"),
    ArrayNil("Array", "ARRAY"){
        @Override
        public void psSet(PrintModel ipw, String source) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setArray(ps, ++ki, %s);%n", source);
        }
        @Override
        public void psSet(PrintModel ipw, String source, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setArray(ps, %d, %s);%n", k, source);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setArray(ps, ++ki, EmSQL.get(%s, \"%s\", Array.class));%n", orig, name);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setArray(ps, %d, EmSQL.get(%s, \"%s\", Array.class));%n", k, orig, name);
        }
    },
    URLStd("URL", "DATALINK") {
        @Override public Collection<String> requires() { return Set.of("java.net.URL"); }
    },
    URLNil("URL", "DATALINK") {
        @Override public Collection<String> requires() { return Set.of("java.net.URL"); }
        @Override
        public void psSet(PrintModel ipw, String source) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setURL(ps, ++ki, %s);%n", source);
        }
        @Override
        public void psSet(PrintModel ipw, String source, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setURL(ps, %d, %s);%n", k, source);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setURL(ps, ++ki, EmSQL.get(%s, \"%s\", URL.class));%n", orig, name);
        }
        @Override
        public  void xPsPush(PrintModel ipw, String orig, String name, int k) {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.printf("EmSQL.setURL(ps, %d, EmSQL.get(%s, \"%s\", URL.class));%n", k, orig, name);
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

    @Override
    public void rsGetValue(PrintModel ipw, int k) {
        if (!isNullable() || isPlainClass) {
            ipw.putf("rs.get%s(%d)", jdbc, k);
        } else {
            cc.add(ClassContextImpl.RUNTIME_EMSQL);
            ipw.putf("EmSQL.get%s(rs,%d)", jdbc, k);
        }
    }
    public  void xPsPush(PrintModel ipw, String orig, String name) {
        cc.add(ClassContextImpl.RUNTIME_EMSQL);
        if (!isNullable()) {
            ipw.printf("ps.set%s(++ki, EmSQL.get(%s, \"%s\", %s.class));%n", jdbc, orig, name, wrapper);
        } else {
            ipw.printf("EmSQL.set%s(ps, ++ki, EmSQL.get(%s, \"%s\", %s.class));%n", jdbc, orig, name, wrapper);
        }
    }
    public  void xPsPush(PrintModel ipw, String orig, String name, int k) {
        cc.add(ClassContextImpl.RUNTIME_EMSQL);
        if (!isNullable()) {
            ipw.printf("ps.set%s(%d, EmSQL.get(%s, \"%s\", %s.class));%n", jdbc, k, orig, name, wrapper);
        } else {
            ipw.printf("EmSQL.set%s(ps, %d, EmSQL.get(%s, \"%s\", %s.class));%n", jdbc, k, orig, name, wrapper);
        }
    }

    @Override
    public void registerOut(PrintModel ipw) {
        ipw.printf("ps.registerOutParameter(++ki, Types.%s);%n", sql);
    }

    @Override
    public void registerOut(PrintModel ipw, int k) {
        ipw.printf("ps.registerOutParameter(%d, Types.%s);%n", k, sql);
    }

    @Override
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
