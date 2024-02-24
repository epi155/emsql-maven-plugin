package io.github.epi155.emsql.pojo.dql;

import io.github.epi155.emsql.api.*;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.SqlParam;
import io.github.epi155.emsql.commons.Tools;
import io.github.epi155.emsql.commons.dql.ApiSelectFields;
import io.github.epi155.emsql.commons.dql.DelegateSelectFields;
import io.github.epi155.emsql.pojo.PojoAction;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

import static io.github.epi155.emsql.commons.Contexts.cc;
import static io.github.epi155.emsql.commons.Contexts.mc;

public class SqlCursorForSelect extends PojoAction implements ApiSelectFields, CursorForSelectModel {
    private final DelegateSelectFields delegateSelectFields;
    @Getter
    @Setter
    private InputModel input;
    @Getter
    @Setter
    private OutputModel output;
    @Setter
    private Integer fetchSize;
    @Setter
    private ProgrammingModeEnum mode = ProgrammingModeEnum.Imperative;

    public SqlCursorForSelect() {
        super();
        this.delegateSelectFields = new DelegateSelectFields(this);
    }
    @Override
    public JdbcStatement sql(Map<String, SqlDataType> fields) throws InvalidQueryException {
        return delegateSelectFields.sql(fields);
    }

    @Override
    public void writeMethod(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) {

        if (mode == ProgrammingModeEnum.Functional) {
            writeFunctional(ipw, name, jdbc, kPrg);
        } else {
            writeImperative(ipw, name, jdbc, kPrg);
        }
    }

    private void writeImperative(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) {
        Map<Integer, SqlParam> oMap = jdbc.getOMap();
        int oSize = oMap.size();
        if (oSize < 1) throw new IllegalStateException("Invalid output parameter number");
        docBegin(ipw);
        docInput(ipw, jdbc);
        docOutput(ipw, oMap);
        docEnd(ipw);
        String cName = Tools.capitalize(name);

        ipw.printf("public static ");
        declareGenerics(ipw, cName, jdbc.getTKeys());
        if (oSize == 1) {
            String oType = oMap.get(1).getType().getWrapper();
            cc.add("io.github.epi155.emsql.runtime.SqlCursor");
            ipw.putf("SqlCursor<%s> open%s(%n", oType, cName);
        } else {
            if (mc.isOutputDelegate()) {
                cc.add("io.github.epi155.emsql.runtime.SqlDelegateCursor");
                ipw.putf("SqlDelegateCursor open%s(%n", cName);
            } else {
                cc.add("io.github.epi155.emsql.runtime.SqlCursor");
                ipw.putf("SqlCursor<O> open%s(%n", cName);
            }
        }

        ipw.printf("        final Connection c");
        declareInput(ipw, jdbc);
        declareOutput(ipw);
        ipw.more();
        Map<Integer, SqlParam> notScalar = notScalar(jdbc.getIMap());
        if (! notScalar.isEmpty()) {
            expandIn(ipw, notScalar, kPrg);
        }
        if (mc.isOutputDelegate()) {
            ipw.printf("return new SqlDelegateCursor() {%n");
        } else {
            if (oSize==1) {
                ipw.printf("return new SqlCursor<%s>() {%n",oMap.get(1).getType().getWrapper());
            } else {
                ipw.printf("return new SqlCursor<O>() {%n");
            }
        }
        ipw.more();
        ipw.printf("private final ResultSet rs;%n");
        ipw.printf("private final PreparedStatement ps;%n");
        ipw.printf("{%n");
        ipw.more();
        if(notScalar.isEmpty()) {
            ipw.printf("this.ps = c.prepareStatement(Q_%s);%n", kPrg);
        } else {
            ipw.printf("this.ps = c.prepareStatement(query);%n");
        }
        setInput(ipw, jdbc);
        if (fetchSize != null) ipw.printf("ps.setFetchSize(%d);%n", fetchSize);
        setQueryHints(ipw);
        debugAction(ipw, kPrg, jdbc);
        ipw.printf("this.rs = ps.executeQuery();%n");
        ipw.ends();
        ipw.printf("@Override%n");
        ipw.printf("public boolean hasNext() throws SQLException {%n");
        ipw.more();
        ipw.printf("return rs.next();%n");
        ipw.ends();
        ipw.printf("@Override%n");
        if (mc.isOutputDelegate()) {
            ipw.printf("public void fetchNext() throws SQLException {%n");
        } else {
            if (oSize==1) {
                ipw.printf("public %s fetchNext() throws SQLException {%n", oMap.get(1).getType().getWrapper());
            } else {
                ipw.printf("public O fetchNext() throws SQLException {%n");
            }
        }
        ipw.more();
        fetch(ipw, oMap);
        if (!mc.isOutputDelegate())
            ipw.printf("return o;%n");
        ipw.ends();
        ipw.printf("@Override%n");
        ipw.printf("public void close() throws SQLException {%n");
        ipw.more();
        ipw.printf("if (rs != null) rs.close();%n");
        ipw.printf("if (ps != null) ps.close();%n");
        ipw.ends();
        ipw.less();
        ipw.printf("};%n"); // close anonymous class statement
        ipw.ends();
    }


    private void writeFunctional(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) {
        Map<Integer, SqlParam> oMap = jdbc.getOMap();
        int oSize = oMap.size();
        if (oSize < 1) throw new IllegalStateException("Invalid output parameter number");
        docBegin(ipw);
        docInput(ipw, jdbc);
        docOutputUse(ipw, oMap);
        docEnd(ipw);
        String cName = Tools.capitalize(name);

        ipw.printf("public static ");
        declareGenerics(ipw, cName, jdbc.getTKeys());

        ipw.putf("void loop%1$s(%n", cName);
        ipw.printf("        final Connection c");
        declareInput(ipw, jdbc);
        declareOutputUse(ipw, oMap.get(1).getType().getWrapper());
        ipw.more();
        openQuery(ipw, jdbc, kPrg);
        ipw.more();
        setInput(ipw, jdbc);
        if (fetchSize != null) ipw.printf("ps.setFetchSize(%d);%n", fetchSize);
        setQueryHints(ipw);
        ipw.printf("try (ResultSet rs = ps.executeQuery()) {%n");
        ipw.more();
        ipw.printf("while (rs.next()) {%n");
        ipw.more();
        fetch(ipw, oMap);
        if (mc.isOutputDelegate()) {
            ipw.printf("co.run();%n");
        } else {
            ipw.printf("co.accept(o);%n");
        }
        ipw.ends();
        ipw.ends();
        ipw.ends();
        ipw.ends();
    }

}
