package io.github.epi155.esql.plugin.sql.dml;

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
import lombok.val;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SqlInsertReturnGeneratedKeys extends SqlAction {
    private Map<String, SqlEnum> inFields = new HashMap<>();
    private Map<String, SqlEnum> outFields = new LinkedHashMap<>();

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
            Map<Integer, SqlParam> oMap = new LinkedHashMap<>();
            int k=0;
            for(val e: outFields.entrySet()) {
                oMap.put(++k, new SqlParam(e.getKey(), e.getValue()));
            }
            return new JdbcStatement(iStmt.getText(), iStmt.getMap(), oMap);
        } else {
            throw new MojoExecutionException("Invalid query format: "+ getQuery());
        }
    }

    @Override
    public void writeMethod(IndentPrintWriter ipw, String name, JdbcStatement jdbc, String kPrg, Set<String> set) {
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
        ipw.printf("try (PreparedStatement ps = c.prepareStatement(Q_%s, Statement.RETURN_GENERATED_KEYS)) {%n", kPrg);
        ipw.more();
        setInput(ipw, iMap);
        if (getTimeout() != null) ipw.printf("ps.setQueryTimeout(%d);%n", getTimeout());
        ipw.printf("ps.executeUpdate();%n");
        ipw.printf("ResultSet rs = ps.getGeneratedKeys();%n");
        ipw.printf("if (rs.next()) {%n");
        ipw.more();
        ipw.printf("if (rs.getMetaData().getColumnType(1) == Types.ROWID) throw new IllegalArgumentException(\"Unsupported operation\");%n");
        fetch(ipw, oMap, set);
        ipw.printf("return Optional.of(o);%n");
        ipw.ends();
        ipw.printf("return Optional.empty();%n");
        ipw.ends();
        ipw.ends();
    }
}
