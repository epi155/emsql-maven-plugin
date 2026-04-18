package io.github.epi155.emsql.spring.dql;

import io.github.epi155.emsql.api.InvalidQueryException;
import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SelectListModel;
import io.github.epi155.emsql.api.SqlDataType;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.dql.ApiDocSignature;
import io.github.epi155.emsql.commons.dql.ApiSelectFields;
import io.github.epi155.emsql.commons.dql.DelegateSelectFields;
import io.github.epi155.emsql.spring.SpringAction;
import lombok.Setter;

import java.util.Map;

import static io.github.epi155.emsql.commons.Contexts.cc;
import static io.github.epi155.emsql.commons.Contexts.mc;

public class SqlSelectList extends SpringAction implements ApiSelectFields, ApiDocSignature, SelectListModel {
    private final DelegateSelectFields delegateSelectFields;
    private final DelegateSelectSignature delegateSelectSignature;
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
    public void writeMethod(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) throws InvalidQueryException {
        cc.add("java.util.List");
        cc.add("java.util.ArrayList");
        delegateSelectSignature.signature(ipw, jdbc, name);

        if (mc.oSize() == 1) {
            jdbc.getOMap().forEach((k, v) -> ipw.putf("List<%s> %s(", v.getType().getWrapper(), name));
        } else {
            ipw.putf("List<O> %s(", name);
        }
        ipw.commaReset();

        declareInput(ipw, jdbc);
        declareOutput(ipw);
        ipw.more();
        cc.add("org.springframework.jdbc.datasource.DataSourceUtils");
        ipw.printf("final Connection c = DataSourceUtils.getConnection(dataSource);%n");
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
        dumpAction(ipw, kPrg, jdbc);
        ipw.ends(); // end method
    }
}
