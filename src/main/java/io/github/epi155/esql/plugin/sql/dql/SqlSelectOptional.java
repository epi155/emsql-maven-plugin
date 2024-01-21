package io.github.epi155.esql.plugin.sql.dql;

import io.github.epi155.esql.plugin.IndentPrintWriter;
import io.github.epi155.esql.plugin.sql.SqlEnum;
import io.github.epi155.esql.plugin.sql.SqlParam;
import io.github.epi155.esql.plugin.Tools;
import io.github.epi155.esql.plugin.sql.JdbcStatement;
import io.github.epi155.esql.plugin.sql.SqlAction;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.maven.plugin.MojoExecutionException;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SqlSelectOptional extends SqlAction {
    private Map<String, SqlEnum> inFields = new HashMap<>();
    private Map<String, SqlEnum> outFields = new HashMap<>();

    private static final String tmpl =
            "^SELECT (.*) (INTO (.*)) FROM (.*)$";
    private static final Pattern regx = Pattern.compile(tmpl, Pattern.CASE_INSENSITIVE);
    @Override
    public JdbcStatement sql() throws MojoExecutionException {
        String nText = Tools.oneLine(getQuery());
        Matcher m = regx.matcher(nText);
        if (m.find()) {
            String sFld = m.group(1);
            String sInto = m.group(3);
            String sTables = m.group(4);
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
        set.add("io.github.epi155.esql.runtime.ESqlCode");
        set.add("java.util.Optional");

        Map<Integer, SqlParam> iMap = jdbc.getIMap();
        Map<Integer, SqlParam> oMap = jdbc.getOMap();
        int iSize = iMap.size();
        int oSize = oMap.size();
        if (oSize < 1) throw new IllegalStateException("Invalid output parameter number");
        String cName = Tools.capitalize(name);
        docBegin(ipw);
        docInput(ipw, iMap);
        docOutput(ipw, oMap);
        docEnd(ipw);

        ipw.putf("public static ");
        declareGenerics(ipw, cName, iSize, oSize);
        if (oSize == 1) {
            String oType = oMap.get(1).getType().getAccess();
            ipw.putf("Optional<%s> %s(%n", oType, name);
        } else {
            ipw.putf("Optional<O> %s(%n", name);
        }

        ipw.printf("        Connection c");
        declareInput(ipw, iMap, cName);
        declareOutput(ipw, oSize, set);
        ipw.more();
        ipw.printf("try (PreparedStatement ps = c.prepareStatement(Q_%s)) {%n", kPrg);
        ipw.more();
        setInput(ipw, iMap);
        ipw.printf("ps.setFetchSize(2);%n");
        if (getTimeout() != null) ipw.printf("ps.setQueryTimeout(%d);%n", getTimeout());
        ipw.printf("try (ResultSet rs = ps.executeQuery()) {%n");
        ipw.more();
        ipw.printf("if (rs.next()) {%n");
        ipw.more();
        fetch(ipw, oMap, set);
        ipw.printf("if (rs.next()) {%n");
        ipw.more();
        ipw.printf("throw ESqlCode.N811.getInstance();%n");
        ipw.orElse();
        ipw.printf("return Optional.of(o);%n");
        ipw.ends();
        ipw.orElse();
        ipw.printf("return Optional.empty();%n");
        ipw.ends();
        ipw.ends();
        ipw.ends();
        ipw.ends();

    }
}
