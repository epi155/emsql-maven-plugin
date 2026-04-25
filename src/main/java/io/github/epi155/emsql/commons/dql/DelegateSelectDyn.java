package io.github.epi155.emsql.commons.dql;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import io.github.epi155.emsql.api.SqlVectorType;
import io.github.epi155.emsql.commons.*;
import lombok.val;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static io.github.epi155.emsql.commons.ClassContextImpl.RUNTIME_EMSQL;
import static io.github.epi155.emsql.commons.Contexts.*;
import static io.github.epi155.emsql.commons.JdbcStatement.filterNotScalar;
import static io.github.epi155.emsql.commons.Tools.getterOf;

public class DelegateSelectDyn {
    private final ApiSelectDyn api;

    public DelegateSelectDyn(ApiSelectDyn api) {
        this.api = api;
    }

    public void customWrite(@NotNull PrintModel ipw, String kPrg) {
        if (api.getOptionalAnd().isEmpty())
            return;
        ipw.printf("private static final Map<String,String> Q_%s_OPT_MAP;%n", kPrg);
        int k = 0;
        for (String key : api.getOptionalAnd().keySet()) {
            ipw.printf("private static final String Q_%s_OPT%04d = \"%s\";%n", kPrg, ++k, StringEscapeUtils.escapeJava(key));
        }
        k = 0;
        ipw.printf("static {%n");
        ipw.more();
        ipw.printf("Q_%s_OPT_MAP = new LinkedHashMap<>();%n", kPrg);
        for (Map.Entry<String, String> entry : api.getOptionalAnd().entrySet()) {
            // :parm -> ?
            Tools.SqlStatement iStmt = Tools.replacePlaceholder(entry.getValue(), cc.getFields(), true);
            ipw.printf("Q_%s_OPT_MAP.put(Q_%s_OPT%04d, \"%s\");%n", kPrg, kPrg, ++k, StringEscapeUtils.escapeJava(iStmt.getText()));
            api.getAndParms().put(entry.getKey(), iStmt.getMap());
        }
        ipw.ends();
        cc.add("java.util.Map");
        cc.add("java.util.LinkedHashMap");
    }

    public void debugAction(PrintModel ipw, String kPrg, JdbcStatement jdbcStatement) {
        if (cc.isDebug()) {
            cc.add("io.github.epi155.emsql.runtime.SqlArg");
            cc.add("java.util.List");
            cc.add("java.util.ArrayList");
//            ipw.more();
            ipw.printf("SqlTrace.showQuery(query, ");
            cc.traceParameterBegin(ipw);
            ipw.more();
            ipw.printf("List<SqlArg> args = new ArrayList<>();%n");
            debugInpStd(ipw, jdbcStatement);
            debugInpOpt(ipw, kPrg);

            ipw.printf("return args.toArray(new SqlArg[0]);%n");
            cc.traceParameterEnds(ipw);
            ipw.printf("});%n");
//            ipw.less();
        }
    }

    public void dumpAction(PrintModel ipw, String kPrg, JdbcStatement jdbcStatement) {
        if (cc.isDebug()) {
            cc.add("io.github.epi155.emsql.runtime.SqlArg");
            cc.add("java.util.List");
            cc.add("java.util.ArrayList");
            ipw.less();
            ipw.printf("} catch (SQLException e) {%n");
            ipw.more();
            ipw.printf("SqlTrace.showCause(e, query, ");
            cc.traceParameterBegin(ipw);
            ipw.more();
            ipw.printf("List<SqlArg> args = new ArrayList<>();%n");
            debugInpStd(ipw, jdbcStatement);
            debugInpOpt(ipw, kPrg);

            ipw.printf("return args.toArray(new SqlArg[0]);%n");
            cc.traceParameterEnds(ipw);
            ipw.printf("});%n");
            ipw.printf("throw e;%n");
//            ipw.ends();
        }
    }

