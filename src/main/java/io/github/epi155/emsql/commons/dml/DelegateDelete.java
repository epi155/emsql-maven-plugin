package io.github.epi155.emsql.commons.dml;

import io.github.epi155.emsql.api.InvalidQueryException;
import io.github.epi155.emsql.api.SqlDataType;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.Tools;

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

    public JdbcStatement proceed(Map<String, SqlDataType> fields) throws InvalidQueryException {
        String nText = Tools.oneLine(api.getExecSql());
        Matcher m = regx.matcher(nText);
        if (m.find()) {
            String sTables = m.group(1);
            String oText = "DELETE FROM " + sTables;
            Tools.SqlStatement iStmt = Tools.replacePlaceholder(oText, fields);
            return new JdbcStatement(iStmt.getText(), iStmt.getMap(), Map.of());
        } else {
            throw new InvalidQueryException("Invalid query format: "+ api.getExecSql());
        }
    }
}
