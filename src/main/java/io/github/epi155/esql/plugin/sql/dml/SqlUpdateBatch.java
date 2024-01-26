package io.github.epi155.esql.plugin.sql.dml;

import io.github.epi155.esql.plugin.ClassContext;
import io.github.epi155.esql.plugin.ComAreaStd;
import io.github.epi155.esql.plugin.IndentPrintWriter;
import io.github.epi155.esql.plugin.Tools;
import io.github.epi155.esql.plugin.sql.JdbcStatement;
import io.github.epi155.esql.plugin.sql.SqlAction;
import io.github.epi155.esql.plugin.sql.SqlEnum;
import io.github.epi155.esql.plugin.sql.SqlParam;
import lombok.Getter;
import lombok.Setter;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Setter
public class SqlUpdateBatch extends SqlAction {
    @Getter
    private ComAreaStd input;
    private int batchSize = 1024;

    private static final String tmpl =
            "^UPDATE (\\w+) SET (.*) WHERE (.*)$";
    private static final Pattern regx = Pattern.compile(tmpl, Pattern.CASE_INSENSITIVE);
    @Override
    public JdbcStatement sql(Map<String, SqlEnum> fields) throws MojoExecutionException {
        String nText = Tools.oneLine(getExecSql());
        Matcher m = regx.matcher(nText);
        if (m.find()) {
            String sTable = m.group(1);
            String sAlter = m.group(2);
            String sWhere = m.group(3);
            String oText = "UPDATE " + sTable + " SET " + sAlter + " WHERE " + sWhere;
            Tools.SqlStatement iStmt = Tools.replacePlaceholder(oText, fields);
            return new JdbcStatement(iStmt.getText(), iStmt.getMap(), Map.of());
        } else {
            throw new MojoExecutionException("Invalid query format: "+ getExecSql());
        }
    }

    @Override
    public void writeMethod(IndentPrintWriter ipw, String name, JdbcStatement jdbc, String kPrg, ClassContext cc) {
        Map<Integer, SqlParam> iMap = jdbc.getIMap();
        int iSize = iMap.size();
        if (1<iSize && iSize<=IMAX) {
            cc.add("io.github.epi155.esql.runtime.ESqlUpdateBatch"+iSize);
        } else {
            cc.add("io.github.epi155.esql.runtime.ESqlUpdateBatch");
        }
        String cName = Tools.capitalize(name);
        docBegin(ipw);
        docInput(ipw, iMap);
        docEnd(ipw);
        declareNewInstance(ipw, "ESqlUpdateBatch", iMap, cName);
        ipw.more();
        ipw.printf("PreparedStatement ps = c.prepareStatement(Q_%s);%n", kPrg);
        if (getTimeout() != null) ipw.printf("ps.setQueryTimeout(%d);%n", getTimeout());
        declareReturnNew(ipw, "ESqlUpdateBatch", iMap, batchSize);
        ipw.more();
        ipw.printf("@Override%n");
        ipw.printf("public void lazyUpdate(%n");
        declareInputBatch(ipw, iMap);
        ipw.closeParenthesisLn();
        ipw.printf("        throws SQLException {%n");
        ipw.more();
        setInput(ipw, iMap);
        ipw.printf("addBatch();%n");
        ipw.ends();
        ipw.less();
        ipw.printf("};%n");
        ipw.ends();
    }
}
