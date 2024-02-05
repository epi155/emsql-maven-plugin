package io.github.epi155.emsql.plugin.sql.dpl;

import io.github.epi155.emsql.plugin.Tools;
import io.github.epi155.emsql.plugin.sql.JdbcStatement;
import io.github.epi155.emsql.plugin.sql.SqlEnum;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.HashMap;
import java.util.Map;

public class SqlInlineProcedure extends SqlCallProcedure {

    @Override
    public JdbcStatement sql(Map<String, SqlEnum> fields) throws MojoExecutionException {
        String nText = getExecSql();
        String uText = nText.toUpperCase();
        if (!uText.contains("BEGIN") || !uText.contains("END")) {
            throw new MojoExecutionException("Invalid query format: "+ getExecSql());
        }
        Map<String,SqlEnum> inpFields = new HashMap<>();
        Map<String,SqlEnum> outFields = new HashMap<>();
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