    private void debugInpStd(PrintModel ipw, JdbcStatement jdbcStatement) {
        int nSize = mc.nSize();
        if (api.isUnboxRequest(nSize)) {
            jdbcStatement.getIMap().forEach((k, v) ->
                    ipw.printf("args.add(new SqlArg(\"%1$s\", \"%2$s\", %1$s));%n", v.getName(), v.getType().getPrimitive()));
        } else {
            Map<Integer, SqlParam> iMap = jdbcStatement.getIMap();
            iMap.forEach((k, v) ->
                    ipw.printf("args.add(new SqlArg(\"%s\", \"%s\", i.%s()));%n",
                            v.getName(), v.getType().getPrimitive(), getterOf(v)));
        }
    }

    private void debugInpOpt(PrintModel ipw, String kPrg) {
        int k = 0;
        for (Map.Entry<String, Map<Integer, SqlParam>> e : api.getAndParms().entrySet()) {
            k++;
            if (e.getValue().isEmpty()) continue;
            ipw.printf("if (options.contains(Q_%s_OPT%04d)) {%n", kPrg, k);
            ipw.more();
            for (Map.Entry<Integer, SqlParam> p : e.getValue().entrySet()) {
                SqlParam v = p.getValue();
                ipw.printf("args.add(new SqlArg(\"%s\", \"%s\", opt%dp%d));%n",
                        v.getName(), v.getType().getPrimitive(), k, p.getKey());
            }
            ipw.ends();
        }
    }

    public void setOptInput(PrintModel ipw, String kPrg) {
        int k = 0;
        for (Map.Entry<String, Map<Integer, SqlParam>> e : api.getAndParms().entrySet()) {
            k++;
            if (e.getValue().isEmpty()) continue;
            ipw.printf("if (options.contains(Q_%s_OPT%04d)) {%n", kPrg, k);
            ipw.more();
            for (Map.Entry<Integer, SqlParam> p : e.getValue().entrySet()) {
                String src = String.format("opt%dp%d", k, p.getKey());
                p.getValue().getType().psSet(ipw, src);
            }
            ipw.ends();
        }
    }

    public void expandIn(@NotNull PrintModel ipw, @NotNull Map<Integer, SqlMulti> notScalar, String kPrg) {
        cc.add(RUNTIME_EMSQL);
        ipw.printf("final String queryAnte = EmSQL.expandQueryParameters(Q_%s_ANTE%n", kPrg);
        ipw.more();
        if (mc.nSize() <= IMAX) {
            notScalar.forEach((k, v) -> ipw.printf(", new EmSQL.Mul(%d, %s.size(), %d)%n", k, v.getName(), v.getType().columns()));
        } else {
            notScalar.forEach((k, v) -> ipw.printf(", new EmSQL.Mul(%d, i.%s().size(), %d)%n", k, getterOf(v), v.getType().columns()));
        }
        ipw.less();
        ipw.printf(");%n", kPrg);
    }

    public void defineMethodArgBuilder(PrintModel ipw, String kPrg, String cName, Runnable usedGenerics) {
        int k = 1;
        for (Map.Entry<String, String> a : api.getOptionalAnd().entrySet()) {
            Map<Integer, SqlParam> parms = api.getAndParms().get(a.getKey());
            Map<OptArg, List<Integer>> args = reorgParms(parms, k);
            docMethod(ipw, a, k, cName, args);
            ipw.printf("public %sBuilder", cName);
            usedGenerics.run();
            ipw.putf(" %s(", a.getKey());
            writeArgument(ipw, args);
            ipw.putf(") {%n");
            ipw.more();
            writeAssign(ipw, args);
            ipw.printf("options.add(Q_%s_OPT%04d);%n", kPrg, k);
            ipw.printf("return this;%n");
            ipw.ends();
            k++;
        }
    }

    private Map<OptArg, List<Integer>> reorgParms(Map<Integer, SqlParam> parms, int k) {
        Map<OptArg, List<Integer>> args = new LinkedHashMap<>();
        if (parms.isEmpty()) return args;
        for (Map.Entry<Integer, SqlParam> e : parms.entrySet()) {
            SqlParam qParm = e.getValue();
            String name = qParm.getName();
            OptArg oa = new OptArg(name, k);

            List<Integer> aLst = args.computeIfAbsent(oa, ka -> {
                reorgName(ka, args);    // set simpleName, ord
                ka.setSql(qParm.getType());
                return new ArrayList<>();
            });

            aLst.add(e.getKey());
        }
        return args;
    }

