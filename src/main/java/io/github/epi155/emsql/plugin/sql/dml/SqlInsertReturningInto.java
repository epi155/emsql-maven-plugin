package io.github.epi155.emsql.plugin.sql.dml;

import io.github.epi155.emsql.plugin.ClassContext;
import io.github.epi155.emsql.plugin.ComAreaStd;
import io.github.epi155.emsql.plugin.IndentPrintWriter;
import io.github.epi155.emsql.plugin.Tools;
import io.github.epi155.emsql.plugin.sql.JdbcStatement;
import io.github.epi155.emsql.plugin.sql.SqlAction;
import io.github.epi155.emsql.plugin.sql.SqlEnum;
import io.github.epi155.emsql.plugin.sql.SqlParam;
import io.github.epi155.emsql.plugin.sql.dql.ApiSelectSignature;
import io.github.epi155.emsql.plugin.sql.dql.DelegateSelectSignature;
import lombok.Getter;
import lombok.Setter;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlInsertReturningInto extends SqlAction implements ApiSelectSignature {
    private final DelegateSelectSignature delegateSelectSignature;
    @Setter @Getter
    private ComAreaStd input;
    @Setter @Getter
    private ComAreaStd output;

    SqlInsertReturningInto() {
        super();
        this.delegateSelectSignature = new DelegateSelectSignature(this);
    }

    private static final String tmpl =
            "^INSERT INTO (\\w+) [(](.*)[)] VALUES [(](.*)[)] RETURNING (.*) INTO (.*)$";
    private static final Pattern regx = Pattern.compile(tmpl, Pattern.CASE_INSENSITIVE);
    @Override
    public JdbcStatement sql(Map<String, SqlEnum> fields) throws MojoExecutionException {
        String nText = Tools.oneLine(getExecSql());
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
            throw new MojoExecutionException("Invalid query format: "+ getExecSql());
        }
    }

    @Override
    public void writeMethod(IndentPrintWriter ipw, String name, JdbcStatement jdbc, String kPrg, ClassContext cc) {
        delegateSelectSignature.signature(ipw, jdbc, name);

        if (jdbc.getOutSize() == 1) {
            // oMap.get(1) may be NULL, the output parameter is NOT the first one
            jdbc.getOMap().forEach((k,v) -> ipw.putf("%s %s(%n", v.getType().getPrimitive(), name));
        } else {
            ipw.putf("O %s(%n", name);
        }

        ipw.printf("        final Connection c");
        declareInput(ipw, jdbc.getIMap(), Tools.capitalize(name));
        declareOutput(ipw, jdbc.getOutSize(), cc);
        ipw.more();
        ipw.printf("try (CallableStatement ps = c.prepareCall(Q_%s)) {%n", kPrg);
        ipw.more();
        setInput(ipw, jdbc.getIMap());
        registerOut(ipw, jdbc.getOMap());
        if (getTimeout() != null) ipw.printf("ps.setQueryTimeout(%d);%n", getTimeout());
        debugAction(ipw, kPrg, jdbc.getIMap(), cc);
        ipw.printf("ps.execute();%n");
        getOutput(ipw, jdbc.getOMap(), cc);
        if (jdbc.getOutSize()>1)
            ipw.printf("return o;%n");
        ipw.ends();
        ipw.ends();
    }

}
