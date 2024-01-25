package io.github.epi155.esql.plugin.sql.dql;

import io.github.epi155.esql.plugin.*;
import io.github.epi155.esql.plugin.sql.JdbcStatement;
import io.github.epi155.esql.plugin.sql.SqlAction;
import io.github.epi155.esql.plugin.sql.SqlEnum;
import io.github.epi155.esql.plugin.sql.SqlParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.maven.plugin.MojoExecutionException;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SqlCursorForSelect extends SqlAction {
    private ComAreaStd input;
    private ComAreaStd output;
    private Integer fetchSize;
    private ProgrammingModeEnum mode = ProgrammingModeEnum.Imperative;

    private static final String tmpl =
            "^SELECT (.*?) (INTO (.*)) FROM (.*)$";
    private static final Pattern regx = Pattern.compile(tmpl, Pattern.CASE_INSENSITIVE);
    @Override
    public JdbcStatement sql(Map<String, SqlEnum> fields) throws MojoExecutionException {
        String nText = Tools.oneLine(getExecSql());
        Matcher m = regx.matcher(nText);
        if (m.find()) {
            String sFld = m.group(1);
            String sInto = m.group(3);
            String sTables = m.group(4);
            String oText = "SELECT " + sFld + " FROM " + sTables;
            Tools.SqlStatement iStmt = Tools.replacePlaceholder(oText, fields);
            @NotNull Map<Integer, SqlParam> oMap = Tools.mapPlaceholder(sInto, fields);
            return new JdbcStatement(iStmt.getText(), iStmt.getMap(), oMap);
        } else {
            throw new MojoExecutionException("Invalid query format: "+ getExecSql());
        }
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
        docInput(ipw, iMap);
        docOutput(ipw, oMap);
        docEnd(ipw);
        String cName = Tools.capitalize(name);

        ipw.printf("public static ");
        declareGenerics(ipw, cName, iSize, oSize);
        if (oSize == 1) {
            String oType = oMap.get(1).getType().getAccess();
            ipw.putf("ESqlCursor<%s> open%s(%n", oType, cName);
        } else {
            if (output!=null && output.isDelegate()) {
                cc.add("io.github.epi155.esql.runtime.ESqlDelegateCursor");
                ipw.putf("ESqlDelegateCursor open%s(%n", cName);
            } else {
                cc.add("io.github.epi155.esql.runtime.ESqlCursor");
                ipw.putf("ESqlCursor<O> open%s(%n", cName);
            }
        }

        ipw.printf("        Connection c");
        declareInput(ipw, iMap, cName);
        declareOutput(ipw, oSize, cc);
        ipw.more();
        if (output!=null && output.isDelegate()) {
            ipw.printf("return new ESqlDelegateCursor() {%n");
        } else {
            ipw.printf("return new ESqlCursor<>() {%n");
        }
        ipw.more();
        ipw.printf("private final ResultSet rs;%n");
        ipw.printf("private final PreparedStatement ps;%n");
        ipw.printf("{%n");
        ipw.more();
        ipw.printf("this.ps = c.prepareStatement(Q_%s);%n", kPrg);
        setInput(ipw, iMap);
        if (fetchSize != null) ipw.printf("ps.setFetchSize(%d);%n", fetchSize);
        if (getTimeout() != null) ipw.printf("ps.setQueryTimeout(%d);%n", getTimeout());
        debugAction(ipw, kPrg, iMap, cc);
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
        int oSize = oMap.size();
        if (oSize < 1) throw new IllegalStateException("Invalid output parameter number");
        String cName = Tools.capitalize(name);
        docBegin(ipw);
        docInput(ipw, iMap);
        docOutputUse(ipw, oMap);
        docEnd(ipw);
        if (oSize > 1) {
            if (output!=null && output.isDelegate()) {
                ipw.printf("public static <DO extends Delegate%s"+RESPONSE+"> void loop%1$s(%n", cName);
            } else {
                ipw.printf("public static <O extends %s"+RESPONSE+"> void loop%1$s(%n", cName);
            }
        } else {
            ipw.printf("public static void loop%s(%n", oMap.get(1).getType().getAccess(), cName);
        }
        ipw.printf("        Connection c");
        declareInput(ipw, iMap, cName);
        declareOutputUse(ipw, oSize, oMap.get(1).getType().getAccess(), cc);
        ipw.more();
        ipw.printf("try (PreparedStatement ps = c.prepareStatement(Q_%s)) {%n", kPrg);
        ipw.more();
        setInput(ipw, iMap);
        if (fetchSize != null) ipw.printf("ps.setFetchSize(%d);%n", fetchSize);
        if (getTimeout() != null) ipw.printf("ps.setQueryTimeout(%d);%n", getTimeout());
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
