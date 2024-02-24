package io.github.epi155.emsql.pojo.dml;

import io.github.epi155.emsql.api.DeleteBatchModel;
import io.github.epi155.emsql.api.InputModel;
import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import io.github.epi155.emsql.pojo.JdbcStatement;
import io.github.epi155.emsql.pojo.SqlAction;
import io.github.epi155.emsql.pojo.Tools;
import lombok.Getter;
import lombok.Setter;
import org.apache.maven.plugin.MojoExecutionException;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static io.github.epi155.emsql.pojo.Tools.cc;
import static io.github.epi155.emsql.pojo.Tools.mc;

public class SqlDeleteBatch extends SqlAction implements ApiDelete, DeleteBatchModel {
    private final DelegateDelete delegateDelete;
    @Getter
    @Setter
    private InputModel input;
    @Setter
    private int batchSize = 1024;

    public SqlDeleteBatch() {
        super();
        this.delegateDelete = new DelegateDelete(this);
    }

    @Override
    public JdbcStatement sql(Map<String, SqlDataType> fields) throws MojoExecutionException {
        return delegateDelete.proceed(fields);
    }

    @Override
    public void writeMethod(PrintModel ipw, String name, @NotNull JdbcStatement jdbc, String kPrg) {
        int nSize = mc.nSize();
        if (1<nSize && nSize<=IMAX) {
            cc.add("io.github.epi155.emsql.runtime.SqlDeleteBatch"+nSize);
        } else {
            cc.add("io.github.epi155.emsql.runtime.SqlDeleteBatch");
        }
        String cName = Tools.capitalize(name);
        docBegin(ipw);
        docInput(ipw, jdbc);
        docEnd(ipw);
        declareNewInstance(ipw, "SqlDeleteBatch", jdbc, cName);
        ipw.more();
        ipw.printf("PreparedStatement ps = c.prepareStatement(Q_%s);%n", kPrg);
        setQueryHints(ipw);
        declareReturnNew(ipw, "SqlDeleteBatch", jdbc, batchSize);
        ipw.more();
        ipw.printf("@Override%n");
        ipw.printf("public void lazyDelete(%n");
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
