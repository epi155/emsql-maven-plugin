package io.github.epi155.emsql.pojo.dql;

import io.github.epi155.emsql.api.*;
import io.github.epi155.emsql.commons.DocUtils;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.SqlParam;
import io.github.epi155.emsql.commons.Tools;
import io.github.epi155.emsql.commons.dql.*;
import io.github.epi155.emsql.pojo.PojoAction;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

import static io.github.epi155.emsql.commons.Contexts.*;

public class SqlCursorForSelectDyn extends PojoAction
        implements ApiSelectFields, ApiSelectDyn, ApiDocSignature,
        CursorForSelectDynModel {
    private final DelegateSelectDynFields delegateSelectFields;
    private final DelegateSelectDyn delegateSelectDyn;
    private final DelegateSelectSignature delegateSelectSignature;
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
    public void writeMethod(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) {
        defineBuilder(ipw, jdbc, name, kPrg);

        delegateSelectSignature.signature(ipw, jdbc, name);
        String cName = Tools.capitalize(name);
        ipw.putf("%sBuilder<O> %s(%n", cName, name);

        ipw.printf("        final Connection c");
        declareInput(ipw, jdbc);
        declareOutput(ipw);
        ipw.more();
        returnBuilder(ipw, jdbc, cName);

        ipw.ends();
    }

    private void returnBuilder(PrintModel ipw, JdbcStatement jdbc, String cName) {
        ipw.printf("return new %sBuilder<>(c", cName);
        jdbc.getNMap().forEach((k, q) -> {
            ipw.commaLn();
            ipw.printf("%s", k);
        });
        if (mc.oSize() >= 2) {
            ipw.commaLn();
            if (mc.isOutputDelegate()) {
                ipw.printf("o");
            } else {
                ipw.printf("so");
            }
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

        // class definition
        ipw.printf("public static class %sBuilder", cName);
        declareGenerics(ipw, cName, jdbc.getTKeys());

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
        declareOutput(ipw);
        ipw.more();
        ipw.printf("this.c = c;%n");
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
        ipw.printf("public void forEach(");
        declareOutputConsumer(ipw, jdbc);
        ipw.putf(") throws SQLException {%n");
        ipw.more();

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
        ipw.printf("public SqlCursor<O> open() throws SQLException {%n");
        ipw.more();
        ipw.printf("return new SqlCursor<O>() {%n");
        ipw.more();
        ipw.printf("private final ResultSet rs;%n");
        ipw.printf("private final PreparedStatement ps;%n");
        ipw.printf("{%n");  // ctor
        ipw.more();

        delegateSelectDyn.writeOpenCode(ipw, jdbc, kPrg);
    }
}
