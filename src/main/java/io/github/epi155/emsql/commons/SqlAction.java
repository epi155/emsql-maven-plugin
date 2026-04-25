package io.github.epi155.emsql.commons;

import io.github.epi155.emsql.api.InvalidQueryException;
import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import io.github.epi155.emsql.api.SqlVectorType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static io.github.epi155.emsql.commons.Contexts.*;
import static io.github.epi155.emsql.commons.Tools.capitalize;
import static io.github.epi155.emsql.commons.Tools.getterOf;

@Getter
@NoArgsConstructor
@Setter
@Slf4j
public abstract class SqlAction {
    @NotNull
    private String execSql;
    /**
     * seconds
     */
    private Integer timeout;
    private boolean tune;

    public abstract JdbcStatement sql(Map<String, SqlDataType> fields) throws InvalidQueryException;

    public abstract void writeMethod(PrintModel pw, String methodName, JdbcStatement jdbc, String kPrg) throws InvalidQueryException;

    public void writeCode(PrintModel ipw, String kPrg) throws InvalidQueryException {
        JdbcStatement jdbc = sql(cc.getFields());
        String sQuery = jdbc.getText();
        cc.validate(sQuery, getClass(), jdbc.getIMap());
        jdbc.writeQuery(kPrg, ipw);
        customWrite(kPrg, ipw);
        /*-------------------------------------------------*/
        writeMethod(ipw, mc.getName(), jdbc, kPrg);
        /*-------------------------------------------------*/

        jdbc.getIMap().values().forEach(it -> cc.addAll(it.getType().requires()));
        jdbc.getOMap().values().forEach(it -> cc.addAll(it.getType().requires()));

    }

    protected void customWrite(String kPrg, PrintModel ipw) {
        // nope
    }

    public void declareInput(PrintModel ipw, @NotNull JdbcStatement jdbc) {
        int nSize = mc.nSize();
        cc.newLine(ipw, tune);
        if (tune) {
            cc.declareTuner(ipw);
        }
        if (1 <= nSize && nSize <= IMAX) {
            jdbc.getNMap().forEach((name, type) -> {
                ipw.commaLn();
                if (type instanceof SqlVectorType && ((SqlVectorType) type).columns() > 1) {
                    cc.add("java.util.List");
                    ipw.printf("        final List<%s> %s", ((SqlVectorType) type).getGeneric(), name);
                } else {
                    ipw.printf("        final %s %s", type.getPrimitive(), name);
                }
            });
        } else if (nSize > IMAX) {
            ipw.commaLn();
            ipw.printf("        final I i");
        }
    }
    public int pushInput(PrintModel ipw, @NotNull JdbcStatement jdbc, int argc) {
        int nSize = mc.nSize();
        if (1 <= nSize && nSize <= IMAX) {
            for(Map.Entry<String, SqlDataType> ee: jdbc.getNMap().entrySet()) {
                if (argc++ > 0) ipw.commaLn(); else ipw.println();
                ipw.printf("        %s", ee.getKey());
            }
        } else if (nSize > IMAX) {
            if (argc++ > 0) ipw.commaLn(); else ipw.println();
            ipw.printf("        i");
        }
        return argc;
    }

    public void plainGenericsNew(PrintModel ipw, JdbcStatement jdbc) {
        int nSize = mc.nSize();
        if (nSize == 0) {
            throw new IllegalArgumentException("Batch operation without arguments");
        } else if (isUnboxRequest(nSize)) {
            ipw.putf("%d<%s>", nSize, jdbc.getNMap().values().stream().map(SqlDataType::getWrapper).collect(Collectors.joining(", ")));
        } else {
            ipw.putf("1<I>");
        }
    }

    public void genericsNew(PrintModel ipw) {
        int nSize = mc.nSize();
        if (nSize == 0) {
            throw new IllegalArgumentException("Batch operation without arguments");
        } else if (!isUnboxRequest(nSize)) {
            ipw.putf("<I>");
        }
    }

    public void declareNextClass(
            PrintModel ipw,
            String cName,
            String eSqlObject,
            JdbcStatement jdbc,
            int batchSize,
            String kPrg) throws InvalidQueryException {
        ipw.printf("public static class %s", cName);
        String iName = cc.inPrepare(cName, jdbc.getIMap().values(), mc);
        batchGenerics(ipw, iName);
        ipw.putf(" extends %s", eSqlObject);
        plainGenericsNew(ipw, jdbc);
        ipw.putf("{%n");
        ipw.more();
        ipw.printf("protected %s(PreparedStatement ps) {%n", cName);
        ipw.more();
        ipw.printf("super(Q_%s, ps, %d);%n", kPrg, batchSize);
        ipw.ends();
    }

