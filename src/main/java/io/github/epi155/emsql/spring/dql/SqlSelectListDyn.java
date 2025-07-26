package io.github.epi155.emsql.spring.dql;

import io.github.epi155.emsql.api.*;
import io.github.epi155.emsql.commons.*;
import io.github.epi155.emsql.commons.dql.ApiDocSignature;
import io.github.epi155.emsql.commons.dql.ApiSelectFields;
import io.github.epi155.emsql.commons.dql.DelegateSelectDynFields;
import io.github.epi155.emsql.spring.SpringAction;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static io.github.epi155.emsql.commons.Contexts.IMAX;
import static io.github.epi155.emsql.commons.Contexts.cc;
import static io.github.epi155.emsql.commons.Contexts.mc;
import static io.github.epi155.emsql.commons.Tools.getterOf;

public class SqlSelectListDyn extends SpringAction implements ApiSelectFields, SelectListDynModel {
    @Setter
    private Map<String,String> optionalAnd = new LinkedHashMap<>();
    private final DelegateSelectDynFields delegateSelectFields;
    @Getter
    @Setter
    private InputModel input;
    @Getter
    @Setter
    private OutputModel output;
    @Setter
    private Integer fetchSize;

    private final Map<String,Map<Integer, SqlParam>> andParms = new LinkedHashMap<>();

    public SqlSelectListDyn() {
        super();
        this.delegateSelectFields = new DelegateSelectDynFields(this);
    }

    @Override
    public JdbcStatement sql(Map<String, SqlDataType> fields) throws InvalidQueryException {
        return delegateSelectFields.sql(fields);
    }
    @Override
    protected void customWrite(String kPrg, @NotNull PrintModel ipw) {
        if (optionalAnd.isEmpty())
            return;
        ipw.printf("private static final Map<String,String> Q_%s_OPT_MAP;%n", kPrg);
        int k=0;
        for(String key: optionalAnd.keySet()) {
            ipw.printf("private static final String Q_%s_OPT%04d = \"%s\";%n", kPrg, ++k, StringEscapeUtils.escapeJava(key));
        }
        k=0;
        ipw.printf("static {%n");
        ipw.more();
        ipw.printf("Q_%s_OPT_MAP = new LinkedHashMap<>();%n", kPrg);
        for(Map.Entry<String, String> entry: optionalAnd.entrySet()) {
            // :parm -> ?
            Tools.SqlStatement iStmt = Tools.replacePlaceholder(entry.getValue(), cc.getFields(), true);
            ipw.printf("Q_%s_OPT_MAP.put(Q_%s_OPT%04d, \"%s\");%n", kPrg, kPrg, ++k, StringEscapeUtils.escapeJava(iStmt.getText()));
            andParms.put(entry.getKey(), iStmt.getMap());
        }
        ipw.ends();
        cc.add("java.util.Map");
        cc.add("java.util.LinkedHashMap");
    }

    @Override
    public void writeMethod(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) {
        cc.add("java.util.List");
        cc.add("java.util.ArrayList");
        defineBuilder(ipw, jdbc, name, kPrg);

        signature(ipw, jdbc, name);
        String cName = Tools.capitalize(name);
        ipw.putf("%sBuilder<O> %s(%n", cName, name);
        ipw.commaReset();

        declareInput(ipw, jdbc);
        declareOutput(ipw);
        ipw.more();
        returnBuilder(ipw, jdbc, cName);

        ipw.ends();
    }

    private void signature(PrintModel ipw, JdbcStatement jdbc, String name) {
        if (mc.oSize() < 1) throw new IllegalStateException("Invalid output parameter number");
        String cName = Tools.capitalize(name);
        docBegin(ipw);
        docInput(ipw, jdbc);
        docOutput(ipw, jdbc.getOMap());
        docEnd(ipw);

        ipw.printf("public ");
        declareGenerics(ipw, cName, jdbc.getTKeys());
    }