    private void docMethod(PrintModel ipw, Map.Entry<String, String> a, int k, String cName, Map<OptArg, List<Integer>> args) {
        ipw.printf("/**%n");
        ipw.printf(" * Add condition <b>%s</b> (#%d)%n", a.getKey(), k);
        ipw.printf(" * <pre>%n");
        val lines = ("AND " + a.getValue()).split("\n");
        for (val line : lines) {
            ipw.printf(" * %s%n", line);
        }
        ipw.printf(" * </pre>%n");
        ipw.printf(" *%n");

        for(Map.Entry<OptArg, List<Integer>> arg: args.entrySet()) {
            ipw.printf(" * @param %s %s (parameter #%s)%n", arg.getKey().normalizedName(), arg.getKey().getName(), listOf(arg.getValue()));
        }

        ipw.printf(" * @return %sBuilder%n", cName);
        ipw.printf(" */%n");

    }

    private String listOf(List<Integer> values) {
        if (values==null || values.isEmpty()) return "[]";
        if (values.size()==1) return String.valueOf(values.get(0));
        StringBuilder sb=new StringBuilder("[");
        int k=0;
        for (int value: values) {
            if (k++ > 0) sb.append(",");
            sb.append(value);
        }
        sb.append("]");
        return sb.toString();
    }

    private void writeArgument(PrintModel ipw, Map<OptArg, List<Integer>> parms) {
        if (parms.isEmpty()) return;
        ipw.println();
        ipw.commaReset();
        for (OptArg e : parms.keySet()) {
            ipw.commaLn();
            ipw.printf("        final %s %s", e.getSql().getPrimitive(), e.normalizedName());
        }
    }

    private void reorgName(OptArg oa, Map<OptArg, List<Integer>> pArg) {
        String name = Tools.normalizeName(oa.getName());
        oa.setSimpleName(name);
        int k=1;
        for(OptArg p: pArg.keySet()) {
            if (name.equals(p.getSimpleName())) k++;
        }
        oa.setOrd(k);
    }

    private void writeAssign(PrintModel ipw, Map<OptArg, List<Integer>> pArg) {
        for (Map.Entry<OptArg, List<Integer>> a : pArg.entrySet()) {
            for (int i : a.getValue()) {
                ipw.printf("this.opt%dp%d = %s;%n", a.getKey().getNdx(), i, a.getKey().normalizedName());
            }
        }
    }

    public void defineInput(PrintModel ipw, JdbcStatement jdbc) {
        int nSize = mc.nSize();
        if (1 <= nSize && nSize <= IMAX) {
            jdbc.getNMap().forEach((name, type) -> {
                if (type instanceof SqlVectorType && ((SqlVectorType) type).columns() > 1) {
                    ipw.printf("private final List<%s> %s;%n", ((SqlVectorType) type).getGeneric(), name);
                } else {
                    ipw.printf("private final %s %s;%n", type.getPrimitive(), name);
                }
            });
        } else if (nSize > IMAX) {
            ipw.printf("private final I i;%n");
        }
    }

    public void defineOutput(PrintModel ipw) {
        if (mc.oSize() >= 2) {
            ipw.printf("private final %s<O> so;%n", cc.supplier());
        }
    }

    public void defineOptional(PrintModel ipw) {
        int k = 1;
        cc.add("java.util.Set");
        cc.add("java.util.HashSet");
        ipw.printf("private final Set<String> options = new HashSet<>();%n");
        for (Map.Entry<String, Map<Integer, SqlParam>> a : api.getAndParms().entrySet()) {
            for (Map.Entry<Integer, SqlParam> e : a.getValue().entrySet()) {
                ipw.printf("private %s opt%dp%d; // %s%n",
                        e.getValue().getType().getPrimitive(), k, e.getKey(), e.getValue().getName());
            }
            k++;
        }
    }

