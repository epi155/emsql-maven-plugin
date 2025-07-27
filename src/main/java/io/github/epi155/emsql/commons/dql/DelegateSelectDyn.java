package io.github.epi155.emsql.commons.dql;

import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.commons.*;
import lombok.val;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static io.github.epi155.emsql.commons.Contexts.*;
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
        int k=0;
        for(String key: api.getOptionalAnd().keySet()) {
            ipw.printf("private static final String Q_%s_OPT%04d = \"%s\";%n", kPrg, ++k, StringEscapeUtils.escapeJava(key));
        }
        k=0;
        ipw.printf("static {%n");
        ipw.more();
        ipw.printf("Q_%s_OPT_MAP = new LinkedHashMap<>();%n", kPrg);
        for(Map.Entry<String, String> entry: api.getOptionalAnd().entrySet()) {
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
            int nSize = mc.nSize();
            ipw.printf("if (log.isDebugEnabled()) {%n");
            ipw.more();
            ipw.printf("SqlTrace.showQuery(query, ");
            cc.traceParameterBegin(ipw);
            ipw.more();
            ipw.printf("List<SqlArg> args = new ArrayList<>();%n");
            if ( api.isUnboxRequest(nSize)) {
                jdbcStatement.getIMap().forEach((k,v) ->
                        ipw.printf("args.add(new SqlArg(\"%1$s\", \"%2$s\", %1$s));%n", v.getName(), v.getType().getPrimitive()));
            } else {
                Map<Integer, SqlParam> iMap = jdbcStatement.getIMap();
                if (mc.isInputReflect()) {
                    iMap.forEach((k,v) ->
                            ipw.printf("args.add(new SqlArg(\"%1$s\", \"%2$s\", EmSQL.get(i, \"%1$s\", %3$s.class)));%n",
                                    v.getName(), v.getType().getPrimitive(), v.getType().getContainer()));
                } else if (mc.isInputDelegate()) {
                    iMap.forEach((k,v) ->
                            ipw.printf("args.add(new SqlArg(\"%1$s\", \"%2$s\", i.%1$s.get()));%n",
                                    v.getName(), v.getType().getPrimitive()));
                } else {
                    iMap.forEach((k,v) ->
                            ipw.printf("args.add(new SqlArg(\"%s\", \"%s\", i.%s()));%n",
                                    v.getName(), v.getType().getPrimitive(), getterOf(v)));
                }
            }
            int k=0;
            for(Map.Entry<String,Map<Integer, SqlParam>> e: api.getAndParms().entrySet()) {
                k++;
                if (e.getValue().isEmpty()) continue;
                ipw.printf("if (options.contains(Q_%s_OPT%04d)) {%n", kPrg, k);
                ipw.more();
                for(Map.Entry<Integer, SqlParam> p: e.getValue().entrySet()) {
                    SqlParam v = p.getValue();
                    ipw.printf("args.add(new SqlArg(\"%s\", \"%s\", opt%dp%d));%n",
                            v.getName(), v.getType().getPrimitive(), k, p.getKey());
                }
                ipw.ends();
            }

            ipw.printf("return args.toArray(new SqlArg[0]);%n");
            cc.traceParameterEnds(ipw);
            ipw.printf("});%n");
            ipw.ends();
        }
    }
    public void setOptInput(PrintModel ipw, String kPrg) {
        int k=0;
        for(Map.Entry<String,Map<Integer, SqlParam>> e: api.getAndParms().entrySet()) {
            k++;
            if (e.getValue().isEmpty()) continue;
            ipw.printf("if (options.contains(Q_%s_OPT%04d)) {%n", kPrg, k);
            ipw.more();
            for(Map.Entry<Integer, SqlParam> p: e.getValue().entrySet()) {
                String src = String.format("opt%dp%d", k, p.getKey());
                p.getValue().getType().psSet(ipw, src);
            }
            ipw.ends();
        }
    }

    public void expandIn(@NotNull PrintModel ipw, @NotNull Map<Integer, SqlParam> notScalar, String kPrg) {
        cc.add(ClassContextImpl.RUNTIME_EMSQL);
        ipw.printf("final String queryAnte = EmSQL.expandQueryParameters(Q_%s_ANTE%n", kPrg);
        ipw.more();
        if (mc.nSize()<=IMAX) {
            notScalar.forEach((k,v) -> ipw.printf(", new EmSQL.Mul(%d, %s.size(), %d)%n", k, v.getName(), v.getType().columns()));
        } else {
            if (mc.isInputReflect()) {
                cc.add("java.util.List");
                notScalar.forEach((k,v) -> ipw.printf(", new EmSQL.Mul(%d, EmSQL.get(i, \"%s\", List.class).size(), %d)%n", k, v.getName(), v.getType().columns()));
            } else if (mc.isInputDelegate()) {
                notScalar.forEach((k,v) -> ipw.printf(", new EmSQL.Mul(%d, i.%s.get().size(), %d)%n", k, v.getName(), v.getType().columns()));
            } else {
                notScalar.forEach((k,v) -> ipw.printf(", new EmSQL.Mul(%d, i.%s().size(), %d)%n", k, getterOf(v), v.getType().columns()));
            }
        }
        ipw.less();
        ipw.printf(");%n", kPrg);
    }

    public void defineMethodArgBuilder(PrintModel ipw, String kPrg, String cName) {
        int k=1;
        for(Map.Entry<String, String> a: api.getOptionalAnd().entrySet()) {
            docMethod(ipw, a, k, cName);
            ipw.printf("public %sBuilder<O> %s(", cName, a.getKey());
            Map<Integer, SqlParam> parms = api.getAndParms().get(a.getKey());
            Map<OptArg, List<Integer>> pArg = writeArgument(ipw, parms, k);
            ipw.putf(") {%n");
            ipw.more();
            writeAssign(ipw, pArg);
            ipw.printf("options.add(Q_%s_OPT%04d);%n", kPrg, k);
            ipw.printf("return this;%n");
            ipw.ends();
            k++;
        }
    }

    private void docMethod(PrintModel ipw, Map.Entry<String, String> a, int k, String cName) {
        ipw.printf("/**%n");
        ipw.printf(" * Add condition <b>%s</b> (#%d)%n", a.getKey(), k);
        ipw.printf(" * <pre>%n");
        val lines = ("AND " + a.getValue()).split("\n");
        for(val line: lines) {
            ipw.printf(" * %s%n", line);
        }
        ipw.printf(" * </pre>%n");
        ipw.printf(" *%n");

        Collection<SqlParam> parms = api.getAndParms().get(a.getKey()).values();
        Set<String> doubleCheck = new HashSet<>();
        int c=0;
        for(SqlParam p: parms) {
            String name = p.getName();
            if (!doubleCheck.contains(name)) {
                doubleCheck.add(name);
                ipw.printf(" * @param %1$s %1$s (parameter #%2$d)%n", name, ++c);
            }
        }
        ipw.printf(" * @return %sBuilder%n", cName);
        ipw.printf(" */%n");

    }

    private Map<OptArg, List<Integer>> writeArgument(PrintModel ipw, Map<Integer, SqlParam> parms, int k) {
        Map<OptArg, List<Integer>> pArg = new LinkedHashMap<>();
        if (parms.isEmpty()) return pArg;
        int n=0;
        ipw.println();
        for(Map.Entry<Integer, SqlParam> e: parms.entrySet()) {
            SqlParam qParm = e.getValue();
            String name = qParm.getName();
            OptArg oa = new OptArg(name, k);
            List<Integer> aLst = pArg.get(oa);
            if (aLst == null) {
                if (n++ > 0) ipw.commaLn();
                ipw.printf("        final %s %s", qParm.getType().getPrimitive(), name);
                aLst = new ArrayList<>();
                pArg.put(oa, aLst);
            }
            aLst.add(e.getKey());
        }
        return pArg;
    }

    private void writeAssign(PrintModel ipw, Map<OptArg, List<Integer>> pArg) {
        for(Map.Entry<OptArg, List<Integer>> a: pArg.entrySet()) {
            for(int i: a.getValue()) {
                ipw.printf("this.opt%dp%d = %s;%n", a.getKey().getNdx(), i, a.getKey().getName());
            }
        }
    }

    public void defineInput(PrintModel ipw, JdbcStatement jdbc) {
        int nSize = mc.nSize();
        if (1<=nSize && nSize<=IMAX) {
            jdbc.getNMap().forEach((name, type) -> {
                if (type.isScalar() || type.columns() <=1) {
                    ipw.printf("private final %s %s;%n", type.getPrimitive(), name);
                } else {
                    ipw.printf("private final List<%s> %s;%n", type.getGeneric(), name);
                }
            });
        } else if (nSize>IMAX){
            if (mc.isInputDelegate()) {
                ipw.printf("private final DI i;%n");
            } else {
                ipw.printf("private final I i;%n");
            }
        }
    }
    public void defineOutput(PrintModel ipw) {
        if (mc.oSize() >= 2) {
            if (mc.isOutputDelegate()) {
                ipw.printf("private final DO o;%n");
            } else {
                ipw.printf("private final %s<O> so;%n", cc.supplier());
            }
        }
    }
    public void defineOptional(PrintModel ipw) {
        int k=1;
        cc.add("java.util.Set");
        cc.add("java.util.HashSet");
        ipw.printf("private final Set<String> options = new HashSet<>();%n");
        for(Map.Entry<String,Map<Integer, SqlParam>> a: api.getAndParms().entrySet()) {
            for( Map.Entry<Integer, SqlParam> e: a.getValue().entrySet()) {
                ipw.printf("private %s opt%dp%d; // %s%n",
                        e.getValue().getType().getPrimitive(), k, e.getKey(), e.getValue().getName());
            }
            k++;
        }
    }

    public void assignInput(PrintModel ipw, JdbcStatement jdbc) {
        int nSize = mc.nSize();
        if (1<=nSize && nSize<=IMAX) {
            jdbc.getNMap().forEach((name, type) ->
                    ipw.printf("this.%1$s = %1$s;%n", name));
        } else if (nSize>IMAX){
            ipw.printf("this.i = i;%n");
        }
    }
    public void assignOutput(PrintModel ipw) {
        if (mc.oSize() >= 2) {
            if (mc.isOutputDelegate()) {
                ipw.printf("this.o = o;%n");
            } else {
                ipw.printf("this.so = so;%n");
            }
        }
    }

    public void declareInput(PrintModel ipw, @NotNull JdbcStatement jdbc) {
        int nSize = mc.nSize();
        if (1<=nSize && nSize<=IMAX) {
            jdbc.getNMap().forEach((name, type) -> {
                ipw.commaLn();
                if (type.isScalar() || type.columns() <=1) {
                    ipw.printf("        final %s %s", type.getPrimitive(), name);
                } else {
                    ipw.printf("        final List<%s> %s", type.getGeneric(), name);
                }
            });
        } else if (nSize>IMAX){
            ipw.commaLn();
            if (mc.isInputDelegate()) {
                ipw.printf("        final DI i");
            } else {
                ipw.printf("        final I i");
            }
        }
    }

    public void declareOutput(PrintModel ipw) {
        if (mc.oSize() < 2) {
            ipw.putf(") {%n");
        } else {
            ipw.commaLn();
            if (mc.isOutputDelegate()) {
                ipw.printf("        final DO o) {%n");
            } else {
                ipw.printf("        final %s<O> so) {%n", cc.supplier());
            }
        }
    }

    public void docEnd(@NotNull PrintModel ipw) {
        String cName = Tools.capitalize(mc.getName());
        ipw.printf(" * @return %sBuilder%n", cName);
        ipw.printf(" */%n");
    }
}
