package io.github.epi155.esql.plugin.sql.dql;

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
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SqlCursorForUpdate extends SqlAction {
    private Map<String, SqlEnum> inFields = new HashMap<>();
    private Map<String, SqlEnum> outFields = new HashMap<>();
    private Integer fetchSize;

    private static final String tmpl =
            "^SELECT (.*?) (INTO (.*)) FROM (.*)(\\s+FOR UPDATE OF (.*))?$";
    private static final Pattern regx = Pattern.compile(tmpl, Pattern.CASE_INSENSITIVE);
    @Override
    public JdbcStatement sql() throws MojoExecutionException {
        String nText = Tools.oneLine(getQuery());
        Matcher m = regx.matcher(nText);
        if (m.find()) {
            String sFld = m.group(1);
            String sInto = m.group(3);
            String sTables = m.group(4);
            String sUpdate = m.group(6);
            String oText = "SELECT " + sFld + " FROM " + sTables;
            Tools.SqlStatement iStmt = Tools.replacePlaceholder(oText, inFields);
            @NotNull Map<Integer, SqlParam> oMap = Tools.mapPlaceholder(sInto, outFields);
            return new JdbcStatement(iStmt.getText(), iStmt.getMap(), oMap);
        } else {
            throw new MojoExecutionException("Invalid query format: "+ getQuery());
        }
    }

    @Override
    public void writeMethod(IndentPrintWriter ipw, String name, JdbcStatement jdbc, String kPrg, Set<String> set) {
        Map<Integer, SqlParam> iMap = jdbc.getIMap();
        Map<Integer, SqlParam> oMap = jdbc.getOMap();
        int oSize = oMap.size();
        if (oSize < 1) throw new IllegalStateException("Invalid output parameter number");
        String cName = Tools.capitalize(name);
        if (oSize > 1) {
            ipw.printf("public static <R extends %s"+RESPONSE+"> void loop%1$s(%n", cName);
        } else {
            ipw.printf("public static void loop%s(%n", oMap.get(1).getType().getAccess(), cName);
        }
        ipw.printf("        Connection c");
        declareInput(ipw, iMap, cName);
        declareOutputUpdate(ipw, oSize, oMap.get(1).getType().getAccess(), set);
        ipw.more();
        ipw.printf("if (c.getAutoCommit())%n");
        ipw.more();
        ipw.printf("throw new IllegalStateException(\"Cursor for update require AutoCommit=false\");%n");
        ipw.less();
        ipw.printf("try (PreparedStatement ps = c.prepareStatement(%n");
        ipw.printf("        Q_%s,%n", kPrg);
        ipw.printf("        ResultSet.TYPE_SCROLL_SENSITIVE,%n");
        ipw.printf("        ResultSet.CONCUR_UPDATABLE)) {%n");
        ipw.more();
        setInput(ipw, iMap);
        if (fetchSize != null) ipw.printf("ps.setFetchSize(%d);%n", fetchSize);
        if (getTimeout() != null) ipw.printf("ps.setQueryTimeout(%d);%n", getTimeout());
        ipw.printf("try (ResultSet rs = ps.executeQuery()) {%n");
        ipw.more();
        ipw.printf("while (rs.next()) {%n");
        ipw.more();
        fetch(ipw, oMap, set);
        if (oSize > 1) {
            ipw.printf("Optional<R> or = uo.apply(o);%n");
        } else {
            ipw.printf("Optional<%s> or = uo.apply(o);%n", oMap.get(1).getType().getAccess());
        }
        ipw.printf("if (or.isPresent()) {%n");
        ipw.more();
        if (oSize > 1) {
            ipw.printf("R r = or.get();%n");
        } else {
            ipw.printf("%s r = or.get();%n", oMap.get(1).getType().getAccess());
        }
        update(ipw, oMap, set);
        ipw.printf("rs.updateRow();%n");
        ipw.ends();
        ipw.ends();
        ipw.ends();
        ipw.ends();
        ipw.ends();
    }
    @Override
    public void writeResponse(IndentPrintWriter ipw, String cMethodName, Collection<SqlParam> sp) {
        if (sp.size()<=1) return;
        ipw.printf("public interface %s"+RESPONSE+" {%n", cMethodName);
        ipw.more();
        sp.forEach(p -> {
            String cName = Tools.capitalize(p.getName());
            String claz = p.getType().getRaw();
            ipw.printf("void set%s(%s s);%n", cName, claz);
            ipw.printf("%s get%s();%n", claz, cName);
        });
        ipw.ends();
    }

}
