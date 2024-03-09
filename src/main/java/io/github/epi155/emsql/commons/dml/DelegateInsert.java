package io.github.epi155.emsql.commons.dml;

import io.github.epi155.emsql.api.InvalidQueryException;
import io.github.epi155.emsql.api.SqlDataType;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.Tools;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DelegateInsert {
    private final ApiInsert api;

    public DelegateInsert(ApiInsert api) {
        this.api = api;
    }
    private static final String tmpl =
            "^INSERT INTO (\\w+) \\((.*)\\) VALUES [(](.*)[)]$";
    private static final Pattern regx = Pattern.compile(tmpl, Pattern.CASE_INSENSITIVE);

    public JdbcStatement proceed(Map<String, SqlDataType> fields, boolean enableList) throws InvalidQueryException {
        String nText = Tools.oneLine(api.getExecSql());
        Matcher m = regx.matcher(nText);
        if (m.find()) {
            String sTable = m.group(1);
            String sCols  = m.group(2).trim();
            String sParms = m.group(3).trim();
            String oText = "INSERT INTO "+sTable+" ( "+sCols+" ) VALUES ( "+sParms+" )";
            Tools.SqlStatement iStmt = Tools.replacePlaceholder(oText, fields, enableList);
            return new JdbcStatement(iStmt.getText(), iStmt.getMap(), Map.of());
        } else {
            throw new InvalidQueryException("Invalid query format: "+ api.getExecSql());
        }
    }
}
