package io.github.epi155.emsql.plugin.sql.dml;

import io.github.epi155.emsql.plugin.*;
import io.github.epi155.emsql.plugin.sql.JdbcStatement;
import io.github.epi155.emsql.plugin.sql.SqlAction;
import io.github.epi155.emsql.plugin.sql.SqlEnum;
import io.github.epi155.emsql.plugin.sql.SqlParam;
import io.github.epi155.emsql.plugin.sql.dql.ApiSelectSignature;
import io.github.epi155.emsql.plugin.sql.dql.DelegateSelectSignature;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlInsertReturnGeneratedKeys extends SqlAction implements ApiSelectSignature {
    private final DelegateSelectSignature delegateSelectSignature;
    @Setter
    @Getter
    private ComAreaStd input;
    @Setter
    @Getter
    private ComAreaDef output;

    SqlInsertReturnGeneratedKeys() {
        super();
        this.delegateSelectSignature = new DelegateSelectSignature(this);
    }

    private static final String tmpl =
            "^INSERT INTO (\\w+) \\((.*)\\) VALUES \\((.*)\\)$";
    private static final Pattern regx = Pattern.compile(tmpl, Pattern.CASE_INSENSITIVE);
    @Override
    public JdbcStatement sql(Map<String, SqlEnum> fields) throws MojoExecutionException {
        String nText = Tools.oneLine(getExecSql());
        Matcher m = regx.matcher(nText);
        if (m.find()) {
            String sTable = m.group(1);
            String sCols  = m.group(2).trim();
            String sParms = m.group(3).trim();
            String oText = "INSERT INTO "+sTable+" ( "+sCols+" ) VALUES ( "+sParms+" )";
            Tools.SqlStatement iStmt = Tools.replacePlaceholder(oText, fields);
            Map<Integer, SqlParam> oMap = new LinkedHashMap<>();
            int k=0;
            for(val e: output.getFields()) {
                oMap.put(++k, new SqlParam(e, fields.get(e)));
            }
            return new JdbcStatement(iStmt.getText(), iStmt.getMap(), oMap);
        } else {
            throw new MojoExecutionException("Invalid query format: "+ getExecSql());
        }
    }

    @Override
    public void writeMethod(IndentPrintWriter ipw, String name, JdbcStatement jdbc, String kPrg, ClassContext cc) {
        delegateSelectSignature.signature(ipw, jdbc, name);

        if (jdbc.getOutSize() == 1) {
            jdbc.getOMap().forEach((k,v) -> ipw.putf("%s<%s> %s(%n", cc.optional(), v.getType().getWrapper(), name));
        } else {
            ipw.putf("%s<O> %s(%n", cc.optional(), name);
        }

        ipw.printf("        final Connection c");
        declareInput(ipw, jdbc.getIMap(), Tools.capitalize(name));
        declareOutput(ipw, jdbc.getOutSize(), cc);
        ipw.more();
        ipw.printf("try (PreparedStatement ps = c.prepareStatement(Q_%s, Statement.RETURN_GENERATED_KEYS)) {%n", kPrg);
        ipw.more();
        setInput(ipw, jdbc.getIMap());
        if (getTimeout() != null) ipw.printf("ps.setQueryTimeout(%d);%n", getTimeout());
        debugAction(ipw, kPrg, jdbc.getIMap(), cc);
        ipw.printf("ps.executeUpdate();%n");
        ipw.printf("ResultSet rs = ps.getGeneratedKeys();%n");
        ipw.printf("if (rs.next()) {%n");
        ipw.more();
        ipw.printf("if (rs.getMetaData().getColumnType(1) == Types.ROWID) throw new IllegalArgumentException(\"Unsupported operation\");%n");
        fetch(ipw, jdbc.getOMap(), cc);
        ipw.printf("return %s.of(o);%n", cc.optional());
        ipw.ends();
        ipw.printf("return %s.empty();%n", cc.optional());
        ipw.ends();
        ipw.ends();
    }
}
