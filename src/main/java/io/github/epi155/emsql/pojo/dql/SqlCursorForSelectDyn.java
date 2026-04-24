package io.github.epi155.emsql.pojo.dql;

import io.github.epi155.emsql.api.*;
import io.github.epi155.emsql.commons.*;
import io.github.epi155.emsql.commons.dql.*;
import io.github.epi155.emsql.pojo.PojoAction;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.github.epi155.emsql.commons.Contexts.cc;
import static io.github.epi155.emsql.commons.Contexts.mc;

public class SqlCursorForSelectDyn extends PojoAction
        implements ApiSelectFields, ApiSelectDyn, ApiDocSignature,
        CursorForSelectDynModel {
    private final DelegateSelectDynFields delegateSelectFields;
    private final DelegateSelectDyn delegateSelectDyn;
    private final DelegateSelectSignature delegateSelectSignature;
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
        this.delegateSelectSignature = new DelegateSelectSignature(this);
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
    public void writeMethod(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) throws InvalidQueryException {
        defineBuilder(ipw, jdbc, name, kPrg);

        delegateSelectSignature.signature(ipw, jdbc, name);
        String cName = Tools.capitalize(name);
        List<String> tKeys = delegateSelectDyn.packGenerics(name, andParms.values(), jdbc.getTKeys());
        ipw.putf("%sBuilder", cName);
        useGenerics(ipw, tKeys);
//        declareGenerics(ipw, tKeys, iName, oName);
        ipw.putf(" %s(%n", name);

        ipw.printf("        final Connection c");
        declareInput(ipw, jdbc);
        declareOutput(ipw);
        ipw.more();
        returnBuilder(ipw, jdbc, cName);

        ipw.ends();
    }

    private void returnBuilder(PrintModel ipw, JdbcStatement jdbc, String cName) {
        ipw.printf("return new %sBuilder<>(c", cName);
        pushInput(ipw, jdbc, 1);
        if (mc.oSize() >= 2) {
            ipw.commaLn();
            ipw.printf("so");
        }
        ipw.putf(");%n");
    }

    @Override
    public void docEnd(@NotNull PrintModel ipw) {
        delegateSelectDyn.docEnd(ipw);
    }

    private void defineBuilder(PrintModel ipw, JdbcStatement jdbc, String name, String kPrg) {
        if (mc.oSize() < 1) throw new IllegalStateException("Invalid output parameter number");
        String cName = Tools.capitalize(name);

        String iName = cc.inPrepare(name, jdbc.getIMap().values(), mc);
        String oName = cc.outPrepare(name, jdbc.getOMap().values(), mc);
        // class definition
        ipw.printf("public static class %sBuilder", cName);
        declareGenerics(ipw, jdbc.getTKeys(), iName, oName);

        ipw.putf("{%n");
        ipw.more();
        ipw.printf("private final Connection c;%n");
        delegateSelectDyn.defineInput(ipw, jdbc);
        delegateSelectDyn.defineOutput(ipw);
        delegateSelectDyn.defineOptional(ipw);
        ipw.println();

        // ctor definition
        ipw.printf("public %sBuilder(%n", cName);
        ipw.printf("        final Connection c");
        declareInput(ipw, jdbc);
        declareOutputPlain(ipw);
        ipw.more();
        ipw.printf("this.c = c;%n");
        delegateSelectDyn.assignInput(ipw, jdbc);
        delegateSelectDyn.assignOutput(ipw);
        ipw.ends();
        delegateSelectDyn.defineMethodArgBuilder(ipw, kPrg, cName, () -> useGenerics(ipw, jdbc.getTKeys()));

        if (mode == ProgrammingModeEnum.Functional) {
            defineForEach(ipw, jdbc, name, kPrg);
        } else {
            cc.add("io.github.epi155.emsql.runtime.SqlCursor");
            defineOpenCursor(ipw, jdbc, name, kPrg);
        }
        ipw.ends();
    }

    private void defineForEach(PrintModel ipw, JdbcStatement jdbc, String name, String kPrg) {
        DocUtils.docCursorForEach(ipw, name);
        ipw.printf("public void forEach(");
        declareOutputConsumer(ipw, jdbc);
        ipw.putf(") throws SQLException {%n");
        ipw.more();

        delegateSelectDyn.writeForEachCode(ipw, jdbc, kPrg);
    }

    private void declareOutputConsumer(PrintModel ipw, JdbcStatement jdbc) {
        Map<Integer, SqlOutParam> oMap = jdbc.getOMap();
        String type = oMap.get(1).getType().getWrapper();
        if (mc.oSize() > 1) {
            ipw.putf("%s<O> co", cc.consumer());
        } else {
            ipw.putf("%s<%s> co", cc.consumer(), type);
        }
    }

    @Override
    public void debugAction(PrintModel ipw, String kPrg, JdbcStatement jdbcStatement) {
        delegateSelectDyn.debugAction(ipw, kPrg, jdbcStatement);
    }
    @Override
    public void dumpAction(PrintModel ipw, String kPrg, JdbcStatement jdbcStatement) {
        delegateSelectDyn.dumpAction(ipw, kPrg, jdbcStatement);
    }

    private void defineOpenCursor(PrintModel ipw, JdbcStatement jdbc, String name, String kPrg) {
        DocUtils.docCursorOpen(ipw, name);
//        ipw.printf("public SqlCursor<O> open() throws SQLException {%n");
        cc.add("io.github.epi155.emsql.runtime.SqlCursor");
        Map<Integer, SqlOutParam> oMap = jdbc.getOMap();
        int oSize = oMap.size();
        String cName = Tools.capitalize(name);
        ipw.printf("public ");
        if (oSize == 1) {
            String oType = oMap.get(1).getType().getWrapper();
            ipw.putf("SqlCursor<%s> open%s() throws SQLException {%n", oType, cName);
        } else {
            ipw.putf("SqlCursor<O> open%s() throws SQLException {%n", cName);
        }

        ipw.more();
        ipw.printf("return new SqlCursor<>() {%n");
        ipw.more();
        ipw.printf("private final ResultSet rs;%n");
        ipw.printf("private final PreparedStatement ps;%n");
        ipw.printf("{%n");  // ctor
        ipw.more();

        delegateSelectDyn.writeOpenCode(ipw, jdbc, kPrg);
    }
}
