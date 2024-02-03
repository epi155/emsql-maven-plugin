package io.github.epi155.emsql.plugin.sql.dml;

import io.github.epi155.emsql.plugin.Tools;
import io.github.epi155.emsql.plugin.sql.JdbcStatement;
import io.github.epi155.emsql.plugin.sql.SqlEnum;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DelegateDelete {
    private final ApiDelete api;
    private static final String tmpl =
            "^DELETE FROM (.*)$";
    private static final Pattern regx = Pattern.compile(tmpl, Pattern.CASE_INSENSITIVE);

    public DelegateDelete(ApiDelete api) {
        this.api = api;
    }

    public JdbcStatement proceed(Map<String, SqlEnum> fields) throws MojoExecutionException {
        String nText = Tools.oneLine(api.getExecSql());
        Matcher m = regx.matcher(nText);
        if (m.find()) {
            String sTables = m.group(1);
            String oText = "DELETE FROM " + sTables;
            Tools.SqlStatement iStmt = Tools.replacePlaceholder(oText, fields);
            return new JdbcStatement(iStmt.getText(), iStmt.getMap(), Map.of());
        } else {
            throw new MojoExecutionException("Invalid query format: "+ api.getExecSql());
        }
    }
}
