package io.github.epi155.emsql.plugin.sql.dql;

import io.github.epi155.emsql.plugin.*;
import io.github.epi155.emsql.plugin.sql.JdbcStatement;
import io.github.epi155.emsql.plugin.sql.SqlAction;
import io.github.epi155.emsql.plugin.sql.SqlKind;
import io.github.epi155.emsql.plugin.sql.SqlParam;
import lombok.Getter;
import lombok.Setter;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.Map;

public class SqlCursorForSelect extends SqlAction implements ApiSelectFields {
    private final DelegateSelectFields delegateSelectFields;
    @Getter
    @Setter
    private ComAreaStd input;
    @Getter
    @Setter
    private ComAreaStd output;
    @Setter
    private Integer fetchSize;
    @Setter
    private ProgrammingModeEnum mode = ProgrammingModeEnum.Imperative;

    SqlCursorForSelect() {
        super();
        this.delegateSelectFields = new DelegateSelectFields(this);
    }
    @Override
    public JdbcStatement sql(Map<String, SqlKind> fields) throws MojoExecutionException {
        return delegateSelectFields.sql(fields);
    }

    @Override
    public void writeMethod(IndentPrintWriter ipw, String name, JdbcStatement jdbc, String kPrg, ClassContext cc) {

        if (mode == ProgrammingModeEnum.Functional) {
            writeFunctional(ipw, name, jdbc, kPrg, cc);
        } else {
            writeImperative(ipw, name, jdbc, kPrg, cc);
        }
    }

    private void writeImperative(IndentPrintWriter ipw, String name, JdbcStatement jdbc, String kPrg, ClassContext cc) {
        Map<Integer, SqlParam> iMap = jdbc.getIMap();
        Map<Integer, SqlParam> oMap = jdbc.getOMap();
        int iSize = iMap.size();
        int oSize = oMap.size();
        if (oSize < 1) throw new IllegalStateException("Invalid output parameter number");
        docBegin(ipw);
        docInput(ipw, jdbc);
        docOutput(ipw, oMap);
        docEnd(ipw);
        String cName = Tools.capitalize(name);

        ipw.printf("public static ");
        declareGenerics(ipw, cName, iSize, oSize);
        if (oSize == 1) {
            String oType = oMap.get(1).getType().getWrapper();
            ipw.putf("SqlCursor<%s> open%s(%n", oType, cName);
        } else {
            if (output!=null && output.isDelegate()) {
                cc.add("io.github.epi155.emsql.runtime.SqlDelegateCursor");
                ipw.putf("SqlDelegateCursor open%s(%n", cName);
            } else {
                cc.add("io.github.epi155.emsql.runtime.SqlCursor");
                ipw.putf("SqlCursor<O> open%s(%n", cName);
            }
        }

        ipw.printf("        final Connection c");
        declareInput(ipw, jdbc, cc);
        declareOutput(ipw, oSize, cc);
        ipw.more();
        Map<Integer, SqlParam> notScalar = notScalar(jdbc.getIMap());
        if (! notScalar.isEmpty()) {
            expandIn(ipw, notScalar, kPrg, jdbc.getNameSize(), cc);
        }
        if (output!=null && output.isDelegate()) {
            ipw.printf("return new SqlDelegateCursor() {%n");
        } else {
            ipw.printf("return new SqlCursor<O>() {%n");
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
        setInput(ipw, jdbc, cc);
        if (fetchSize != null) ipw.printf("ps.setFetchSize(%d);%n", fetchSize);
        setQueryHints(ipw);
        debugAction(ipw, kPrg, jdbc, cc);
        ipw.printf("this.rs = ps.executeQuery();%n");
        ipw.ends();
        ipw.printf("@Override%n");
        ipw.printf("public boolean hasNext() throws SQLException {%n");
        ipw.more();
        ipw.printf("return rs.next();%n");
        ipw.ends();
        ipw.printf("@Override%n");
        if (output!=null && output.isDelegate()) {
            ipw.printf("public void fetchNext() throws SQLException {%n");
        } else {
            ipw.printf("public O fetchNext() throws SQLException {%n");
        }
        ipw.more();
        fetch(ipw, oMap, cc);
        if (output==null || !output.isDelegate())
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


    private void writeFunctional(IndentPrintWriter ipw, String name, JdbcStatement jdbc, String kPrg, ClassContext cc) {
        Map<Integer, SqlParam> iMap = jdbc.getIMap();
        Map<Integer, SqlParam> oMap = jdbc.getOMap();
        int iSize = iMap.size();
        int oSize = oMap.size();
        if (oSize < 1) throw new IllegalStateException("Invalid output parameter number");
        docBegin(ipw);
        docInput(ipw, jdbc);
        docOutputUse(ipw, oMap);
        docEnd(ipw);
        String cName = Tools.capitalize(name);

        ipw.printf("public static ");
        declareGenerics(ipw, cName, iSize, oSize);

        ipw.putf("void loop%1$s(%n", cName);
        ipw.printf("        final Connection c");
        declareInput(ipw, jdbc, cc);
        declareOutputUse(ipw, oSize, oMap.get(1).getType().getWrapper(), cc);
        ipw.more();
        Map<Integer, SqlParam> notScalar = notScalar(jdbc.getIMap());
        if (notScalar.isEmpty()) {
            ipw.printf("try (PreparedStatement ps = c.prepareStatement(Q_%s)) {%n", kPrg);
        } else {
            expandIn(ipw, notScalar, kPrg, jdbc.getNameSize(), cc);
            ipw.printf("try (PreparedStatement ps = c.prepareStatement(query)) {%n");
        }
        ipw.more();
        setInput(ipw, jdbc, cc);
        if (fetchSize != null) ipw.printf("ps.setFetchSize(%d);%n", fetchSize);
        setQueryHints(ipw);
        ipw.printf("try (ResultSet rs = ps.executeQuery()) {%n");
        ipw.more();
        ipw.printf("while (rs.next()) {%n");
        ipw.more();
        fetch(ipw, oMap, cc);
        if (output!=null && output.isDelegate()) {
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
