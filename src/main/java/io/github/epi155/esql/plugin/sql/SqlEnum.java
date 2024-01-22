package io.github.epi155.esql.plugin.sql;

import io.github.epi155.esql.plugin.ClassContext;
import io.github.epi155.esql.plugin.IndentPrintWriter;
import lombok.Getter;

import java.util.Collection;
import java.util.Set;

@Getter
public enum SqlEnum {
    BooleanStd {
        @Override
        public void psSet(IndentPrintWriter ipw, int k, String source) {
            ipw.printf("ps.setBoolean(%d, %s);%n", k, source);
        }

        @Override
        public void rsGet(IndentPrintWriter ipw, int k, String target, ClassContext cc) {
            ipw.printf("%s(rs.getBoolean(%d));%n", target, k);
        }

        @Override
        public String getRaw() {
            return "boolean";
        }

        @Override
        public String getAccess() {
            return "Boolean";
        }

        @Override
        public void setValue(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("ps.setBoolean(%d, %s);%n", k, name);
        }

        @Override
        public void rsGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            ipw.printf("boolean o =  rs.getBoolean(%d);%n", k);
        }

        @Override
        public void psPush(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("ps.setBoolean(%d, ESQL.get(i, \"%s\", Boolean.class));%n", k, name);
        }

        @Override
        public void register(IndentPrintWriter ipw, int k) {
            ipw.printf("ps.registerOutParameter(%d, Types.BOOLEAN);%n", k);
        }