    public void declareInputBatch(PrintModel ipw, @NotNull JdbcStatement jdbc) {
        int nSize = mc.nSize();
        if (nSize == 0) {
            throw new IllegalArgumentException("Batch operation without arguments");
        } else if (isUnboxRequest(nSize)) {
            AtomicInteger k = new AtomicInteger();
            jdbc.getNMap().forEach((name, type) -> {
                ipw.printf("        final %s %s", type.getWrapper(), name);
                if (k.incrementAndGet() < nSize) ipw.commaLn();
            });
        } else {
            ipw.printf("        final I i");
        }
    }

    public void declareOutput(PrintModel ipw) {
        if (mc.oSize() < 2) {
            ipw.putf(")%n");
        } else {
            ipw.commaLn();
            ipw.printf("        final %s<O> so)%n", cc.supplier());
        }
        ipw.printf("        throws SQLException {%n");
    }

    public void declareOutputPlain(PrintModel ipw) {
        if (mc.oSize() < 2) {
            ipw.putf(") {%n");
        } else {
            ipw.commaLn();
            ipw.printf("        final %s<O> so) {%n", cc.supplier());
        }
    }

    public void declareOutputUse(@NotNull PrintModel ipw, String type) {
        ipw.commaLn();
        if (mc.oSize() > 1) {
            ipw.printf("        %s<O> so,%n", cc.supplier());
            ipw.printf("        %s<O> co)%n", cc.consumer());
        } else {
            ipw.printf("        %s<%s> co)%n", cc.consumer(), type);
        }
        ipw.printf("        throws SQLException {%n");
    }

    public void fetch(PrintModel ipw, @NotNull Map<Integer, SqlOutParam> oMap) {
        val eol = new Eol(mc.oSize());
        if (oMap.size() > 1) {
            ipw.printf("O o = so.get();%n");
            oMap.forEach((k, s) -> s.fetchParameter(ipw, k));
            if (cc.isDebug()) {
                ipw.printf("if (log.isTraceEnabled()) {%n");
                ipw.more();
                ipw.printf("SqlTrace.showResult(%n");
                ipw.more();
                oMap.forEach((k, v) -> ipw.printf("new SqlArg(\"%s\", \"%s\", o%d)%s%n",
                        v.getName(), v.getType().getPrimitive(), k, eol.nl()));
                ipw.less();
                ipw.printf(");%n");
                ipw.ends();
            }
        } else {
            oMap.forEach((k, v) -> v.fetchValue(ipw, k));
            if (cc.isDebug()) {
                ipw.printf("if (log.isTraceEnabled()) {%n");
                ipw.more();
                ipw.printf("SqlTrace.showResult(%n");
                ipw.more();
                oMap.forEach((k, v) -> ipw.printf("new SqlArg(\"%s\", \"%s\", o)%s%n",
                        v.getName(), v.getType().getPrimitive(), eol.nl()));
                ipw.less();
                ipw.printf(");%n");
                ipw.ends();
            }
        }
    }

    public void getOutput(PrintModel ipw, @NotNull Map<Integer, SqlOutParam> oMap) {
        val eol = new Eol(mc.oSize());
        if (oMap.size() > 1) {
            ipw.printf("O o = so.get();%n");
            oMap.forEach((k, s) -> s.getParameter(ipw, k));
            if (cc.isDebug()) {
                ipw.printf("if (log.isTraceEnabled()) {%n");
                ipw.more();
                ipw.printf("SqlTrace.showResult(%n");
                ipw.more();
                oMap.forEach((k, v) -> ipw.printf("new SqlArg(\"%s\", \"%s\", o%d)%s%n",
                        v.getName(), v.getType().getPrimitive(), k, eol.nl()));
                ipw.less();
                ipw.printf(");%n");
                ipw.ends();
            }
        } else {
            oMap.forEach((k, v) -> v.getValue(ipw, k)); // once
            if (cc.isDebug()) {
                ipw.printf("if (log.isTraceEnabled()) {%n");
                ipw.more();
                ipw.printf("SqlTrace.showResult(%n");
                ipw.more();
                oMap.forEach((k, v) -> ipw.printf("new SqlArg(\"%s\", \"%s\", o)%s%n",
                        v.getName(), v.getType().getPrimitive(), eol.nl()));
                ipw.less();
                ipw.printf(");%n");
                ipw.ends();
            }
        }
    }

