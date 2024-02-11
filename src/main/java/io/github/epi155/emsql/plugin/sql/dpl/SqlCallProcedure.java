package io.github.epi155.emsql.plugin.sql.dpl;

import io.github.epi155.emsql.plugin.ComAreaDef;
import io.github.epi155.emsql.plugin.ComAreaStd;
import io.github.epi155.emsql.plugin.IndentPrintWriter;
import io.github.epi155.emsql.plugin.Tools;
import io.github.epi155.emsql.plugin.sql.JdbcStatement;
import io.github.epi155.emsql.plugin.sql.SqlAction;
import io.github.epi155.emsql.plugin.sql.SqlKind;
import io.github.epi155.emsql.plugin.sql.dql.ApiSelectSignature;
import io.github.epi155.emsql.plugin.sql.dql.DelegateCallSignature;
import lombok.Getter;
import lombok.Setter;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.epi155.emsql.plugin.Tools.mc;

public class SqlCallProcedure extends SqlAction implements ApiSelectSignature {
    @Setter
    @Getter
    private ComAreaStd input;
    @Setter @Getter
    private ComAreaDef output;
    private final DelegateCallSignature delegateSelectSignature;
    protected SqlCallProcedure() {
        super();
        this.delegateSelectSignature = new DelegateCallSignature(this);
    }

    private static final String tmpl =
            "^CALL (\\w+)\\((.*)\\)$";
    private static final Pattern regx = Pattern.compile(tmpl, Pattern.CASE_INSENSITIVE);


    @Override
    public JdbcStatement sql(Map<String, SqlKind> fields) throws MojoExecutionException {
        String nText = Tools.oneLine(getExecSql());
        Matcher m = regx.matcher(nText);
        if (m.find()) {
            Map<String,SqlKind> inpFields = new HashMap<>();
            Map<String,SqlKind> outFields = new HashMap<>();
            fields.forEach((k,v) -> {
                if (output!=null && output.getFields().contains(k)) {
                    outFields.put(k, v);
                } else {
                    inpFields.put(k,v);
                }
            });
            return Tools.replacePlaceholder(nText, inpFields, outFields);

        } else {
            throw new MojoExecutionException("Invalid query format: "+ getExecSql());
        }
    }

    public void writeMethod(IndentPrintWriter ipw, String name, JdbcStatement jdbc, String kPrg) {
        delegateSelectSignature.signature(ipw, jdbc, name);

        if (mc.oSize() == 0) {
            ipw.putf("void %s(%n", name);
        } else if (mc.oSize() == 1) {
            // oMap.get(1) may be NULL, the output parameter is NOT the first one
            jdbc.getOMap().forEach((k,v) -> ipw.putf("%s %s(%n", v.getType().getPrimitive(), name));
        } else {
            ipw.putf("O %s(%n", name);
        }

        ipw.printf("        final Connection c");
        declareInput(ipw, jdbc);
        declareOutput(ipw);
        ipw.more();
        ipw.printf("try (CallableStatement ps = c.prepareCall(Q_%s)) {%n", kPrg);
        ipw.more();
        setInput(ipw, jdbc);
        registerOut(ipw, jdbc.getOMap());
        setQueryHints(ipw);
        debugAction(ipw, kPrg, jdbc);
        ipw.printf("ps.execute();%n");
        getOutput(ipw, jdbc.getOMap());
        if (mc.oSize()>1)
            ipw.printf("return o;%n");
        ipw.ends();
        ipw.ends();
    }
}
