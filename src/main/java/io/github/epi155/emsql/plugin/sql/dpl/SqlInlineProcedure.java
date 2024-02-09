package io.github.epi155.emsql.plugin.sql.dpl;

import io.github.epi155.emsql.plugin.Tools;
import io.github.epi155.emsql.plugin.sql.JdbcStatement;
import io.github.epi155.emsql.plugin.sql.SqlKind;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.HashMap;
import java.util.Map;

public class SqlInlineProcedure extends SqlCallProcedure {

    @Override
    public JdbcStatement sql(Map<String, SqlKind> fields) throws MojoExecutionException {
        String nText = getExecSql();
        String uText = nText.toUpperCase();
        int k0 = uText.indexOf("DECLARE");
        int k1 = uText.indexOf("BEGIN");
        int k2 = uText.indexOf("END");
        if (!(k1>=0 && k2>k1 && (k0<0 || k0<k1))) {
            throw new MojoExecutionException("Invalid query format: "+ getExecSql());
        }
        Map<String,SqlKind> inpFields = new HashMap<>();
        Map<String,SqlKind> outFields = new HashMap<>();
        fields.forEach((k,v) -> {
            if (getOutput()!=null && getOutput().getFields().contains(k)) {
                outFields.put(k, v);
            } else {
                inpFields.put(k,v);
            }
        });
        return Tools.replacePlaceholder(nText, inpFields, outFields);
    }
}