    private void returnBuilder(PrintModel ipw, JdbcStatement jdbc, String cName) {
        cc.add("java.util.Collections");
        cc.add("java.lang.reflect.Method");
        cc.add("org.springframework.aop.framework.ProxyFactory");
        cc.add("org.springframework.transaction.PlatformTransactionManager");
        cc.add("org.springframework.transaction.TransactionDefinition");
        cc.add("org.springframework.transaction.annotation.Transactional");
        cc.add("org.springframework.transaction.interceptor.*");

        /*
         * La classe *Builder, viene proxy-ata e viene aggiunta la transazionalità al metodo "list()".
         * ------------------------------------------------------------------------------------------------------------
         * The *Builder class is proxy-ed and transactionality is added to the "list()" method.
         */

        ipw.printf("try {%n");
        ipw.more();

        // 1. Configurazione dell'attributo transazionale
        ipw.printf("RuleBasedTransactionAttribute txAttr = new RuleBasedTransactionAttribute();%n");
        // @Transactional(propagation = Propagation.REQUIRED)
        ipw.printf("txAttr.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);%n");
        // @Transactional(readOnly=true)
        ipw.printf("txAttr.setReadOnly(true);%n");
        // @Transactional(isolation = Isolation.DEFAULT)
        ipw.printf("txAttr.setIsolationLevel(TransactionDefinition.ISOLATION_DEFAULT);%n");
        // @Transactional(rollbackFor = Exception.class)
        ipw.printf("txAttr.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Exception.class)));%n");
        ipw.println();

        // 2. Mappatura del metodo
        ipw.printf("Method method = %sBuilder.class.getMethod(\"list\", null);%n", cName);
        ipw.printf("MethodMapTransactionAttributeSource attrSource = new MethodMapTransactionAttributeSource();%n");
        ipw.printf("attrSource.addTransactionalMethod(method, txAttr);%n");

        // 3. Intercettore con regole personalizzate
        ipw.printf("TransactionInterceptor txInterceptor = new TransactionInterceptor(transactionManager, attrSource);%n");
        ipw.println();

        // 4. Collegamento del proxy con l'intercettore
        ipw.printf("%1$sBuilder<O> target = new %1$sBuilder<>(", cName);
        int c=0;
        for(String arg: jdbc.getNMap().keySet()) {
            if(c++ > 0) ipw.commaLn(); else ipw.println();
            ipw.printf("        %s", arg);
        }
        if (mc.oSize() >= 2) {
            if(c > 0) ipw.commaLn(); else ipw.println();
            if (mc.isOutputDelegate()) {
                ipw.printf("        o");
            } else {
                ipw.printf("        so");
            }
        }
        ipw.putf(");%n");

        ipw.printf("ProxyFactory proxyFactory = new ProxyFactory(target);%n");
        ipw.printf("proxyFactory.addAdvice(txInterceptor);%n");
        ipw.printf("return (%sBuilder<O>) proxyFactory.getProxy();%n", cName);

        ipw.less();
        ipw.printf("} catch (Exception e) {%n");
        ipw.more();
        ipw.printf("throw  new RuntimeException(e);%n");
        ipw.ends();
    }

    @Override
    public void docEnd(@NotNull PrintModel ipw) {
        String cName = Tools.capitalize(mc.getName());
        ipw.printf(" * @return %sBuilder%n", cName);
        ipw.printf(" */%n");
    }

    public void defineBuilder(PrintModel ipw, JdbcStatement jdbc, String name, String kPrg) {
        if (mc.oSize() < 1) throw new IllegalStateException("Invalid output parameter number");
        String cName = Tools.capitalize(name);

        // class definition
        ipw.printf("public class %sBuilder", cName);
        declareGenerics(ipw, cName, jdbc.getTKeys());

        ipw.putf("{%n");
        ipw.more();
        defineInput(ipw, jdbc);
        defineOutput(ipw);
        defineOptional(ipw);
        ipw.println();

        // ctor definition
        ipw.printf("public %sBuilder(%n", cName);   // public required by CGLIB
        ipw.commaReset();
        declareInput(ipw, jdbc);
        declareOutput(ipw);
        ipw.more();
        assignInput(ipw, jdbc);
        assignOutput(ipw);
        ipw.ends();
        defineMethodArgBuilder(ipw, kPrg, cName);
        defineMethodList(ipw, jdbc, kPrg);
        ipw.ends();
    }

