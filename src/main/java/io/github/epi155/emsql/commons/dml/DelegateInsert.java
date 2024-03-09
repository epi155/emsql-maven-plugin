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
    private static final String tmpl1 =
            "^INSERT INTO (\\w+) \\((.*)\\) VALUES [(](.*)[)]$";
    private static final String tmpl2 =
            "^INSERT INTO (\\w+) \\((.*)\\) SELECT (.*) FROM (.*)$";
    private static final Pattern regx1 = Pattern.compile(tmpl1, Pattern.CASE_INSENSITIVE);
    private static final Pattern regx2 = Pattern.compile(tmpl2, Pattern.CASE_INSENSITIVE);

    public JdbcStatement proceed(Map<String, SqlDataType> fields, boolean enableList) throws InvalidQueryException {
        String nText = Tools.oneLine(api.getExecSql());
        Matcher m1 = regx1.matcher(nText);
        if (m1.find()) {
            String sTable = m1.group(1);
            String sCols  = m1.group(2).trim();
            String sParms = m1.group(3).trim();
            String oText = "INSERT INTO "+sTable+" ( "+sCols+" ) VALUES ( "+sParms+" )";
            Tools.SqlStatement iStmt = Tools.replacePlaceholder(oText, fields, false);
            return new JdbcStatement(iStmt.getText(), iStmt.getMap(), Map.of());
        }
        Matcher m2 = regx2.matcher(nText);
        if (m2.find()) {
            String sTable = m2.group(1);
            String sDCols = m2.group(2).trim();
            String sSCols = m2.group(3).trim();
            String sParms = m2.group(4).trim();
            String oText = "INSERT INTO "+sTable+" ( "+sDCols+" ) SELECT "+sSCols+" FROM "+sParms;
            Tools.SqlStatement iStmt = Tools.replacePlaceholder(oText, fields, enableList);
            return new JdbcStatement(iStmt.getText(), iStmt.getMap(), Map.of());
        }
        throw new InvalidQueryException("Invalid query format: "+ api.getExecSql());
    }
}
