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
public class SqlInsert extends SqlAction {
    private Map<String, SqlEnum> inFields = new HashMap<>();

    private static final String tmpl =
            "^INSERT INTO (\\w+) [(](.*)[)] VALUES [(](.*)[)]$";
    private static final Pattern regx = Pattern.compile(tmpl, Pattern.CASE_INSENSITIVE);
    @Override
    public JdbcStatement sql() throws MojoExecutionException {
        String nText = Tools.oneLine(getQuery());
        Matcher m = regx.matcher(nText);
        if (m.find()) {
            String sTable = m.group(1);
            String sCols  = m.group(2).trim();
            String sParms = m.group(3).trim();
            String oText = "INSERT INTO "+sTable+" ( "+sCols+" ) VALUES ( "+sParms+" )";
            Tools.SqlStatement iStmt = Tools.replacePlaceholder(oText, inFields);
            return new JdbcStatement(iStmt.getText(), iStmt.getMap(), Map.of());
        } else {
            throw new MojoExecutionException("Invalid query format: "+ getQuery());
        }
    }

    @Override
    public void writeMethod(IndentPrintWriter ipw, String name, JdbcStatement jdbc, String kPrg, Set<String> set) {
        Map<Integer, SqlParam> iMap = jdbc.getIMap();
        String cName = Tools.capitalize(name);
        docBegin(ipw);
        docInput(ipw, iMap);
        docEnd(ipw);
        if (isReflect() && iMap.size() > IMAX) {
            ipw.printf("public static <T> int %s(%n", name);
        } else {
            ipw.printf("public static int %s(%n", name);
        }
        ipw.printf("        Connection c");
        declareInput(ipw, iMap, cName);
        ipw.closeParenthesisLn();
        ipw.printf("        throws SQLException {%n");
        ipw.more();
        ipw.printf("try (PreparedStatement ps = c.prepareStatement(Q_%s)) {%n", kPrg);
        ipw.more();
        setInput(ipw, iMap);
        if (getTimeout() != null) ipw.printf("ps.setQueryTimeout(%d);%n", getTimeout());
        ipw.printf("return ps.executeUpdate();%n");
        ipw.ends();
        ipw.ends();
    }
}
