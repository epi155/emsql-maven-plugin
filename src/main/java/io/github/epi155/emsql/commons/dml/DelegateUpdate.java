package io.github.epi155.emsql.commons.dml;

import io.github.epi155.emsql.api.InvalidQueryException;
import io.github.epi155.emsql.api.SqlDataType;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.Tools;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DelegateUpdate {
    private static final String tmpl =
            "^UPDATE (\\w+) ((\\w+)\\s+)?SET (.*) WHERE (.*)$";
    private static final Pattern regx = Pattern.compile(tmpl, Pattern.CASE_INSENSITIVE);
    private final ApiUpdate api;

    public DelegateUpdate(ApiUpdate api) {
        this.api = api;
    }

    public JdbcStatement proceed(Map<String, SqlDataType> fields, boolean enableList) throws InvalidQueryException {
        String nText = Tools.oneLine(api.getExecSql());
        Matcher m = regx.matcher(nText);
        if (m.find()) {
            String sTable = m.group(1);
            String sAlias = m.group(3);
            String sAlter = m.group(4);
            String sWhere = m.group(5);
            String oText = "UPDATE " + sTable + (sAlias == null ? "" : " " + sAlias) + " SET " + sAlter + " WHERE " + sWhere;
            Tools.SqlStatement iStmt = Tools.replacePlaceholder(oText, fields, enableList);
            return new JdbcStatement(iStmt.getText(), iStmt.getMap(), Map.of());
        } else {
            throw new InvalidQueryException("Invalid query format: " + api.getExecSql());
        }
    }
}