    public void setInput(@NotNull PrintModel ipw, @NotNull JdbcStatement jdbc) {
        ipw.printf("int ki=0;%n");
        int nSize = mc.nSize();
        Map<Integer, SqlParam> iMap = jdbc.getIMap();
        if (1 <= nSize && nSize <= IMAX) {
            iMap.forEach((k, s) -> s.setValue(ipw));
        } else {
            iMap.forEach((k, s) -> s.setParameter(ipw));
        }
    }

    public void setInputAbs(@NotNull PrintModel ipw, @NotNull JdbcStatement jdbc) {
        int nSize = mc.nSize();
        Map<Integer, SqlParam> iMap = jdbc.getIMap();
        if (isUnboxRequest(nSize)) {
            iMap.forEach((k, s) -> s.setValue(ipw, k));
        } else {
            iMap.forEach((k, s) -> s.setParameter(ipw, k));
        }
    }

    public void registerOutAbs(PrintModel ipw, @NotNull Map<Integer, SqlOutParam> oMap) {
        oMap.forEach((k, s) -> s.registerOutParms(ipw, k));
    }

    /**
     * @param size input parameter number
     * @return {@code true} unbox parameters, {@code false} boxed parameters
     */
    public boolean isUnboxRequest(int size) {
        return size <= IMAX;
    }

    public void docInput(PrintModel ipw, @NotNull JdbcStatement jdbc) {
        int nSize = mc.nSize();
        if (1 <= nSize && nSize <= IMAX) {
            AtomicInteger count = new AtomicInteger();
            jdbc.getNMap().forEach((name, type) -> ipw.printf(" * @param %s :%1$s (parameter #%d)%n", name, count.incrementAndGet()));
        } else if (nSize > IMAX) {
            ipw.printf(" * @param i input parameter wrapper%n");
        }
    }

    public void docOutput(PrintModel ipw, @NotNull Map<Integer, SqlOutParam> oMap) {
        if (oMap.size() > 1) {
            ipw.printf(" * @param so output constructor%n");
        }
    }

    public void docOutputUse(PrintModel ipw, @NotNull Map<Integer, SqlOutParam> oMap) {
        if (oMap.size() > 1) {
            ipw.printf(" * @param so output constructor%n");
        }
        ipw.printf(" * @param co output consumer%n");
    }

    public void docEnd(@NotNull PrintModel ipw) {
        ipw.printf(" * @throws SQLException SQL error%n");
        ipw.printf(" */%n");
    }

    public void declareGenerics(PrintModel ipw, List<String> inFlds, String iName, String oName) {
        if (mc.oSize() <= 1) {
            if (mc.nSize() > IMAX) {
                ipw.putf("<I extends %s", iName);
                if (!inFlds.isEmpty())
                    genericInner(ipw, inFlds);
                ipw.putf("> ");
            } else {
                genericIn(ipw, inFlds, "<", "> ");
            }
        } else {
            if (mc.nSize() <= IMAX) {
                ipw.putf("<O extends %s", oName);
                genericIn(ipw, inFlds, ",", null);
                ipw.putf("> ");
            } else {
                ipw.putf("<I extends %1$s", iName);
                if (!inFlds.isEmpty())
                    genericInner(ipw, inFlds);
                ipw.putf(",O extends %1$s> ", oName);
            }
        }
    }

    public void declareGenerics(PrintModel ipw, TensorArgument inFlds, String iName, String oName) {
        if (mc.oSize() <= 1) {
            if (mc.nSize() > IMAX) {
                ipw.putf("<I extends %s", iName);
                if (inFlds.isPresent())
                    genericInner(ipw, inFlds);
                ipw.putf("> ");
            } else {
                genericIn(ipw, inFlds.getAll(), "<", "> ");
            }
        } else {
            if (mc.nSize() <= IMAX) {
                ipw.putf("<O extends %s", oName);
                genericIn(ipw, inFlds.getAll(), ",", null);
                ipw.putf("> ");
            } else {
                ipw.putf("<I extends %1$s", iName);
                if (inFlds.isPresent())
                    genericInner(ipw, inFlds);
                ipw.putf(",O extends %1$s> ", oName);
            }
        }
    }

