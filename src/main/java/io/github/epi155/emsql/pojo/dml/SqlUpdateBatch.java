package io.github.epi155.emsql.pojo.dml;

import io.github.epi155.emsql.api.InputModel;
import io.github.epi155.emsql.api.PrintModel;
import io.github.epi155.emsql.api.SqlDataType;
import io.github.epi155.emsql.api.UpdateBatchModel;
import io.github.epi155.emsql.pojo.JdbcStatement;
import io.github.epi155.emsql.pojo.SqlAction;
import io.github.epi155.emsql.pojo.Tools;
import lombok.Getter;
import lombok.Setter;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.Map;

import static io.github.epi155.emsql.pojo.Tools.cc;
import static io.github.epi155.emsql.pojo.Tools.mc;

public class SqlUpdateBatch extends SqlAction implements ApiUpdate, UpdateBatchModel {
    private final DelegateUpdate delegateUpdate;
    @Getter
    @Setter
    private InputModel input;
    @Setter
    private int batchSize = 1024;

    public SqlUpdateBatch() {
        super();
        this.delegateUpdate = new DelegateUpdate(this);
    }

    @Override
    public JdbcStatement sql(Map<String, SqlDataType> fields) throws MojoExecutionException {
        return delegateUpdate.proceed(fields);
    }

    @Override
    public void writeMethod(PrintModel ipw, String name, JdbcStatement jdbc, String kPrg) {
        int nSize = mc.nSize();
        if (1<nSize && nSize<=IMAX) {
            cc.add("io.github.epi155.emsql.runtime.SqlUpdateBatch"+nSize);
        } else {
            cc.add("io.github.epi155.emsql.runtime.SqlUpdateBatch");
        }
        String cName = Tools.capitalize(name);
        docBegin(ipw);
        docInput(ipw, jdbc);
        docEnd(ipw);
        declareNewInstance(ipw, "SqlUpdateBatch", jdbc, cName);
        ipw.more();
        ipw.printf("PreparedStatement ps = c.prepareStatement(Q_%s);%n", kPrg);
        setQueryHints(ipw);
        declareReturnNew(ipw, "SqlUpdateBatch", jdbc, batchSize);
        ipw.more();
        ipw.printf("@Override%n");
        ipw.printf("public void lazyUpdate(%n");
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
