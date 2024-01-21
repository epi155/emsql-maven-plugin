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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SqlInsertReturningInto extends SqlAction {
    private Map<String, SqlEnum> inFields = new HashMap<>();
    private Map<String, SqlEnum> outFields = new LinkedHashMap<>();

    private static final String tmpl =
            "^INSERT INTO (\\w+) [(](.*)[)] VALUES [(](.*)[)] RETURNING (.*) INTO (.*)$";
    private static final Pattern regx = Pattern.compile(tmpl, Pattern.CASE_INSENSITIVE);
    @Override
    public JdbcStatement sql() throws MojoExecutionException {
        String nText = Tools.oneLine(getQuery());
        Matcher m = regx.matcher(nText);
        if (m.find()) {
            String sTable = m.group(1);
            String iCols  = m.group(2).trim();
            String iParms = m.group(3).trim();
            String oCols  = m.group(4).trim();
            String oParms = m.group(5).trim();
            String oText = "BEGIN INSERT INTO "+sTable+" ( "+iCols+" ) VALUES ( "+iParms+" ) RETURNING "+oCols + " INTO " + oParms + " ; END;";
            return Tools.replacePlaceholder(oText, inFields, outFields);
        } else {
            throw new MojoExecutionException("Invalid query format: "+ getQuery());
        }
    }

    @Override
    public void writeMethod(IndentPrintWriter ipw, String name, JdbcStatement jdbc, String kPrg, Set<String> set) {
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
        if (oSize > 1) {
            ipw.printf("public static <R extends %s"+RESPONSE+"> R %s(%n", cName, name);
        } else {
            if (isReflect() && iSize > IMAX) {
                oMap.forEach((k,v) -> ipw.printf("public static <T> %s %s(%n", v.getType().getRaw(), name));
            } else {
                oMap.forEach((k,v) -> ipw.printf("public static %s %s(%n", v.getType().getRaw(), name));
            }
        }
        ipw.printf("        Connection c");
        declareInput(ipw, iMap, cName);
        declareOutput(ipw, oSize, set);
        ipw.more();
        ipw.printf("try (CallableStatement ps = c.prepareCall(Q_%s)) {%n", kPrg);
        ipw.more();
        setInput(ipw, iMap);
        registerOut(ipw, oMap);
        if (getTimeout() != null) ipw.printf("ps.setQueryTimeout(%d);%n", getTimeout());
        ipw.printf("ps.execute();%n");
        getOutput(ipw, oMap, set);
        ipw.printf("return o;%n");
        ipw.ends();
        ipw.ends();
    }

}
