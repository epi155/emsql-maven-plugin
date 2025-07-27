package io.github.epi155.emsql.commons.dql;

import io.github.epi155.emsql.api.InvalidQueryException;
import io.github.epi155.emsql.api.SqlDataType;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.SqlParam;
import io.github.epi155.emsql.commons.Tools;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DelegateSelectFields {
    private static final String TMPL =
            "^SELECT (.*) (INTO\\s+(:\\w+[.\\w+]*(\\s*,\\s*:\\w+[.\\w+]*)*))\\s+FROM (.*)$";
    private static final Pattern regx = Pattern.compile(TMPL, Pattern.CASE_INSENSITIVE);
    private final ApiSelectFields api;
    public DelegateSelectFields(ApiSelectFields api) {
        this.api = api;
    }

    public JdbcStatement sql(Map<String, SqlDataType> fields) throws InvalidQueryException {
        String nText = Tools.oneLine(api.getExecSql());
        Matcher m = regx.matcher(nText);
        if (m.find()) {
            String sFld = m.group(1);
            String sInto = m.group(3);
            String sTables = m.group(5);
            String oText = "SELECT " + sFld + " FROM " + sTables;
            Tools.SqlStatement iStmt = Tools.replacePlaceholder(oText, fields, true);
            @NotNull Map<Integer, SqlParam> oMap = Tools.mapPlaceholder(sInto, fields);
            return new JdbcStatement(iStmt.getText(), iStmt.getMap(), oMap);
        } else {
            throw new InvalidQueryException("Invalid query format: " + api.getExecSql());
        }
    }
}