        @Override
        public void psGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            ipw.printf("boolean o =  ps.getBoolean(%d);%n", k);
        }

        @Override
        public void psGet(IndentPrintWriter ipw, int k, String cName, ClassContext cc) {
            ipw.printf("o.set%s(ps.getBoolean(%d));%n", cName, k);
        }

        @Override
        public void rsPull(IndentPrintWriter ipw, Integer k, String name) {
            ipw.printf("ESQL.set(o, \"%s\", rs.getBoolean(%d));%n", name, k);
        }
    },
    BooleanNil {
        @Override
        public void psSet(IndentPrintWriter ipw, int k, String source) {
            ipw.printf("{ Boolean it = %s; if (it==null) ps.setNull(%2$d, Types.BOOLEAN); else ps.setBoolean(%2$d, it); }%n", source, k);
        }

        @Override
        public void rsGet(IndentPrintWriter ipw, int k, String target, ClassContext cc) {
            cc.add("io.github.epi155.esql.runtime.ESQL");
            ipw.printf("%s(ESQL.box(rs.getBoolean(%d), rs.wasNull()));%n", target, k);
        }

        @Override
        public String getRaw() {
            return "Boolean";
        }

        @Override
        public String getAccess() {
            return "Boolean";
        }

        @Override
        public void setValue(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("if (%s == null) {%n", name);
            ipw.more();
            ipw.printf("ps.setNull(%d, Types.BOOLEAN);%n", k);
            ipw.orElse();
            ipw.printf("ps.setBoolean(%d, %s);%n", k, name);
            ipw.ends();
        }

        @Override
        public void rsGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            cc.add("io.github.epi155.esql.runtime.ESQL");
            ipw.printf("Boolean o = ESQL.box(rs.getBoolean(%d), rs.wasNull();%n", k);
        }

        @Override
        public void psPush(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("{ Boolean it = ESQL.get(i, \"%s\", Boolean.class); if (it==null) ps.setNull(%2$d, Types.BOOLEAN); else ps.setBoolean(%2$d, it); };%n", name, k);
        }

        @Override
        public void register(IndentPrintWriter ipw, int k) {
            ipw.printf("ps.registerOutParameter(%d, Types.BOOLEAN);%n", k);
        }

        @Override
        public void psGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            cc.add("io.github.epi155.esql.runtime.ESQL");
            ipw.printf("Boolean o = ESQL.box(ps.getBoolean(%d), ps.wasNull();%n", k);
        }

        @Override
        public void psGet(IndentPrintWriter ipw, int k, String cName, ClassContext cc) {
            cc.add("io.github.epi155.esql.runtime.ESQL");
            ipw.printf("o.set%s(ESQL.box(ps.getBoolean(%d), ps.wasNull()));%n", cName, k);
        }

        @Override
        public void rsPull(IndentPrintWriter ipw, Integer k, String name) {
            ipw.printf("ESQL.set(o, \"%s\", ESQL.box(rs.getBoolean(%d), rs.wasNull()));%n", name, k);
        }
    },
    ShortStd {
        @Override
        public void psSet(IndentPrintWriter ipw, int k, String source) {
            ipw.printf("ps.setShort(%d, %s);%n", k, source);
        }

        @Override
        public void rsGet(IndentPrintWriter ipw, int k, String cName, ClassContext cc) {
            ipw.printf("o.setShort(rs.get%s(%d));%n", cName, k);
        }

        @Override
        public String getRaw() {
            return "short";
        }

        @Override
        public String getAccess() {
            return "Short";
        }

        @Override
        public void setValue(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("ps.setShort(%d, %s);%n", k, name);
        }

        @Override
        public void rsGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            ipw.printf("short o =  rs.getShort(%d);%n", k);
        }

        @Override
        public void psPush(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("ps.setShort(%d, ESQL.get(i, \"%s\", Short.class));%n", k, name);
        }

        @Override
        public void register(IndentPrintWriter ipw, int k) {
            ipw.printf("ps.registerOutParameter(%d, Types.SMALLINT);%n", k);
        }

        @Override
        public void psGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            ipw.printf("short o =  ps.getShort(%d);%n", k);
        }

        @Override
        public void psGet(IndentPrintWriter ipw, int k, String cName, ClassContext cc) {
            ipw.printf("o.setShort(ps.get%s(%d));%n", cName, k);
        }

        @Override
        public void rsPull(IndentPrintWriter ipw, Integer k, String name) {
            ipw.printf("ESQL.set(o, \"%s\", rs.getShort(%d));%n", name, k);
        }
    },
    ShortNil {
        @Override
        public void psSet(IndentPrintWriter ipw, int k, String source) {
            ipw.printf("{ Short it = %s; if (it==null) ps.setNull(%2$d, Types.SMALLINT); else ps.setShort(%2$d, it); }%n", source, k);
        }

        @Override
        public void rsGet(IndentPrintWriter ipw, int k, String target, ClassContext cc) {
            cc.add("io.github.epi155.esql.runtime.ESQL");
            ipw.printf("%s(ESQL.box(rs.getShort(%d), rs.wasNull()));%n", target, k);
        }

        @Override
        public String getRaw() {
            return "short";
        }

        @Override
        public String getAccess() {
            return "Short";
        }

        @Override
        public void setValue(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("if (%s == null) {%n", name);
            ipw.more();
            ipw.printf("ps.setNull(%d, Types.SMALLINT);%n", k);
            ipw.orElse();
            ipw.printf("ps.setShort(%d, %s);%n", k, name);
            ipw.ends();
        }

        @Override
        public void rsGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            cc.add("io.github.epi155.esql.runtime.ESQL");
            ipw.printf("Short o = ESQL.box(rs.getShort(%d), rs.wasNull();%n", k);
        }

        @Override
        public void psPush(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("{ Short it = ESQL.get(i, \"%s\", Short.class); if (it==null) ps.setNull(%2$d, Types.SMALLINT); else ps.setShort(%2$d, it); };%n", name, k);
        }

        @Override
        public void register(IndentPrintWriter ipw, int k) {
            ipw.printf("ps.registerOutParameter(%d, Types.SMALLINT);%n", k);
        }

        @Override
        public void psGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            cc.add("io.github.epi155.esql.runtime.ESQL");
            ipw.printf("Short o = ESQL.box(ps.getShort(%d), ps.wasNull();%n", k);
        }

        @Override
        public void psGet(IndentPrintWriter ipw, int k, String cName, ClassContext cc) {
            cc.add("io.github.epi155.esql.runtime.ESQL");
            ipw.printf("o.set%s(ESQL.box(ps.getShort(%d), ps.wasNull()));%n", cName, k);
        }

        @Override
        public void rsPull(IndentPrintWriter ipw, Integer k, String name) {
            ipw.printf("ESQL.set(o, \"%s\", ESQL.box(rs.getShort(%d), rs.wasNull()));%n", name, k);
        }
    },
    IntegerStd {
        @Override
        public void psSet(IndentPrintWriter ipw, int k, String source) {
            ipw.printf("ps.setInt(%d, %s);%n", k, source);
        }

        @Override
        public void psPush(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("ps.setInt(%d, ESQL.get(i, \"%s\", Integer.class));%n", k, name);
        }

        @Override
        public void register(IndentPrintWriter ipw, int k) {
            ipw.printf("ps.registerOutParameter(%d, Types.INTEGER);%n", k);
        }

        @Override
        public void psGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            ipw.printf("int o =ps.getInt(%d);%n", k);
        }

        @Override
        public void psGet(IndentPrintWriter ipw, int k, String cName, ClassContext cc) {
            ipw.printf("o.set%s(ps.getInt(%d));%n", cName, k);
        }

        @Override
        public void rsPull(IndentPrintWriter ipw, Integer k, String name) {
            ipw.printf("ESQL.set(o, \"%s\", rs.getInt(%d));%n", name, k);
        }

        @Override
        public void rsGet(IndentPrintWriter ipw, int k, String target, ClassContext cc) {
            ipw.printf("%s(rs.getInt(%d));%n", target, k);
        }

        @Override
        public String getRaw() {
            return "int";
        }

        @Override
        public String getAccess() {
            return "Integer";
        }

        @Override
        public void setValue(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("ps.setInt(%d, %s);%n", k, name);
        }

        @Override
        public void rsGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            ipw.printf("int o =rs.getInt(%d);%n", k);
        }
    },
    IntegerNil {
        @Override
        public void psSet(IndentPrintWriter ipw, int k, String source) {
            ipw.printf("{ Integer it = %s; if (it==null) ps.setNull(%2$d, Types.INTEGER); else ps.setInt(%2$d, it); }%n", source, k);
        }
        @Override
        public void psPush(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("{ Integer it = ESQL.get(i, \"%s\", Integer.class); if (it==null) ps.setNull(%2$d, Types.INTEGER); else ps.setInt(%2$d, it); };%n", name, k);
        }

        @Override
        public void register(IndentPrintWriter ipw, int k) {
            ipw.printf("ps.registerOutParameter(%d, Types.INTEGER);%n", k);
        }

        @Override
        public void psGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            cc.add("io.github.epi155.esql.runtime.ESQL");
            ipw.printf("Integer o = ESQL.box(ps.getInt(%d), ps.wasNull();%n", k);
        }

        @Override
        public void psGet(IndentPrintWriter ipw, int k, String cName, ClassContext cc) {
            cc.add("io.github.epi155.esql.runtime.ESQL");
            ipw.printf("o.set%s(ESQL.box(ps.getInt(%d), ps.wasNull()));%n", cName, k);
        }

        @Override
        public void rsPull(IndentPrintWriter ipw, Integer k, String name) {
            ipw.printf("ESQL.set(o, \"%s\", ESQL.box(rs.getInt(%d), rs.wasNull()));%n", name, k);
        }

        @Override
        public void rsGet(IndentPrintWriter ipw, int k, String target, ClassContext cc) {
            cc.add("io.github.epi155.esql.runtime.ESQL");
            ipw.printf("%s(ESQL.box(rs.getInt(%d), rs.wasNull()));%n", target, k);
        }

        @Override
        public String getRaw() {
            return "Integer";
        }

        @Override
        public String getAccess() {
            return "Integer";
        }

        @Override
        public void setValue(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("if (%s == null) {%n", name);
            ipw.more();
            ipw.printf("ps.setNull(%d, Types.INTEGER);%n", k);
            ipw.orElse();
            ipw.printf("ps.setInt(%d, %s);%n", k, name);
            ipw.ends();
        }

        @Override
        public void rsGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            cc.add("io.github.epi155.esql.runtime.ESQL");
            ipw.printf("Integer o = ESQL.box(rs.getInt(%d), rs.wasNull();%n", k);
        }
    },
    LongStd {
        @Override
        public void psSet(IndentPrintWriter ipw, int k, String source) {
            ipw.printf("ps.setLong(%d, %s);%n", k, source);
        }

        @Override
        public void rsGet(IndentPrintWriter ipw, int k, String target, ClassContext cc) {
            ipw.printf("%s(rs.getLong(%d));%n", target, k);
        }

        @Override
        public String getRaw() {
            return "long";
        }

        @Override
        public String getAccess() {
            return "Long";
        }

        @Override
        public void setValue(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("ps.setLong(%d, %s);%n", k, name);
        }

        @Override
        public void rsGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            ipw.printf("long o = rs.getLong(%d);%n", k);
        }

        @Override
        public void psPush(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("ps.setLong(%d, ESQL.get(i, \"%s\", Long.class));%n", k, name);
        }

        @Override
        public void register(IndentPrintWriter ipw, int k) {
            ipw.printf("ps.registerOutParameter(%d, Types.BIGINT);%n", k);
        }

        @Override
        public void psGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            ipw.printf("long o = ps.getLong(%d);%n", k);
        }

        @Override
        public void psGet(IndentPrintWriter ipw, int k, String cName, ClassContext cc) {
            ipw.printf("o.set%s(ps.getLong(%d));%n", cName, k);
        }

        @Override
        public void rsPull(IndentPrintWriter ipw, Integer k, String name) {
            ipw.printf("ESQL.set(o, \"%s\", rs.getLong(%d));%n", name, k);
        }
    },
    LongNil {
        @Override
        public void psSet(IndentPrintWriter ipw, int k, String source) {
            ipw.printf("{ Long it = %s; if (it==null) ps.setNull(%2$d, Types.BIGINT); else ps.setLong(%2$d, it); }%n", source, k);
        }

        @Override
        public void rsGet(IndentPrintWriter ipw, int k, String target, ClassContext cc) {
            cc.add("io.github.epi155.esql.runtime.ESQL");
            ipw.printf("%s(ESQL.box(rs.getLong(%d), rs.wasNull()));%n", target, k);
        }

        @Override
        public String getRaw() {
            return "Long";
        }

        @Override
        public String getAccess() {
            return "Long";
        }

        @Override
        public void setValue(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("if (%s == null) {%n", name);
            ipw.more();
            ipw.printf("ps.setNull(%d, Types.BIGINT);%n", k);
            ipw.orElse();
            ipw.printf("ps.setLong(%d, %s);%n", k, name);
            ipw.ends();
        }

        @Override
        public void rsGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            cc.add("io.github.epi155.esql.runtime.ESQL");
            ipw.printf("Long o = ESQL.box(rs.getLong(%d), rs.wasNull();%n", k);
        }

        @Override
        public void psPush(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("{ Long it = ESQL.get(i, \"%s\", Long.class); if (it==null) ps.setNull(%2$d, Types.BIGINT); else ps.setLong(%2$d, it); };%n", name, k);
        }

        @Override
        public void register(IndentPrintWriter ipw, int k) {
            ipw.printf("ps.registerOutParameter(%d, Types.BIGINT);%n", k);
        }

        @Override
        public void psGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            cc.add("io.github.epi155.esql.runtime.ESQL");
            ipw.printf("Long o = ESQL.box(ps.getLong(%d), ps.wasNull();%n", k);
        }

        @Override
        public void psGet(IndentPrintWriter ipw, int k, String cName, ClassContext cc) {
            cc.add("io.github.epi155.esql.runtime.ESQL");
            ipw.printf("o.set%s(ESQL.box(ps.getLong(%d), ps.wasNull()));%n", cName, k);
        }

        @Override
        public void rsPull(IndentPrintWriter ipw, Integer k, String name) {
            ipw.printf("ESQL.set(o, \"%s\", ESQL.box(rs.getLong(%d), rs.wasNull()));%n", name, k);
        }
    },
    VarCharStd {
        @Override
        public void psSet(IndentPrintWriter ipw, int k, String source) {
            ipw.printf("ps.setString(%d, %s);%n", k, source);
        }

        @Override
        public void psPush(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("ps.setString(%d, ESQL.get(i, \"%s\", String.class));%n", k, name);
        }

        @Override
        public void register(IndentPrintWriter ipw, int k) {
            ipw.printf("ps.registerOutParameter(%d, Types.VARCHAR);%n", k);
        }

        @Override
        public void psGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            ipw.printf("String o = ps.getString(%d);%n", k);
        }

        @Override
        public void psGet(IndentPrintWriter ipw, int k, String cName, ClassContext cc) {
            ipw.printf("o.set%s(ps.getString(%d));%n", cName, k);
        }

        @Override
        public void rsPull(IndentPrintWriter ipw, Integer k, String name) {
            ipw.printf("ESQL.set(o, \"%s\", rs.getString(%d));%n", name, k);
        }

        @Override
        public void rsGet(IndentPrintWriter ipw, int k, String target, ClassContext cc) {
            ipw.printf("%s(rs.getString(%d));%n", target, k);
        }

        @Override
        public String getRaw() {
            return "String";
        }

        @Override
        public String getAccess() {
            return "String";
        }

        @Override
        public void setValue(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("ps.setString(%d, %s);%n", k, name);
        }

        @Override
        public void rsGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            ipw.printf("String o = rs.getString(%d);%n", k);
        }
    },
    VarCharNil {
        @Override
        public void psSet(IndentPrintWriter ipw, int k, String source) {
            ipw.printf("{ String it = %s; if (it==null) ps.setNull(%2$d, Types.VARCHAR); else ps.setString(%2$d, it); }%n", source, k);
        }
        @Override
        public void psPush(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("{ String it = ESQL.get(i, \"%s\", String.class); if (it==null) ps.setNull(%2$d, Types.VARCHAR); else ps.setInt(%2$d, it); };%n", name, k);
        }

        @Override
        public void register(IndentPrintWriter ipw, int k) {
            VarCharStd.register(ipw, k);
        }

        @Override
        public void psGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            VarCharStd.psGetValue(ipw, k, cc);
        }

        @Override
        public void psGet(IndentPrintWriter ipw, int k, String cName, ClassContext cc) {
            VarCharStd.psGet(ipw, k, cName, cc);
        }

        @Override
        public void rsPull(IndentPrintWriter ipw, Integer k, String name) {
            VarCharStd.rsPull(ipw,k,name);
        }

        @Override
        public void rsGet(IndentPrintWriter ipw, int k, String cName, ClassContext cc) {
            VarCharStd.rsGet(ipw, k, cName, cc);
        }

        @Override
        public String getRaw() {
            return VarCharStd.getRaw();
        }

        @Override
        public String getAccess() {
            return VarCharStd.getAccess();
        }

        @Override
        public void setValue(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("if (%s == null) {%n", name);
            ipw.more();
            ipw.printf("ps.setNull(%d, Types.VARCHAR);%n", k);
            ipw.orElse();
            ipw.printf("ps.setString(%d, %s);%n", k, name);
            ipw.ends();
        }

        @Override
        public void rsGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            ipw.printf("String o = rs.getString(%d);%n", k);
        }
    },
    DateStd {
        @Override
        public void psSet(IndentPrintWriter ipw, int k, String source) {
            ipw.printf("ps.setDate(%d, %s);%n", k, source);
        }

        @Override
        public void rsGet(IndentPrintWriter ipw, int k, String target, ClassContext cc) {
            ipw.printf("%s(rs.getDate(%d));%n", target, k);
        }

        @Override
        public String getRaw() {
            return "Date";
        }

        @Override
        public String getAccess() {
            return "Date";
        }

        @Override
        public void setValue(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("ps.setDate(%d, %s);%n", k, name);
        }

        @Override
        public void rsGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            ipw.printf("Date o = rs.getDate(%d);%n", k);
        }

        @Override
        public void psPush(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("ps.setSDate(%d, ESQL.get(i, \"%s\", Date.class));%n", k, name);
        }

        @Override
        public void register(IndentPrintWriter ipw, int k) {
            ipw.printf("ps.registerOutParameter(%d, Types.DATE);%n", k);
        }

        @Override
        public void psGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            ipw.printf("Date o = ps.getDate(%d);%n", k);
        }

        @Override
        public void psGet(IndentPrintWriter ipw, int k, String cName, ClassContext cc) {
            ipw.printf("o.set%s(ps.getDate(%d));%n", cName, k);
        }

        @Override
        public void rsPull(IndentPrintWriter ipw, Integer k, String name) {
            ipw.printf("ESQL.set(o, \"%s\", rs.getDate(%d));%n", name, k);
        }
    },
    DateNil {
        @Override
        public void psSet(IndentPrintWriter ipw, int k, String source) {
            ipw.printf("{ Date it = %s; if (it==null) ps.setNull(%2$d, Types.DATE); else ps.setDate(%2$d, it); }%n", source, k);
        }

        @Override
        public void rsGet(IndentPrintWriter ipw, int k, String target, ClassContext cc) {
            ipw.printf("%s(rs.getDate(%d));%n", target, k);
        }

        @Override
        public String getRaw() {
            return DateStd.getRaw();
        }

        @Override
        public String getAccess() {
            return DateStd.getAccess();
        }

        @Override
        public void setValue(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("if (%s == null) {%n", name);
            ipw.more();
            ipw.printf("ps.setNull(%d, Types.DATE);%n", k);
            ipw.orElse();
            ipw.printf("ps.setDate(%d, %s);%n", k, name);
            ipw.ends();
        }

        @Override
        public void rsGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            DateStd.rsGetValue(ipw, k, cc);
        }

        @Override
        public void psPush(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("{ Date it = ESQL.get(i, \"%s\", Date.class); if (it==null) ps.setNull(%2$d, Types.DATE); else ps.setDate(%2$d, it); };%n", name, k);
        }

        @Override
        public void register(IndentPrintWriter ipw, int k) {
            DateStd.register(ipw, k);
        }

        @Override
        public void psGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            DateStd.psGetValue(ipw, k, cc);
        }

        @Override
        public void psGet(IndentPrintWriter ipw, int k, String cName, ClassContext cc) {
            DateStd.psGet(ipw, k, cName, cc);
        }

        @Override
        public void rsPull(IndentPrintWriter ipw, Integer k, String name) {
            DateStd.rsPull(ipw, k, name);
        }
    },
    TimestampStd {
        @Override
        public void psSet(IndentPrintWriter ipw, int k, String source) {
            ipw.printf("ps.setTimestamp(%d, %s);%n", k, source);
        }

        @Override
        public void rsGet(IndentPrintWriter ipw, int k, String target, ClassContext cc) {
            ipw.printf("%s(rs.getTimestamp(%d));%n", target, k);
        }

        @Override
        public String getRaw() {
            return "Timestamp";
        }

        @Override
        public String getAccess() {
            return "Timestamp";
        }

        @Override
        public void setValue(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("ps.setTimestamp(%d, %s);%n", k, name);
        }

        @Override
        public void rsGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            ipw.printf("Timestamp o = rs.getTimestamp(%d);%n", k);
        }

        @Override
        public void psPush(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("ps.setTimestamp(%d, ESQL.get(i, \"%s\", Timestamp.class));%n", k, name);
        }

        @Override
        public void register(IndentPrintWriter ipw, int k) {
            ipw.printf("ps.registerOutParameter(%d, Types.TIMESTAMP);%n", k);
        }

        @Override
        public void psGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            ipw.printf("Timestamp o = ps.getTimestamp(%d);%n", k);
        }

        @Override
        public void psGet(IndentPrintWriter ipw, int k, String cName, ClassContext cc) {
            ipw.printf("o.set%s(ps.getTimestamp(%d));%n", cName, k);
        }

        @Override
        public void rsPull(IndentPrintWriter ipw, Integer k, String name) {
            ipw.printf("ESQL.set(o, \"%s\", rs.getBoolean(%d));%n", name, k);
        }
    },
    TimestampNil {
        @Override
        public void psSet(IndentPrintWriter ipw, int k, String source) {
            ipw.printf("{ Timestamp it = %s; if (it==null) ps.setNull(%2$d, Types.TIMESTAMP); else ps.setTimestamp(%2$d, it); }%n", source, k);
        }

        @Override
        public void rsGet(IndentPrintWriter ipw, int k, String cName, ClassContext cc) {
            TimestampStd.rsGet(ipw, k, cName, cc);
        }

        @Override
        public String getRaw() {
            return TimestampStd.getRaw();
        }

        @Override
        public String getAccess() {
            return TimestampStd.getAccess();
        }

        @Override
        public void setValue(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("if (%s == null) {%n", name);
            ipw.more();
            ipw.printf("ps.setNull(%d, Types.TIMESTAMP);%n", k);
            ipw.orElse();
            ipw.printf("ps.setTimestamp(%d, %s);%n", k, name);
            ipw.ends();
        }

        @Override
        public void rsGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            TimestampStd.rsGetValue(ipw, k, cc);
        }

        @Override
        public void psPush(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("{ Timestamp it = ESQL.get(i, \"%s\", Timestamp.class); if (it==null) ps.setNull(%2$d, Types.TIMESTAMP); else ps.setTimestamp(%2$d, it); };%n", name, k);
        }

        @Override
        public void register(IndentPrintWriter ipw, int k) {
            TimestampStd.register(ipw, k);
        }

        @Override
        public void psGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            TimestampStd.psGetValue(ipw, k, cc);
        }

        @Override
        public void psGet(IndentPrintWriter ipw, int k, String cName, ClassContext cc) {
            TimestampStd.psGet(ipw, k, cName, cc);
        }

        @Override
        public void rsPull(IndentPrintWriter ipw, Integer k, String name) {
            TimestampStd.rsPull(ipw, k, name);
        }
    },
    NumericStd {
        @Override
        public void psSet(IndentPrintWriter ipw, int k, String source) {
            ipw.printf("ps.setBigDecimal(%d, %s);%n", k, source);
        }

        @Override
        public void rsGet(IndentPrintWriter ipw, int k, String target, ClassContext cc) {
            ipw.printf("%s(rs.getBigDecimal(%d));%n", target, k);
        }

        @Override
        public String getRaw() {
            return "BigDecimal";
        }

        @Override
        public String getAccess() {
            return "BigDecimal";
        }

        @Override
        public void setValue(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("ps.setBigDecimal(%d, %s);%n", k, name);
        }

        @Override
        public void rsGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            ipw.printf("BigDecimal o = rs.getBigDecimal(%d);%n", k);
        }

        @Override
        public Collection<String> requires() {
            return Set.of("java.math.BigDecimal");
        }

        @Override
        public void rsPull(IndentPrintWriter ipw, Integer k, String name) {
            ipw.printf("ESQL.set(o, \"%s\", rs.getBigDecimal(%d));%n", name, k);
        }

        @Override
        public void psPush(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("ps.setBigDecimal(%d, ESQL.get(i, \"%s\", BigDecimal.class));%n", k, name);
        }

        @Override
        public void register(IndentPrintWriter ipw, int k) {
            ipw.printf("ps.registerOutParameter(%d, Types.NUMERIC);%n", k);
        }

        @Override
        public void psGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            ipw.printf("BigDecimal o = ps.getBigDecimal(%d);%n", k);
        }

        @Override
        public void psGet(IndentPrintWriter ipw, int k, String cName, ClassContext cc) {
            ipw.printf("o.set%s(ps.getBigDecimal(%d));%n", cName, k);
        }
    },
    NumericNil {
        @Override
        public void psSet(IndentPrintWriter ipw, int k, String source) {
            ipw.printf("{ BigDecimal it = %s; if (it==null) ps.setNull(%2$d, Types.NUMERIC); else ps.setBigDecimal(%2$d, it); }%n", source, k);
        }

        @Override
        public void rsGet(IndentPrintWriter ipw, int k, String cName, ClassContext cc) {
            NumericStd.rsGet(ipw, k, cName, cc);
        }

        @Override
        public String getRaw() {
            return NumericStd.getRaw();
        }

        @Override
        public String getAccess() {
            return NumericStd.getAccess();
        }

        @Override
        public void setValue(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("if (%s == null) {%n", name);
            ipw.more();
            ipw.printf("ps.setNull(%d, Types.NUMERIC);%n", k);
            ipw.orElse();
            ipw.printf("ps.setBigDecimal(%d, %s);%n", k, name);
            ipw.ends();
        }

        @Override
        public void rsGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            NumericStd.rsGetValue(ipw, k, cc);
        }

        @Override
        public Collection<String> requires() {
            return NumericStd.requires();
        }

        @Override
        public void rsPull(IndentPrintWriter ipw, Integer k, String name) {
            NumericStd.rsPull(ipw, k, name);
        }

        @Override
        public void psPush(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("{ BigDecimal it = ESQL.get(i, \"%s\", BigDecimal.class); if (it==null) ps.setNull(%2$d, Types.NUMERIC); else ps.setBigDecimal(%2$d, it); };%n", name, k);
        }

        @Override
        public void register(IndentPrintWriter ipw, int k) {
            NumericStd.register(ipw, k);
        }

        @Override
        public void psGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            NumericStd.psGetValue(ipw, k, cc);
        }

        @Override
        public void psGet(IndentPrintWriter ipw, int k, String cName, ClassContext cc) {
            NumericStd.psGet(ipw, k, cName, cc);
        }
    },
    LocalDateStd {
        @Override
        public void psSet(IndentPrintWriter ipw, int k, String source) {
            ipw.printf("ps.setDate(%d, Date.valueOf(%s));%n", k, source);
        }

        @Override
        public void rsGet(IndentPrintWriter ipw, int k, String target, ClassContext cc) {
            ipw.printf("%s(rs.getDate(%d).toLocalDate());%n", target, k);
        }

        @Override
        public String getRaw() {
            return "LocalDate";
        }

        @Override
        public String getAccess() {
            return "LocalDate";
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
            ipw.printf("ESQL.set(o, \"%s\", getDate(%d).toLocalDate());%n", name, k);
        }

        @Override
        public void psPush(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("ps.setSDate(%d, Date.valueOf(ESQL.get(i, \"%s\", LocalDate.class)));%n", k, name);
        }

        @Override
        public void register(IndentPrintWriter ipw, int k) {
            ipw.printf("ps.registerOutParameter(%d, Types.DATE);%n", k);
        }

        @Override
        public void psGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            ipw.printf("LocalDate o = ps.getDate(%d).toLocalDate();%n", k);
        }

        @Override
        public void psGet(IndentPrintWriter ipw, int k, String cName, ClassContext cc) {
            ipw.printf("o.set%s(ps.getDate(%d).toLocalDate());%n", cName, k);
        }
    },
    LocalDateNil {
        @Override
        public void psSet(IndentPrintWriter ipw, int k, String source) {
            ipw.printf("{ LocalDate it = %s; if (it==null) ps.setNull(%2$d, Types.DATE); else ps.setDate(%2$d, Date.valueOf(it)); }%n", k, source);
        }

        @Override
        public void rsGet(IndentPrintWriter ipw, int k, String target, ClassContext cc) {
            cc.add("io.github.epi155.esql.runtime.ESQL");
            ipw.printf("%s(ESQL.toLocalDate(rs.getDate(%d)));%n", target, k);
        }

        @Override
        public String getRaw() {
            return LocalDateStd.getRaw();
        }

        @Override
        public String getAccess() {
            return LocalDateStd.getAccess();
        }

        @Override
        public void setValue(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("if (%s == null) {%n", name);
            ipw.more();
            ipw.printf("ps.setNull(%d, Types.DATE);%n", k);
            ipw.orElse();
            ipw.printf("ps.setDate(%d, %s.toLocalDate());%n", k, name);
            ipw.ends();
        }

        @Override
        public void rsGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            cc.add("io.github.epi155.esql.runtime.ESQL");
            ipw.printf("LocalDate o = ESQL.toLocalDate(rs.getDate(%d));%n", k);
        }

        @Override
        public void psPush(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("{ LocalDate it = ESQL.get(i, \"%s\", LocalDate.class); if (it==null) ps.setNull(%2$d, Types.DATE); else ps.setDate(%2$d, Date.valueOf(it)); };%n", name, k);
        }

        @Override
        public void register(IndentPrintWriter ipw, int k) {
            ipw.printf("ps.registerOutParameter(%d, Types.DATE);%n", k);
        }

        @Override
        public void psGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            cc.add("io.github.epi155.esql.runtime.ESQL");
            ipw.printf("LocalDate o = ESQL.toLocalDate(ps.getDate(%d));%n", k);
        }

        @Override
        public void psGet(IndentPrintWriter ipw, int k, String cName, ClassContext cc) {
            cc.add("io.github.epi155.esql.runtime.ESQL");
            ipw.printf("o.set%s(ESQL.toLocalDate(ps.getDate(%d)));%n", cName, k);
        }

        @Override
        public void rsPull(IndentPrintWriter ipw, Integer k, String name) {
            ipw.printf("ESQL.set(o, \"%s\", ESQL.toLocalDate(ps.getDate(%d)));%n", name, k);
        }
    },
    LocalDateTimeStd {
        @Override
        public void psSet(IndentPrintWriter ipw, int k, String source) {
            ipw.printf("ps.setTimestamp(%d, Timestamp.valueOf(%s));%n", k, source);
        }

        @Override
        public void rsGet(IndentPrintWriter ipw, int k, String target, ClassContext cc) {
            ipw.printf("%s(rs.getTimestamp(%d).toLocalDateTime());%n", target, k);
        }

        @Override
        public String getRaw() {
            return "LocalDateTime";
        }

        @Override
        public String getAccess() {
            return "LocalDateTime";
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
            ipw.printf("ESQL.set(o, \"%s\", rs.getTimestamp(%d).toLocalDateTime());%n", name, k);
        }

        @Override
        public void psPush(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("ps.setTimestamp(%d, Timestamp.valueOf(ESQL.get(i, \"%s\", LocalDateTime.class)));%n", k, name);
        }

        @Override
        public void register(IndentPrintWriter ipw, int k) {
            ipw.printf("ps.registerOutParameter(%d, Types.TIMESTAMP);%n", k);
        }

        @Override
        public void psGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            ipw.printf("LocalDateTime o = ps.getTimestamp(%d).toLocalDateTime();%n", k);
        }

        @Override
        public void psGet(IndentPrintWriter ipw, int k, String cName, ClassContext cc) {
            ipw.printf("o.set%s(ps.getTimestamp(%d).toLocalDateTime());%n", cName, k);
        }
    },
    LocalDateTimeNil {
        @Override
        public void psSet(IndentPrintWriter ipw, int k, String so) {
            ipw.printf("{ LocalDateTime it = %s; if (it==null) ps.setNull(%2$d, Types.TIMESTAMP); else ps.setTimestamp(%2$d, Timestamp.valueOf(it)); }%n", so, k);
        }

        @Override
        public void rsGet(IndentPrintWriter ipw, int k, String target, ClassContext cc) {
            cc.add("io.github.epi155.esql.runtime.ESQL");
            ipw.printf("%s(ESQL.toLocalDateTime(rs.getTimestamp(%d)));%n", target, k);
        }

        @Override
        public String getRaw() {
            return "LocalDateTime";
        }

        @Override
        public String getAccess() {
            return "LocalDateTime";
        }

        @Override
        public void setValue(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("if (%s == null) {%n", name);
            ipw.more();
            ipw.printf("ps.setNull(%d, Types.TIMESTAMP);%n", k);
            ipw.orElse();
            ipw.printf("ps.setTimestamp(%d, %s.toLocalDateTime());%n", k, name);
            ipw.ends();
        }

        @Override
        public void rsGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            cc.add("io.github.epi155.esql.runtime.ESQL");
            ipw.printf("LocalDateTime o = ESQL.toLocalDateTime(rs.getTimestamp(%d));%n", k);
        }

        @Override
        public void psPush(IndentPrintWriter ipw, int k, String name) {
            ipw.printf("{ LocalDateTime it = ESQL.get(i, \"%s\", LocalDateTime.class); if (it==null) ps.setNull(%2$d, Types.TIMESTAMP); else ps.setTimestamp(%2$d, Timestamp.valueOf(it)); };%n", name, k);
        }

        @Override
        public void register(IndentPrintWriter ipw, int k) {
            ipw.printf("ps.registerOutParameter(%d, Types.TIMESTAMP);%n", k);
        }

        @Override
        public void psGetValue(IndentPrintWriter ipw, int k, ClassContext cc) {
            cc.add("io.github.epi155.esql.runtime.ESQL");
            ipw.printf("LocalDateTime o = ESQL.toLocalDateTime(ps.getTimestamp(%d));%n", k);
        }

        @Override
        public void psGet(IndentPrintWriter ipw, int k, String cName, ClassContext cc) {
            cc.add("io.github.epi155.esql.runtime.ESQL");
            ipw.printf("o.set%s(ESQL.toLocalDateTime(ps.getTimestamp(%d)));%n", cName, k);
        }

        @Override
        public void rsPull(IndentPrintWriter ipw, Integer k, String name) {
            ipw.printf("ESQL.set(o, \"%s\", ESQL.toLocalDateTime(ps.getTimestamp(%d)));%n", name, k);
        }
    };
    public abstract void psSet(IndentPrintWriter ipw, int k, String source);
    public abstract void rsGet(IndentPrintWriter ipw, int k, String target, ClassContext cc);
    public abstract String getRaw();
    public abstract String getAccess();
    public abstract void setValue(IndentPrintWriter ipw, int k, String name);
    public abstract void rsGetValue(IndentPrintWriter ipw, int k, ClassContext cc);
    public  abstract void psPush(IndentPrintWriter ipw, int k, String name);
    public abstract void register(IndentPrintWriter ipw, int k);
    public abstract void psGetValue(IndentPrintWriter ipw, int k, ClassContext cc);
    public abstract void psGet(IndentPrintWriter ipw, int k, String cName, ClassContext cc);
    public abstract void rsPull(IndentPrintWriter ipw, Integer k, String name);
    public Collection<String> requires() {
        return Set.of();
    }
}
