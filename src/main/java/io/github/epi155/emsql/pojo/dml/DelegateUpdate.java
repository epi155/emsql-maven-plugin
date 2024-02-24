package io.github.epi155.emsql.pojo.dml;

import io.github.epi155.emsql.api.SqlDataType;
import io.github.epi155.emsql.pojo.JdbcStatement;
import io.github.epi155.emsql.pojo.Tools;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DelegateUpdate {
    private final ApiUpdate api;
    private static final String tmpl =
            "^UPDATE (\\w+) SET (.*) WHERE (.*)$";
    private static final Pattern regx = Pattern.compile(tmpl, Pattern.CASE_INSENSITIVE);

    public DelegateUpdate(ApiUpdate api) {
        this.api = api;
    }

    public JdbcStatement proceed(Map<String, SqlDataType> fields) throws MojoExecutionException {
        String nText = Tools.oneLine(api.getExecSql());
        Matcher m = regx.matcher(nText);
        if (m.find()) {
            String sTable = m.group(1);
            String sAlter = m.group(2);
            String sWhere = m.group(3);
            String oText = "UPDATE " + sTable + " SET " + sAlter + " WHERE " + sWhere;
            Tools.SqlStatement iStmt = Tools.replacePlaceholder(oText, fields);
            return new JdbcStatement(iStmt.getText(), iStmt.getMap(), Map.of());
        } else {
            throw new MojoExecutionException("Invalid query format: "+ api.getExecSql());
        }
    }
}
