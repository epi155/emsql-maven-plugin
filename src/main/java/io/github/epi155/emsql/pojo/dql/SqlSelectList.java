package io.github.epi155.emsql.pojo.dql;

import io.github.epi155.emsql.api.*;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.dql.ApiDocSignature;
import io.github.epi155.emsql.commons.dql.ApiSelectFields;
import io.github.epi155.emsql.commons.dql.DelegateSelectFields;
import io.github.epi155.emsql.pojo.PojoAction;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

import static io.github.epi155.emsql.commons.Contexts.cc;
import static io.github.epi155.emsql.commons.Contexts.mc;

public class SqlSelectList extends PojoAction implements ApiSelectFields, ApiDocSignature, SelectListModel {
    private final DelegateSelectFields delegateSelectFields;
    private final DelegateSelectSignature delegateSelectSignature;
    @Getter
    @Setter
    private InputModel input;
    @Getter
    @Setter
    private OutputModel output;
    @Setter
    private Integer fetchSize;

    public SqlSelectList() {
        super();
        this.delegateSelectFields = new DelegateSelectFields(this);
        this.delegateSelectSignature = new DelegateSelectSignature(this);
    }

    @Override
    public JdbcStatement sql(Map<String, SqlDataType> fields) throws InvalidQueryException {
        return delegateSelectFields.sql(fields);
    }

    @Override
    public void writeMethod(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) {
        cc.add("java.util.List");
        cc.add("java.util.ArrayList");
        delegateSelectSignature.signature(ipw, jdbc, name);

        if (mc.oSize() == 1) {
            jdbc.getOMap().forEach((k, v) -> ipw.putf("List<%s> %s(%n", v.getType().getWrapper(), name));
        } else {
            ipw.putf("List<O> %s(%n", name);
        }

        ipw.printf("        final Connection c");
        declareInput(ipw, jdbc);
        declareOutput(ipw);
        ipw.more();
        debugAction(ipw, kPrg, jdbc);
        openQuery(ipw, jdbc, kPrg);
        ipw.more();
        setInput(ipw, jdbc);
        if (fetchSize != null) ipw.printf("ps.setFetchSize(%d);%n", fetchSize);
        setQueryHints(ipw);
        ipw.printf("try (ResultSet rs = ps.executeQuery()) {%n");
        ipw.more();
        if (mc.oSize() == 1) {
            jdbc.getOMap().forEach((k, v) ->
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