    public void useGenerics(PrintModel ipw, TensorArgument inFlds) {
        if (mc.oSize() <= 1) {
            if (mc.nSize() > IMAX) {
                ipw.putf("<I");
                if (inFlds.isPresent()) {
//                    useInner(ipw, inFlds);
                    addInner(ipw, inFlds.getAll());
                }
                ipw.putf(">");
            } else {
                useIn(ipw, inFlds.getAll(), "<", ">");
            }
        } else {
            if (mc.nSize() <= IMAX) {
                ipw.putf("<O");
                useIn(ipw, inFlds.getAll(), ",", null);
                ipw.putf(">");
            } else {
                ipw.putf("<I");
                if (inFlds.isPresent())
                    addInner(ipw, inFlds.getAll());
                ipw.putf(",O>");
            }
        }
    }

    public void batchGenerics(PrintModel ipw, String iName) {
        if (!isUnboxRequest(mc.nSize())) {
            ipw.putf("<I extends %s", iName);
            ipw.putf("> ");
        }
    }

    public void batchGeneric(PrintModel ipw) {
        if (mc.nSize() > IMAX) {
            ipw.putf("<I");
            ipw.putf(">");
        }
    }

    private void genericInner(PrintModel ipw, List<String> flds) {
        ipw.putf("<");
        for (int k = 1; k <= flds.size(); k++) {
            if (k > 1) ipw.putf(",");
            ipw.putf("L%d", k);
        }
        ipw.putf(">");
        int k = 0;
        for (val fld : flds) {
            ipw.putf(",L%d extends %s%s", ++k, capitalize(fld), REQUEST);
        }
    }
    private void genericInner(PrintModel ipw, TensorArgument flds) {
        if (flds.haveStatic()) {
            ipw.putf("<");
            for (int k = 1; k <= flds.staticSize(); k++) {
                if (k > 1) ipw.putf(",");
                ipw.putf("L%d", k);
            }
            ipw.putf(">");
        }
        int k = 0;
        for (val fld : flds.getAll()) {
            ipw.putf(",L%d extends %s%s", ++k, capitalize(fld), REQUEST);
        }
    }

    private void addInner(PrintModel ipw, List<String> flds) {
        int k = 0;
        for (val ignored : flds) {
            ipw.putf(",L%d", ++k);
        }
    }

    private void genericIn(PrintModel ipw, List<String> inFlds, String prefix, String suffix) {
        if (!inFlds.isEmpty()) {
            int k = 0;
            if (prefix != null) ipw.putf(prefix);
            for (String inFld : inFlds) {
                if (k++ > 0) ipw.putf(",");
                ipw.putf("L%d extends %s" + REQUEST, k, capitalize(inFld));
            }
            if (suffix != null) ipw.putf(suffix);
        }
    }

    private void useIn(PrintModel ipw, List<String> inFlds, String prefix, String suffix) {
        if (!inFlds.isEmpty()) {
            int k = 0;
            if (prefix != null) ipw.putf(prefix);
            for (String ignored : inFlds) {
                if (k++ > 0) ipw.putf(",");
                ipw.putf("L%d", k);
            }
            if (suffix != null) ipw.putf(suffix);
        }
    }

    public void debugQuery(PrintModel ipw, String kPrg) {
        if (cc.isDebug()) {
            ipw.printf("log.debug(\"BatchQuery: {}\", Q_%s);%n", kPrg);
        }
    }