    public void assignInput(PrintModel ipw, JdbcStatement jdbc) {
        int nSize = mc.nSize();
        if (1 <= nSize && nSize <= IMAX) {
            jdbc.getNMap().forEach((name, type) ->
                    ipw.printf("this.%1$s = %1$s;%n", name));
        } else if (nSize > IMAX) {
            ipw.printf("this.i = i;%n");
        }
    }

    public void assignOutput(PrintModel ipw) {
        if (mc.oSize() >= 2) {
            ipw.printf("this.so = so;%n");
        }
    }

    public void declareInput(PrintModel ipw, @NotNull JdbcStatement jdbc) {
        int nSize = mc.nSize();
        if (1 <= nSize && nSize <= IMAX) {
            jdbc.getNMap().forEach((name, type) -> {
                ipw.commaLn();
                if (type instanceof SqlVectorType && ((SqlVectorType) type).columns() > 1) {
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

    public void declareOutput(PrintModel ipw) {
        if (mc.oSize() < 2) {
            ipw.putf(") {%n");
        } else {
            ipw.commaLn();
            ipw.printf("        final %s<O> so) {%n", cc.supplier());
        }
    }

    public void docEnd(@NotNull PrintModel ipw) {
        String cName = Tools.capitalize(mc.getName());
        ipw.printf(" * @return %sBuilder%n", cName);
        ipw.printf(" */%n");
    }


    public void writeOpenCode(PrintModel ipw, JdbcStatement jdbc, String kPrg) {
        Map<Integer, SqlMulti> notScalar = api.notScalar(jdbc.getIMap());
        if (notScalar.isEmpty()) {
            ipw.printf("String query = EmSQL.buildQuery(Q_%1$s_ANTE, Q_%1$s_OPT_MAP, Q_%1$s_POST, options);%n", kPrg);
        } else {
            expandIn(ipw, notScalar, kPrg);
            ipw.printf("String query = EmSQL.buildQuery(queryAnte, Q_%1$s_OPT_MAP, Q_%1$s_POST, options);%n", kPrg);
        }

        debugAction(ipw, kPrg, jdbc);

        if (cc.isDebug()) {
            ipw.printf("try {%n");   // trap open
            ipw.more();
        }
        ipw.printf("this.ps = c.prepareStatement(query);%n");
        api.setInput(ipw, jdbc);
        setOptInput(ipw, kPrg);
        if (api.getFetchSize() != null) ipw.printf("ps.setFetchSize(%d);%n", api.getFetchSize());
        api.setQueryHints(ipw);
        ipw.printf("this.rs = ps.executeQuery();%n");

        if (cc.isDebug()) {
            dumpAction(ipw, kPrg, jdbc);    // catch trap open
            ipw.ends(); // end trap open
        }
        ipw.ends(); // end ctor

        // hasNext
        ipw.printf("@Override%n");
        ipw.printf("public boolean hasNext() throws SQLException {%n");
        ipw.more();
        ipw.printf("return rs.next();%n");
        ipw.ends();

        // fetchNext
        ipw.printf("@Override%n");
        Map<Integer, SqlOutParam> oMap = jdbc.getOMap();
        int oSize = oMap.size();
        if (oSize == 1) {
            String oType = oMap.get(1).getType().getWrapper();
            ipw.printf("public %s fetchNext() throws SQLException {%n", oType);
        } else {
            ipw.printf("public O fetchNext() throws SQLException {%n");
        }

        ipw.more();
        api.fetch(ipw, jdbc.getOMap());
        ipw.printf("return o;%n");
        ipw.ends();

        // close
        ipw.printf("@Override%n");
        ipw.printf("public void close() throws SQLException {%n");
        ipw.more();
        ipw.printf("if (rs != null) rs.close();%n");
        ipw.printf("ps.close();%n");
        ipw.ends();

        ipw.less(); ipw.printf("};%n");// end new SqlCursor<O>
        ipw.ends(); // end open()
    }

    public void writeForEachCode(PrintModel ipw, JdbcStatement jdbc, String kPrg) {
        Map<Integer, SqlMulti> notScalar = api.notScalar(jdbc.getIMap());
        if (notScalar.isEmpty()) {
            ipw.printf("String query = EmSQL.buildQuery(Q_%1$s_ANTE, Q_%1$s_OPT_MAP, Q_%1$s_POST, options);%n", kPrg);
        } else {
            expandIn(ipw, notScalar, kPrg);
            ipw.printf("String query = EmSQL.buildQuery(queryAnte, Q_%1$s_OPT_MAP, Q_%1$s_POST, options);%n", kPrg);
        }

        debugAction(ipw, kPrg, jdbc);

        ipw.printf("try (PreparedStatement ps = c.prepareStatement(query)) {%n");
        ipw.more();
        api.setInput(ipw, jdbc);
        setOptInput(ipw, kPrg);
        if (api.getFetchSize() != null) ipw.printf("ps.setFetchSize(%d);%n", api.getFetchSize());
        api.setQueryHints(ipw);
        ipw.printf("try (ResultSet rs = ps.executeQuery()) {%n");
        ipw.more();
        ipw.printf("while (rs.next()) {%n");
        ipw.more();
        api.fetch(ipw, jdbc.getOMap());
        ipw.printf("co.accept(o);%n");
        ipw.ends(); // end while
        ipw.ends(); // end try (ResultSet rs)
        dumpAction(ipw, kPrg, jdbc);    // catch try (PreparedStatement ps ...XXX
        ipw.ends(); // end try (PreparedStatement ps)
        ipw.ends(); // end forEach
    }

    public void writeResultListCode(@NotNull PrintModel ipw, @NotNull JdbcStatement jdbc, String kPrg) {
        cc.add(RUNTIME_EMSQL);
        Map<Integer, SqlMulti> notScalar = api.notScalar(jdbc.getIMap());
        if (notScalar.isEmpty()) {
            ipw.printf("String query = EmSQL.buildQuery(Q_%1$s_ANTE, Q_%1$s_OPT_MAP, Q_%1$s_POST, options);%n", kPrg);
        } else {
            expandIn(ipw, notScalar, kPrg);
            ipw.printf("String query = EmSQL.buildQuery(queryAnte, Q_%1$s_OPT_MAP, Q_%1$s_POST, options);%n", kPrg);
        }

        debugAction(ipw, kPrg, jdbc);

        ipw.printf("try (PreparedStatement ps = c.prepareStatement(query)) {%n");
        ipw.more();
        api.setInput(ipw, jdbc);
        setOptInput(ipw, kPrg);
        if (api.getFetchSize() != null) ipw.printf("ps.setFetchSize(%d);%n", api.getFetchSize());
        api.setQueryHints(ipw);
        ipw.printf("try (ResultSet rs = ps.executeQuery()) {%n");
        ipw.more();
        if (mc.oSize() == 1) {
            jdbc.getOMap().forEach((k, v) ->
                    ipw.printf("List<%s> list = new ArrayList<>();%n", v.getType().getWrapper()));
        } else {
            ipw.printf("List<O> list = new ArrayList<>();%n");
        }
        ipw.printf("while (rs.next()) {%n");
        ipw.more();
        api.fetch(ipw, jdbc.getOMap());
        ipw.printf("list.add(o);%n");
        ipw.ends();
        ipw.printf("return list;%n");
        ipw.ends(); // end try (ResultSet rs)
        dumpAction(ipw, kPrg, jdbc);
        ipw.ends(); // end try (PreparedStatement ps)
        ipw.ends(); // end list()
    }

    public TensorArgument packGenerics(String name, Collection<Map<Integer, SqlParam>> optParams, List<String> fixParams) {
        Map<String, SqlDataType> elems = new HashMap<>();
        for (Map<Integer, SqlParam> ak: optParams) {
            Collection<SqlParam> params = ak.values();
            InterfacePS.registerTensor(name, params);
            for(SqlParam aj: params) {
                elems.put(aj.getName(), aj.getType());
            }
        }
        return new TensorArgument(fixParams, filterNotScalar(elems));
    }
}
