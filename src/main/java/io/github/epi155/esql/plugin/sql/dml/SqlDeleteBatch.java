package io.github.epi155.esql.plugin.sql.dml;

import io.github.epi155.esql.plugin.IndentPrintWriter;
import io.github.epi155.esql.plugin.SqlEnum;
import io.github.epi155.esql.plugin.SqlParam;
import io.github.epi155.esql.plugin.Tools;
import io.github.epi155.esql.plugin.sql.JdbcStatement;
import io.github.epi155.esql.plugin.sql.SqlAction;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SqlDeleteBatch extends SqlAction {
    private Map<String, SqlEnum> inFields = new HashMap<>();
    private int batchSize = 1024;

    private static final String tmpl =
            "^DELETE FROM (.*)$";
    private static final Pattern regx = Pattern.compile(tmpl, Pattern.CASE_INSENSITIVE);
    @Override
    public JdbcStatement sql() throws MojoExecutionException {
        String nText = Tools.oneLine(getQuery());
        Matcher m = regx.matcher(nText);
        if (m.find()) {
            String sTables = m.group(1);
            String oText = "DELETE FROM " + sTables;
            Tools.SqlStatement iStmt = Tools.replacePlaceholder(oText, inFields);
            return new JdbcStatement(iStmt.getText(), iStmt.getMap(), Map.of());
        } else {
            throw new MojoExecutionException("Invalid query format: "+ getQuery());
        }
    }

    @Override
    public void writeMethod(IndentPrintWriter ipw, String name, JdbcStatement jdbc, String kPrg, Set<String> set) {
        Map<Integer, SqlParam> iMap = jdbc.getIMap();
        int iSize = iMap.size();
        if (1<iSize && iSize<=IMAX) {
            set.add("io.github.epi155.esql.runtime.ESqlDeleteBatch"+iSize);
        } else {
            set.add("io.github.epi155.esql.runtime.ESqlDeleteBatch");
        }
        String cName = Tools.capitalize(name);
        docBegin(ipw);
        docInput(ipw, iMap);
        docEnd(ipw);
        declareNewInstance(ipw, "ESqlDeleteBatch", iMap, cName);
        ipw.more();
        declareReturnNew(ipw, "ESqlDeleteBatch", iMap, cName);
        ipw.more();
        ipw.printf("private final PreparedStatement ps;%n");
        ipw.printf("private int pending = 0;%n");
        ipw.printf("{%n");
        ipw.more();
        ipw.printf("this.ps = c.prepareStatement(Q_%s);%n", kPrg);
        ipw.ends();
        ipw.printf("@Override%n");
        ipw.printf("public void lazyDelete(%n");
        declareInputBatch(ipw, iMap, cName);
        ipw.closeParenthesisLn();
        ipw.printf("        throws SQLException {%n");
        ipw.more();
        setInput(ipw, iMap);
        ipw.printf("ps.addBatch();%n");
        ipw.printf("if (++pending > %d)%n", batchSize);
        ipw.more();
        ipw.printf("flush();%n");
        ipw.less();
        ipw.ends();
        ipw.printf("@Override%n");
        ipw.printf("public void flush() throws SQLException {%n");
        ipw.more();
        ipw.printf("if (pending > 0) {%n");
        ipw.more();
        if (getTimeout() != null) ipw.printf("ps.setQueryTimeout(%d);%n", getTimeout());
        ipw.printf("ps.executeBatch();%n");
        ipw.ends();
        ipw.printf("pending = 0;%n");
        ipw.ends();
        ipw.printf("@Override%n");
        ipw.printf("public void close() throws SQLException {%n");
        ipw.more();
        ipw.printf("flush();%n");
        ipw.printf("ps.close();%n");
        ipw.ends();
        ipw.less();
        ipw.printf("};%n");
        ipw.ends();
    }


}
