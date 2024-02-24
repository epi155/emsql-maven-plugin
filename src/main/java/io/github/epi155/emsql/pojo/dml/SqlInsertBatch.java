package io.github.epi155.emsql.pojo.dml;

import io.github.epi155.emsql.api.InputModel;
import io.github.epi155.emsql.api.InsertBatchModel;
import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import io.github.epi155.emsql.pojo.JdbcStatement;
import io.github.epi155.emsql.pojo.SqlAction;
import io.github.epi155.emsql.pojo.Tools;
import lombok.Getter;
import lombok.Setter;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.Map;

import static io.github.epi155.emsql.pojo.Tools.cc;
import static io.github.epi155.emsql.pojo.Tools.mc;

public class SqlInsertBatch extends SqlAction implements ApiInsert, InsertBatchModel {
    private final DelegateInsert delegateInsert;
    @Setter
    @Getter
    private InputModel input;
    @Setter
    private int batchSize = 1024;

    public SqlInsertBatch() {
        super();
        this.delegateInsert = new DelegateInsert(this);
    }
    @Override
    public JdbcStatement sql(Map<String, SqlDataType> fields) throws MojoExecutionException {
        return delegateInsert.proceed(fields);
    }
    @Override
    public void writeMethod(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) {
        int nSize = mc.nSize();
        if (1<nSize && nSize<=IMAX) {
            cc.add("io.github.epi155.emsql.runtime.SqlInsertBatch"+nSize);
        } else {
            cc.add("io.github.epi155.emsql.runtime.SqlInsertBatch");
        }
        String cName = Tools.capitalize(name);
        docBegin(ipw);
        docInput(ipw, jdbc);
        docEnd(ipw);
        declareNewInstance(ipw, "SqlInsertBatch", jdbc, cName);
        ipw.more();
        ipw.printf("PreparedStatement ps = c.prepareStatement(Q_%s);%n", kPrg);
        setQueryHints(ipw);
        declareReturnNew(ipw, "SqlInsertBatch", jdbc, batchSize);
        ipw.more();
        ipw.printf("@Override%n");
        ipw.printf("public void lazyInsert(%n");
        declareInputBatch(ipw, jdbc);
        ipw.closeParenthesisLn();
        ipw.printf("        throws SQLException {%n");
        ipw.more();
        setInput(ipw, jdbc);
        ipw.printf("addBatch();%n");
        ipw.ends();
        ipw.less();
        ipw.printf("};%n");
        ipw.ends();
    }
}
