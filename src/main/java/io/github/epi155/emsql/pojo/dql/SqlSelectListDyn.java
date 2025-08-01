package io.github.epi155.emsql.pojo.dql;

import io.github.epi155.emsql.api.*;
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

import static io.github.epi155.emsql.commons.Contexts.cc;
import static io.github.epi155.emsql.commons.Contexts.mc;

public class SqlSelectListDyn extends PojoAction
        implements ApiSelectFields, ApiDocSignature, ApiSelectDyn,
        SelectListDynModel {
    private final DelegateSelectDyn delegateSelectDyn;
    private final DelegateSelectDynFields delegateSelectFields;
    private final DelegateSelectSignature delegateSelectSignature;
    @Getter
    private final Map<String, Map<Integer, SqlParam>> andParms = new LinkedHashMap<>();
    @Setter
    @Getter
    private Map<String, String> optionalAnd = new LinkedHashMap<>();
    @Getter
    @Setter
    private InputModel input;
    @Getter
    @Setter
    private OutputModel output;
    @Setter
    private Integer fetchSize;

    public SqlSelectListDyn() {
        super();
        this.delegateSelectFields = new DelegateSelectDynFields(this);
        this.delegateSelectSignature = new DelegateSelectSignature(this);
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

    public void defineBuilder(PrintModel ipw, JdbcStatement jdbc, String name, String kPrg) {
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
        defineMethodList(ipw, jdbc, kPrg);
        ipw.ends();
    }

    private void defineMethodList(@NotNull PrintModel ipw, @NotNull JdbcStatement jdbc, String kPrg) {
        ipw.printf("public List<O> list() throws SQLException {%n");
        ipw.more();
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
            jdbc.getOMap().forEach((k, v) ->
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
