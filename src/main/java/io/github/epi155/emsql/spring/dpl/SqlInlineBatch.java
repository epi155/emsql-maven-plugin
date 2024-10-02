package io.github.epi155.emsql.spring.dpl;

import io.github.epi155.emsql.api.*;
import io.github.epi155.emsql.commons.JdbcStatement;
import io.github.epi155.emsql.commons.Tools;
import io.github.epi155.emsql.commons.dpl.ApiInline;
import io.github.epi155.emsql.commons.dpl.DelegateInline;
import io.github.epi155.emsql.spring.SpringBatchAction;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

import static io.github.epi155.emsql.commons.Contexts.*;

@Setter
public class SqlInlineBatch extends SpringBatchAction
        implements ApiInline, InlineBatchModel {

    private final DelegateInline delegateInline;
    @Setter
    @Getter
    private InputModel input;
    @Setter @Getter
    private OutFieldsModel output;
    @Setter @Getter
    private InOutFieldsModel inputOutput;

    public SqlInlineBatch() {
        super();
        this.delegateInline = new DelegateInline(this);
    }

    @Override
    public JdbcStatement sql(Map<String, SqlDataType> fields) throws InvalidQueryException {
        return delegateInline.proceed(fields);
    }

    @Override
    public void writeMethod(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) {
        int nSize = mc.nSize();
        if (nSize<=IMAX) {
            cc.add("io.github.epi155.emsql.runtime.SqlInlineBatch"+nSize);
        } else {
            cc.add("io.github.epi155.emsql.runtime.SqlInlineBatch1");
        }
        String cName = Tools.capitalize(name);
        docBegin(ipw);
        docInput(ipw, jdbc);
        docEnd(ipw);
        declareNewInstance(ipw, cName);
        ipw.more();
        cc.add("org.springframework.jdbc.datasource.DataSourceUtils");
        ipw.printf("final Connection c = DataSourceUtils.getConnection(dataSource);%n");
        ipw.printf("final CallableStatement ps = c.prepareCall(Q_%s);%n", kPrg);
        setQueryHints(ipw);
        ipw.printf("return new %s", cName);
        batchGeneric(ipw);
        ipw.putf("(ps);%n");
        ipw.ends();
        declareNextClass(ipw, cName, "SqlInlineBatch", jdbc, batchSize, kPrg);
        ipw.printf("@Override%n");
        ipw.printf("public void lazyInline(%n");
        declareInputBatch(ipw, jdbc);
        ipw.closeParenthesisLn();
        ipw.printf("        throws SQLException {%n");
        ipw.more();
        setInputAbs(ipw, jdbc);
        debugAction(ipw, kPrg, jdbc);
        ipw.printf("addBatch();%n");
        ipw.ends();
        ipw.ends();
    }
}