    private void defineMethodList(@NotNull PrintModel ipw, @NotNull JdbcStatement jdbc, String kPrg) {
        /*
         * L'annotazione @Transactional funziona se la classe è creata (e proxy-ata) da Spring.
         * In questo caso la classe *Builder è creata programmaticamente.
         * ------------------------------------------------------------------------------------------------------------
         * The @Transactional annotation works if the class is created (and proxy-ed) by Spring.
         * In this case the *Builder class is created programmatically.
         */
        ipw.printf("// @Transactional(readOnly=true)%n");
        ipw.printf("public List<? super O> list() throws SQLException {%n");
        ipw.more();
        ipw.printf("final Connection c = DataSourceUtils.getConnection(dataSource);%n");
        Map<Integer, SqlParam> notScalar = notScalar(jdbc.getIMap());
        if (notScalar.isEmpty()) {
            ipw.printf("String query = EmSQL.buildQuery(Q_%1$s_ANTE, Q_%1$s_OPT_MAP, Q_%1$s_POST, options);%n", kPrg);
        } else {
            expandIn(ipw, notScalar, kPrg);
            ipw.printf("String query = EmSQL.buildQuery(queryAnte, Q_%1$s_OPT_MAP, Q_%1$s_POST, options);%n", kPrg);
        }

        debugAction(ipw, kPrg, jdbc);

        ipw.printf("try (PreparedStatement ps = c.prepareStatement(query)) {%n");
        ipw.more();
        setInput(ipw, jdbc);
        setOptInput(ipw, kPrg);
        if (fetchSize != null) ipw.printf("ps.setFetchSize(%d);%n", fetchSize);
        setQueryHints(ipw);
        ipw.printf("try (ResultSet rs = ps.executeQuery()) {%n");
        ipw.more();
        if (mc.oSize() == 1) {
            jdbc.getOMap().forEach((k,v) ->
                    ipw.printf("List<%s> list = new ArrayList<>();%n", v.getType().getWrapper()));
        } else {
            ipw.printf("List<O> list = new ArrayList<>();%n");
        }
        ipw.printf("while (rs.next()) {%n");
        ipw.more();
        fetch(ipw, jdbc.getOMap());
        ipw.printf("list.add(o);%n");
        ipw.ends();
        ipw.printf("return list;%n");
        ipw.ends(); // end try (ResultSet rs)
        ipw.ends(); // end try (PreparedStatement ps)
        ipw.ends(); // end list()
    }

    @Override
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
            if ( isUnboxRequest(nSize)) {
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
            for(Map.Entry<String,Map<Integer, SqlParam>> e: andParms.entrySet()) {
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

    private void setOptInput(PrintModel ipw, String kPrg) {
        int k=0;
        for(Map.Entry<String,Map<Integer, SqlParam>> e: andParms.entrySet()) {
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

    @Override
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


    private void defineMethodArgBuilder(PrintModel ipw, String kPrg, String cName) {
        int k=1;
        for(Map.Entry<String, String> a: optionalAnd.entrySet()) {
            docMethod(ipw, a, k, cName);
            ipw.printf("public %sBuilder<O> %s(", cName, a.getKey());   // final ?! CGLIB conflict
            Map<Integer, SqlParam> parms = andParms.get(a.getKey());
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

        Collection<SqlParam> parms = andParms.get(a.getKey()).values();
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

    private void writeAssign(PrintModel ipw, Map<OptArg, List<Integer>> pArg) {
        for(Map.Entry<OptArg, List<Integer>> a: pArg.entrySet()) {
            for(int i: a.getValue()) {
                ipw.printf("this.opt%dp%d = %s;%n", a.getKey().getNdx(), i, a.getKey().getName());
            }
        }
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

    private void defineInput(PrintModel ipw, JdbcStatement jdbc) {
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
    private void defineOutput(PrintModel ipw) {
        if (mc.oSize() >= 2) {
            if (mc.isOutputDelegate()) {
                ipw.printf("private final DO o;%n");
            } else {
                ipw.printf("private final %s<O> so;%n", cc.supplier());
            }
        }
    }
    private void defineOptional(PrintModel ipw) {
        int k=1;
        cc.add("java.util.Set");
        cc.add("java.util.HashSet");
        ipw.printf("private final Set<String> options = new HashSet<>();%n");
        for(Map.Entry<String,Map<Integer, SqlParam>> a: andParms.entrySet()) {
            for( Map.Entry<Integer, SqlParam> e: a.getValue().entrySet()) {
                ipw.printf("private %s opt%dp%d; // %s%n",
                        e.getValue().getType().getPrimitive(), k, e.getKey(), e.getValue().getName());
            }
            k++;
        }
    }

    @Override
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

    @Override
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

    private void assignInput(PrintModel ipw, JdbcStatement jdbc) {
        int nSize = mc.nSize();
        if (1<=nSize && nSize<=IMAX) {
            jdbc.getNMap().forEach((name, type) ->
                    ipw.printf("this.%1$s = %1$s;%n", name));
        } else if (nSize>IMAX){
            ipw.printf("this.i = i;%n");
        }
    }
    private void assignOutput(PrintModel ipw) {
        if (mc.oSize() >= 2) {
            if (mc.isOutputDelegate()) {
                ipw.printf("this.o = o;%n");
            } else {
                ipw.printf("this.so = so;%n");
            }
        }
    }
}
