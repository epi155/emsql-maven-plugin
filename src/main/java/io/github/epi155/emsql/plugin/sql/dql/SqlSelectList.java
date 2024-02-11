package io.github.epi155.emsql.plugin.sql.dql;

import io.github.epi155.emsql.plugin.ComAreaLst;
import io.github.epi155.emsql.plugin.ComAreaStd;
import io.github.epi155.emsql.plugin.IndentPrintWriter;
import io.github.epi155.emsql.plugin.sql.JdbcStatement;
import io.github.epi155.emsql.plugin.sql.SqlAction;
import io.github.epi155.emsql.plugin.sql.SqlKind;
import lombok.Getter;
import lombok.Setter;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.Map;

import static io.github.epi155.emsql.plugin.Tools.cc;
import static io.github.epi155.emsql.plugin.Tools.mc;

public class SqlSelectList extends SqlAction implements ApiSelectFields, ApiSelectSignature {
    private final DelegateSelectFields delegateSelectFields;
    private final DelegateSelectSignature delegateSelectSignature;
    @Getter
    @Setter
    private ComAreaStd input;
    @Getter
    @Setter
    private ComAreaLst output;
    @Setter
    private Integer fetchSize;

    SqlSelectList() {
        super();
        this.delegateSelectFields = new DelegateSelectFields(this);
        this.delegateSelectSignature = new DelegateSelectSignature(this);
    }

    @Override
    public JdbcStatement sql(Map<String, SqlKind> fields) throws MojoExecutionException {
        return delegateSelectFields.sql(fields);
    }

    @Override
    public void writeMethod(IndentPrintWriter ipw, String name, JdbcStatement jdbc, String kPrg) {
        cc.add("java.util.List");
        cc.add("java.util.ArrayList");
        delegateSelectSignature.signature(ipw, jdbc, name);

        if (mc.oSize() == 1) {
            jdbc.getOMap().forEach((k,v) -> ipw.putf("List<%s> %s(%n", v.getType().getWrapper(), name));
        } else {
            ipw.putf("List<O> %s(%n", name);
        }

        ipw.printf("        final Connection c");
        declareInput(ipw, jdbc);
        declareOutput(ipw);
        ipw.more();
        openQuery(ipw, jdbc, kPrg);
        ipw.more();
        setInput(ipw, jdbc);
        if (fetchSize != null) ipw.printf("ps.setFetchSize(%d);%n", fetchSize);
        setQueryHints(ipw);
        debugAction(ipw, kPrg, jdbc);
        ipw.printf("try (ResultSet rs = ps.executeQuery()) {%n");
        ipw.more();
        if (mc.oSize() == 1) {
            jdbc.getOMap().forEach((k,v) ->
                    ipw.printf("List<%s> list = new ArrayList<>();%n", v.getType().getWrapper()));
        } else {
            ipw.printf("List<O> list = new ArrayList<>();%n");
        }
        ipw.printf("while (rs.next()) {%n");
        ipw.more();
        fetch(ipw, jdbc.getOMap());
        ipw.printf("list.add(o);%n");
        ipw.ends();
        ipw.printf("return list;%n");
        ipw.ends();
        ipw.ends();
        ipw.ends();
    }
}
