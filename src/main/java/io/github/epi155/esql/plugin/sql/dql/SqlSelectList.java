package io.github.epi155.esql.plugin.sql.dql;

import io.github.epi155.esql.plugin.*;
import io.github.epi155.esql.plugin.sql.JdbcStatement;
import io.github.epi155.esql.plugin.sql.SqlAction;
import io.github.epi155.esql.plugin.sql.SqlEnum;
import io.github.epi155.esql.plugin.sql.SqlParam;
import lombok.Getter;
import lombok.Setter;
import org.apache.maven.plugin.MojoExecutionException;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Setter
public class SqlSelectList extends SqlAction {
    @Getter
    private ComAreaStd input;
    @Getter
    private ComAreaLst output;
    private Integer fetchSize;

    private static final String tmpl =
            "^SELECT (.*) (INTO (.*?)) FROM (.*)$";
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
        cc.add("java.util.List");
        cc.add("java.util.ArrayList");

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

        ipw.printf("public static ");
        declareGenerics(ipw, cName, iSize, oSize);
        if (oSize == 1) {
            String oType = oMap.get(1).getType().getWrapper();
            ipw.putf("List<%s> %s(%n", oType, name);
        } else {
            ipw.putf("List<O> %s(%n", name);
        }

        ipw.printf("        Connection c");
        declareInput(ipw, iMap, cName);
        declareOutput(ipw, oSize, cc);
        ipw.more();
        ipw.printf("try (PreparedStatement ps = c.prepareStatement(Q_%s)) {%n", kPrg);
        ipw.more();
        setInput(ipw, iMap);
        if (fetchSize != null) ipw.printf("ps.setFetchSize(%d);%n", fetchSize);
        if (getTimeout() != null) ipw.printf("ps.setQueryTimeout(%d);%n", getTimeout());
        debugAction(ipw, kPrg, iMap, cc);
        ipw.printf("try (ResultSet rs = ps.executeQuery()) {%n");
        ipw.more();
        if (oSize == 1) {
            ipw.printf("List<%s> list = new ArrayList<>();%n", oMap.get(1).getType().getWrapper());
        } else {
            ipw.printf("List<O> list = new ArrayList<>();%n");
        }
        ipw.printf("while (rs.next()) {%n");
        ipw.more();
        fetch(ipw, oMap, cc);
        ipw.printf("list.add(o);%n");
        ipw.ends();
        ipw.printf("return list;%n");
        ipw.ends();
        ipw.ends();
        ipw.ends();
    }
}
