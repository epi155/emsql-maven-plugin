package io.github.epi155.emsql.spring.dql;

import io.github.epi155.emsql.api.*;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.SqlParam;
import io.github.epi155.emsql.commons.Tools;
import io.github.epi155.emsql.commons.dql.ApiSelectDyn;
import io.github.epi155.emsql.commons.dql.ApiSelectFields;
import io.github.epi155.emsql.commons.dql.DelegateSelectDyn;
import io.github.epi155.emsql.commons.dql.DelegateSelectDynFields;
import io.github.epi155.emsql.spring.SpringAction;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

import static io.github.epi155.emsql.commons.Contexts.cc;
import static io.github.epi155.emsql.commons.Contexts.mc;

public class SqlSelectListDyn extends SpringAction
        implements ApiSelectFields, ApiSelectDyn,
        SelectListDynModel {
    private final DelegateSelectDyn delegateSelectDyn;
    private final DelegateSelectDynFields delegateSelectFields;

    @Setter
    @Getter
    private Map<String,String> optionalAnd = new LinkedHashMap<>();
    @Getter
    @Setter
    private InputModel input;
    @Getter
    @Setter
    private OutputModel output;
    @Setter
    private Integer fetchSize;

    @Getter
    private final Map<String,Map<Integer, SqlParam>> andParms = new LinkedHashMap<>();

    public SqlSelectListDyn() {
        super();
        this.delegateSelectFields = new DelegateSelectDynFields(this);
        this.delegateSelectDyn = new DelegateSelectDyn(this);
    }

    @Override
    public JdbcStatement sql(Map<String, SqlDataType> fields) throws InvalidQueryException {
        return delegateSelectFields.sql(fields);
    }
    @Override
    protected void customWrite(String kPrg, @NotNull PrintModel ipw) {
        delegateSelectDyn.customWrite(ipw, kPrg);
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
        delegateSelectDyn.docEnd(ipw);
    }

    public void defineBuilder(PrintModel ipw, JdbcStatement jdbc, String name, String kPrg) {
        if (mc.oSize() < 1) throw new IllegalStateException("Invalid output parameter number");
        String cName = Tools.capitalize(name);

        // class definition
        ipw.printf("public class %sBuilder", cName);
        declareGenerics(ipw, cName, jdbc.getTKeys());

        ipw.putf("{%n");
        ipw.more();
        delegateSelectDyn.defineInput(ipw, jdbc);
        delegateSelectDyn.defineOutput(ipw);
        delegateSelectDyn.defineOptional(ipw);
        ipw.println();

        // ctor definition
        ipw.printf("public %sBuilder(%n", cName);   // public required by CGLIB
        ipw.commaReset();
        declareInput(ipw, jdbc);
        declareOutput(ipw);
        ipw.more();
        delegateSelectDyn.assignInput(ipw, jdbc);
        delegateSelectDyn.assignOutput(ipw);
        ipw.ends();
        delegateSelectDyn.defineMethodArgBuilder(ipw, kPrg, cName);   // final ?! CGLIB conflict
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
        ipw.printf("public List<O> list() throws SQLException {%n");
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
        delegateSelectDyn.setOptInput(ipw, kPrg);
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
        delegateSelectDyn.debugAction(ipw, kPrg, jdbcStatement);
    }

    @Override
    public void expandIn(@NotNull PrintModel ipw, @NotNull Map<Integer, SqlParam> notScalar, String kPrg) {
        delegateSelectDyn.expandIn(ipw, notScalar, kPrg);
    }

    @Override
    public void declareInput(PrintModel ipw, @NotNull JdbcStatement jdbc) {
        delegateSelectDyn.declareInput(ipw, jdbc);
    }

    @Override
    public void declareOutput(PrintModel ipw) {
        delegateSelectDyn.declareOutput(ipw);
    }
}
