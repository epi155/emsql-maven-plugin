package io.github.epi155.emsql.spring.dml;

import io.github.epi155.emsql.api.*;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.SqlParam;
import io.github.epi155.emsql.commons.Tools;
import io.github.epi155.emsql.commons.dql.ApiDocSignature;
import io.github.epi155.emsql.spring.SpringAction;
import io.github.epi155.emsql.spring.dql.DelegateSelectSignature;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.epi155.emsql.commons.Contexts.cc;
import static io.github.epi155.emsql.commons.Contexts.mc;

public class SqlInsertReturnGeneratedKeys extends SpringAction implements ApiDocSignature, InsertReturnGeneratedKeysModel {
    private static final String tmpl =
            "^INSERT INTO (\\w+) \\((.*)\\) VALUES \\((.*)\\)$";
    private static final Pattern regx = Pattern.compile(tmpl, Pattern.CASE_INSENSITIVE);
    private final DelegateSelectSignature delegateSelectSignature;
    @Setter
    @Getter
    private InputModel input;
    @Setter
    @Getter
    private OutFieldsModel output;
    public SqlInsertReturnGeneratedKeys() {
        super();
        this.delegateSelectSignature = new DelegateSelectSignature(this);
    }

    @Override
    public JdbcStatement sql(Map<String, SqlDataType> fields) throws InvalidQueryException {
        String nText = Tools.oneLine(getExecSql());
        Matcher m = regx.matcher(nText);
        if (m.find()) {
            String sTable = m.group(1);
            String sCols = m.group(2).trim();
            String sParms = m.group(3).trim();
            String oText = "INSERT INTO " + sTable + " ( " + sCols + " ) VALUES ( " + sParms + " )";
            Tools.SqlStatement iStmt = Tools.replacePlaceholder(oText, fields, true);
            Map<Integer, SqlParam> oMap = new LinkedHashMap<>();
            int k = 0;
            for (val e : output.getFields()) {
                oMap.put(++k, new SqlParam(e, fields.get(e)));
            }
            return new JdbcStatement(iStmt.getText(), iStmt.getMap(), oMap);
        } else {
            throw new InvalidQueryException("Invalid query format: " + getExecSql());
        }
    }

    @Override
    public void writeMethod(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) {
        delegateSelectSignature.signature(ipw, jdbc, name);

        if (mc.oSize() == 1) {
            jdbc.getOMap().forEach((k, v) -> ipw.putf("%s<%s> %s(", cc.optional(), v.getType().getWrapper(), name));
        } else {
            ipw.putf("%s<O> %s(", cc.optional(), name);
        }
        ipw.commaReset();

        declareInput(ipw, jdbc);
        declareOutput(ipw);
        ipw.more();
        debugAction(ipw, kPrg, jdbc);
        ipw.printf("try (PreparedStatement ps = c.prepareStatement(Q_%s, Statement.RETURN_GENERATED_KEYS)) {%n", kPrg);
        ipw.more();
        setInput(ipw, jdbc);
        setQueryHints(ipw);
        ipw.printf("ps.executeUpdate();%n");
        ipw.printf("ResultSet rs = ps.getGeneratedKeys();%n");
        ipw.printf("if (rs.next()) {%n");
        ipw.more();
        ipw.printf("if (rs.getMetaData().getColumnType(1) == Types.ROWID) throw new IllegalArgumentException(\"Unsupported operation\");%n");
        fetch(ipw, jdbc.getOMap());
        ipw.printf("return %s.of(o);%n", cc.optional());
        ipw.ends();
        ipw.printf("return %s.empty();%n", cc.optional());
        ipw.ends();
        ipw.ends();
    }
}
