package io.github.epi155.esql.plugin.sql.dql;

import io.github.epi155.esql.plugin.ClassContext;
import io.github.epi155.esql.plugin.ComAreaStd;
import io.github.epi155.esql.plugin.IndentPrintWriter;
import io.github.epi155.esql.plugin.Tools;
import io.github.epi155.esql.plugin.sql.JdbcStatement;
import io.github.epi155.esql.plugin.sql.SqlAction;
import io.github.epi155.esql.plugin.sql.SqlEnum;
import io.github.epi155.esql.plugin.sql.SqlParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SqlSelectCount extends SqlAction {
    private ComAreaStd input;

    private static final String tmpl =
            "^SELECT COUNT[(](.*)[)] FROM (.*)$";
    private static final Pattern regx = Pattern.compile(tmpl, Pattern.CASE_INSENSITIVE);
    @Override
    public JdbcStatement sql(Map<String, SqlEnum> fields) throws MojoExecutionException {
        String nText = Tools.oneLine(getExecSql());
        Matcher m = regx.matcher(nText);
        if (m.find()) {
            String sFld = m.group(1);
            String sTables = m.group(2);
            String oText = "SELECT COUNT(" + sFld + ") FROM " + sTables;
            Tools.SqlStatement iStmt = Tools.replacePlaceholder(oText, fields);
            return new JdbcStatement(iStmt.getText(), iStmt.getMap(), Map.of());
        } else {
            throw new MojoExecutionException("Invalid query format: "+ getExecSql());
        }
    }

    @Override
    public void writeMethod(IndentPrintWriter ipw, String name, JdbcStatement jdbc, String kPrg, ClassContext cc) {
        cc.add("io.github.epi155.esql.runtime.ESqlCode");

        Map<Integer, SqlParam> iMap = jdbc.getIMap();
        int iSize = iMap.size();
        String cName = Tools.capitalize(name);
        docBegin(ipw);
        docInput(ipw, iMap);
        docEnd(ipw);

        ipw.printf("public static ");
        declareGenerics(ipw, cName, iSize, 1);
        ipw.putf("long %s(%n", name);

        ipw.printf("        Connection c");
        declareInput(ipw, iMap, cName);
        ipw.closeParenthesisLn();
        ipw.printf("        throws SQLException {%n");
        ipw.more();
        ipw.printf("try (PreparedStatement ps = c.prepareStatement(Q_%s)) {%n", kPrg);
        ipw.more();
        setInput(ipw, iMap);
        ipw.printf("ps.setFetchSize(2);%n");
        if (getTimeout() != null) ipw.printf("ps.setQueryTimeout(%d);%n", getTimeout());
        debugAction(ipw, kPrg, iMap, cc);
        ipw.printf("try (ResultSet rs = ps.executeQuery()) {%n");
        ipw.more();
        ipw.printf("if (rs.next()) {%n");
        ipw.more();
        ipw.printf("return rs.getLong(1);%n");
        ipw.orElse();
        ipw.printf("throw ESqlCode.P100.getInstance();%n");
        ipw.ends();
        ipw.ends();
        ipw.ends();
        ipw.ends();
    }
}
