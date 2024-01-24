package io.github.epi155.esql.plugin.sql.dml;

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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SqlInsertReturningInto extends SqlAction {
    private ComAreaStd input;
    private ComAreaStd output;

    private static final String tmpl =
            "^INSERT INTO (\\w+) [(](.*)[)] VALUES [(](.*)[)] RETURNING (.*) INTO (.*)$";
    private static final Pattern regx = Pattern.compile(tmpl, Pattern.CASE_INSENSITIVE);
    @Override
    public JdbcStatement sql(Map<String, SqlEnum> fields) throws MojoExecutionException {
        String nText = Tools.oneLine(getQuery());
        Matcher m = regx.matcher(nText);
        if (m.find()) {
            String sTable = m.group(1);
            String iCols  = m.group(2).trim();
            String iParms = m.group(3).trim();
            String oCols  = m.group(4).trim();
            String sInto = m.group(5).trim();
            String oText = "BEGIN INSERT INTO "+sTable+" ( "+iCols+" ) VALUES ( "+iParms+" ) RETURNING "+oCols + " INTO " + sInto + " ; END;";
            Map<Integer, SqlParam> oMap = Tools.mapPlaceholder(sInto, fields);
            Map<String,SqlEnum> inpFields = new HashMap<>();
            Map<String,SqlEnum> outFields = new HashMap<>();
            oMap.forEach((k,v) -> outFields.put(v.getName(), v.getType()));
            fields.forEach((k,v) -> {
                if (! outFields.containsKey(k)) {
                    inpFields.put(k,v);
                }
            });
            return Tools.replacePlaceholder(oText, inpFields, outFields);

        } else {
            throw new MojoExecutionException("Invalid query format: "+ getQuery());
        }
    }

    @Override
    public void writeMethod(IndentPrintWriter ipw, String name, JdbcStatement jdbc, String kPrg, ClassContext cc) {
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
            // oMap.get(1) may be NULL, the output parameter is NOT the first one
            oMap.forEach((k,v) -> ipw.putf("%s %s(%n", v.getType().getRaw(), name));
        } else {
            ipw.putf("O %s(%n", name);
        }

        ipw.printf("        Connection c");
        declareInput(ipw, iMap, cName);
        declareOutput(ipw, oSize, cc);
        ipw.more();
        ipw.printf("try (CallableStatement ps = c.prepareCall(Q_%s)) {%n", kPrg);
        ipw.more();
        setInput(ipw, iMap);
        registerOut(ipw, oMap);
        if (getTimeout() != null) ipw.printf("ps.setQueryTimeout(%d);%n", getTimeout());
        debugAction(ipw, kPrg, iMap, cc);
        ipw.printf("ps.execute();%n");
        getOutput(ipw, oMap, cc);
        ipw.printf("return o;%n");
        ipw.ends();
        ipw.ends();
    }

}