    public void debugAction(PrintModel ipw, String kPrg, JdbcStatement jdbcStatement) {
        if (cc.isDebug()) {
            cc.add("io.github.epi155.emsql.runtime.SqlArg");
            int nSize = mc.nSize();
            ipw.printf("if (log.isDebugEnabled()) {%n");
            ipw.more();
            ipw.printf("SqlTrace.showQuery(Q_%s, ", kPrg);
            cc.traceParameterBegin(ipw);
            ipw.more();
            ipw.printf("return new SqlArg[]{%n");
            ipw.more();
            val eol = new Eol(mc.iSize());
            if (isUnboxRequest(nSize)) {
                jdbcStatement.getIMap().forEach((k, v) ->
                        ipw.printf("new SqlArg(\"%1$s\", \"%2$s\", %1$s)%3$s%n", v.getName(), v.getType().getPrimitive(), eol.nl()));
            } else {
                Map<Integer, SqlParam> iMap = jdbcStatement.getIMap();
                iMap.forEach((k, v) ->
                        ipw.printf("new SqlArg(\"%s\", \"%s\", i.%s())%s%n",
                                v.getName(), v.getType().getPrimitive(), getterOf(v), eol.nl()));
            }
            ipw.less();
            ipw.printf("};%n");
            cc.traceParameterEnds(ipw);
            ipw.printf("});%n");
            ipw.ends();
        }
    }
    public void dumpAction(PrintModel ipw, String kPrg, JdbcStatement jdbcStatement) {
        if (cc.isDebug()) {
            cc.add("io.github.epi155.emsql.runtime.SqlArg");
            int nSize = mc.nSize();
            ipw.less();
            ipw.printf("} catch (SQLException e) {%n");
            ipw.more();
            ipw.printf("SqlTrace.showCause(e, Q_%s, ", kPrg);
            cc.traceParameterBegin(ipw);
            ipw.more();
            ipw.printf("return new SqlArg[]{%n");
            ipw.more();
            val eol = new Eol(mc.iSize());
            if (isUnboxRequest(nSize)) {
                jdbcStatement.getIMap().forEach((k, v) ->
                        ipw.printf("new SqlArg(\"%1$s\", \"%2$s\", %1$s)%3$s%n", v.getName(), v.getType().getPrimitive(), eol.nl()));
            } else {
                Map<Integer, SqlParam> iMap = jdbcStatement.getIMap();
                iMap.forEach((k, v) ->
                        ipw.printf("new SqlArg(\"%s\", \"%s\", i.%s())%s%n",
                                v.getName(), v.getType().getPrimitive(), getterOf(v), eol.nl()));
            }
            ipw.less();
            ipw.printf("};%n");
            cc.traceParameterEnds(ipw);
            ipw.printf("});%n");
            ipw.printf("throw e;%n");
        }
        ipw.ends(); // ends try () { }
    }

    public void setQueryHints(PrintModel ipw) {
        if (timeout != null) ipw.printf("ps.setQueryTimeout(%d);%n", timeout);
        if (tune) ipw.printf("u.accept(new SqlStmtSetImpl(ps));%n");
    }

    public Map<Integer, SqlMulti> notScalar(@NotNull Map<Integer, SqlParam> parameters) {
        Map<Integer, SqlMulti> notScalar = new LinkedHashMap<>();
        parameters.forEach((k, v) -> {
            if (v.getType() instanceof SqlVectorType) {
                notScalar.put(k, new SqlMulti(v.getName(), (SqlVectorType) v.getType()));
            }
        });
        return notScalar;
    }

    public void expandIn(@NotNull PrintModel ipw, @NotNull Map<Integer, SqlMulti> notScalar, String kPrg) {
        cc.add(ClassContextImpl.RUNTIME_EMSQL);
        ipw.printf("final String query = EmSQL.expandQueryParameters(Q_%s%n", kPrg);
        ipw.more();
        if (mc.nSize() <= IMAX) {
            notScalar.forEach((k, v) -> ipw.printf(", new EmSQL.Mul(%d, %s.size(), %d)%n", k, v.getName(), v.getType().columns()));
        } else {
            notScalar.forEach((k, v) -> ipw.printf(", new EmSQL.Mul(%d, i.%s().size(), %d)%n", k, getterOf(v), v.getType().columns()));
        }
        ipw.less();
        ipw.printf(");%n", kPrg);
    }

    public void openQuery(PrintModel ipw, JdbcStatement jdbc, String kPrg) {
        Map<Integer, SqlMulti> notScalar = notScalar(jdbc.getIMap());
        if (notScalar.isEmpty()) {
            ipw.printf("try (PreparedStatement ps = c.prepareStatement(Q_%s)) {%n", kPrg);
        } else {
            expandIn(ipw, notScalar, kPrg);
            ipw.printf("try (PreparedStatement ps = c.prepareStatement(query)) {%n");
        }

    }

    public static class Eol {
        private int count;

        public Eol(int size) {
            this.count = size;
        }

        public String nl() {
            return --count > 0 ? "," : "";
        }
    }

}
