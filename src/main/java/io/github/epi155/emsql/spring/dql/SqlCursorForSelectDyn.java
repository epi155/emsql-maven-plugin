package io.github.epi155.emsql.spring.dql;

import io.github.epi155.emsql.api.*;
import io.github.epi155.emsql.commons.DocUtils;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.SqlParam;
import io.github.epi155.emsql.commons.Tools;
import io.github.epi155.emsql.commons.dql.*;
import io.github.epi155.emsql.spring.SpringAction;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

import static io.github.epi155.emsql.commons.Contexts.cc;
import static io.github.epi155.emsql.commons.Contexts.mc;

public class SqlCursorForSelectDyn extends SpringAction
        implements ApiSelectFields, ApiSelectDyn,
        CursorForSelectDynModel {
    private final DelegateSelectDynFields delegateSelectFields;
    private final DelegateSelectDyn delegateSelectDyn;
    @Getter
    @Setter
    private InputModel input;
    @Getter
    @Setter
    private OutputModel output;
    @Getter
    @Setter
    private Integer fetchSize;
    @Setter
    private ProgrammingModeEnum mode = ProgrammingModeEnum.Imperative;
    @Getter
    private final Map<String, Map<Integer, SqlParam>> andParms = new LinkedHashMap<>();
    @Setter
    @Getter
    private Map<String, String> optionalAnd = new LinkedHashMap<>();

    public SqlCursorForSelectDyn() {
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
         * La classe *Builder, viene proxy-ata e viene aggiunta la transazionalità ai metodi "open()" e "forEach()".
         * ------------------------------------------------------------------------------------------------------------
         * The *Builder class is proxy-ed and transactionality is added to the "open()" and "forEach()" methods.
         */

        ipw.printf("try {%n");
        ipw.more();

        // 1. Configurazione dell'attributo transazionale
        ipw.printf("RuleBasedTransactionAttribute txAttr = new RuleBasedTransactionAttribute();%n");
        // @Transactional(propagation = Propagation.REQUIRED)
        ipw.printf("txAttr.setPropagationBehavior(TransactionDefinition.PROPAGATION_MANDATORY);%n");
        // @Transactional(readOnly=true)
        ipw.printf("txAttr.setReadOnly(true);%n");
        // @Transactional(isolation = Isolation.DEFAULT)
        ipw.printf("txAttr.setIsolationLevel(TransactionDefinition.ISOLATION_DEFAULT);%n");
        // @Transactional(rollbackFor = default {}) ! (RuntimeException | Error)
        //ipw.printf("txAttr.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Exception.class)));%n");
        ipw.println();

        // 2. Mappatura del metodo
        if (mode == ProgrammingModeEnum.Functional) {
            ipw.printf("Method method = %sBuilder.class.getMethod(\"forEach\", new Class[]{%s.class});%n", cName, cc.consumer());
        } else {
            ipw.printf("Method method = %sBuilder.class.getMethod(\"open\", null);%n", cName);
        }
        ipw.printf("MethodMapTransactionAttributeSource attrSource = new MethodMapTransactionAttributeSource();%n");
        ipw.printf("attrSource.addTransactionalMethod(method, txAttr);%n");

        // 3. Intercettore con regole personalizzate
        ipw.printf("TransactionInterceptor txInterceptor = new TransactionInterceptor(transactionManager, attrSource);%n");
        ipw.println();

        // 4. Collegamento del proxy con l'intercettore
        ipw.printf("%1$sBuilder<O> target = new %1$sBuilder<>(", cName);
        int c = 0;
        for (String arg : jdbc.getNMap().keySet()) {
            if (c++ > 0) ipw.commaLn();
            else ipw.println();
            ipw.printf("        %s", arg);
        }
        if (mc.oSize() >= 2) {
            if (c > 0) ipw.commaLn();
            else ipw.println();
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

    private void defineBuilder(PrintModel ipw, JdbcStatement jdbc, String name, String kPrg) {
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
        ipw.printf("public %sBuilder(%n", cName);
        ipw.commaReset();
        declareInput(ipw, jdbc);
        declareOutput(ipw);
        ipw.more();
        delegateSelectDyn.assignInput(ipw, jdbc);
        delegateSelectDyn.assignOutput(ipw);
        ipw.ends();
        delegateSelectDyn.defineMethodArgBuilder(ipw, kPrg, cName);

        if (mode == ProgrammingModeEnum.Functional) {
            defineForEach(ipw, jdbc, name, kPrg);
        } else {
            defineOpenCursor(ipw, jdbc, name, kPrg);
        }
        ipw.ends();
    }

    private void defineForEach(PrintModel ipw, JdbcStatement jdbc, String name, String kPrg) {
        DocUtils.docCursorForEach(ipw, name);
        /*
         * L'annotazione @Transactional funziona se la classe è creata (e proxy-ata) da Spring.
         * In questo caso la classe *Builder è creata programmaticamente.
         * ------------------------------------------------------------------------------------------------------------
         * The @Transactional annotation works if the class is created (and proxy-ed) by Spring.
         * In this case the *Builder class is created programmatically.
         */
        ipw.printf("// @Transactional(readOnly=true, propagation=Propagation.MANDATORY) :: implemented programmatically ::%n");
        ipw.printf("public void forEach(");
        declareOutputConsumer(ipw, jdbc);
        ipw.putf(") throws SQLException {%n");
        ipw.more();
        ipw.printf("final Connection c = DataSourceUtils.getConnection(dataSource);%n");

        delegateSelectDyn.writeForEachCode(ipw, jdbc, kPrg);
    }

    private void declareOutputConsumer(PrintModel ipw, JdbcStatement jdbc) {
        Map<Integer, SqlParam> oMap = jdbc.getOMap();
        String type = oMap.get(1).getType().getWrapper();
        if (mc.oSize() > 1) {
            if (mc.isOutputDelegate()) {
                ipw.putf("Runnable co");
            } else {
                ipw.putf("%s<O> co", cc.consumer());
            }
        } else {
            if (mc.isOutputDelegate()) {
                ipw.putf("Runnable co");
            } else {
                ipw.putf("%s<%s> co", cc.consumer(), type);
            }
        }
    }

    @Override
    public void debugAction(PrintModel ipw, String kPrg, JdbcStatement jdbcStatement) {
        delegateSelectDyn.debugAction(ipw, kPrg, jdbcStatement);
    }

    private void defineOpenCursor(PrintModel ipw, JdbcStatement jdbc, String name, String kPrg) {
        DocUtils.docCursorOpen(ipw, name);
        /*
         * L'annotazione @Transactional funziona se la classe è creata (e proxy-ata) da Spring.
         * In questo caso la classe *Builder è creata programmaticamente.
         * ------------------------------------------------------------------------------------------------------------
         * The @Transactional annotation works if the class is created (and proxy-ed) by Spring.
         * In this case the *Builder class is created programmatically.
         */
        ipw.printf("// @Transactional(readOnly=true, propagation=Propagation.MANDATORY) :: implemented programmatically ::%n");
        ipw.printf("public SqlCursor<O> open() throws SQLException {%n");
        ipw.more();
        ipw.printf("return new SqlCursor<O>() {%n");
        ipw.more();
        ipw.printf("private final ResultSet rs;%n");
        ipw.printf("private final PreparedStatement ps;%n");
        ipw.printf("{%n");  // ctor
        ipw.more();
        ipw.printf("final Connection c = DataSourceUtils.getConnection(dataSource);%n");

        delegateSelectDyn.writeOpenCode(ipw, jdbc, kPrg);
    }
}
