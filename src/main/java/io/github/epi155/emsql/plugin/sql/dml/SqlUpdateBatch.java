package io.github.epi155.emsql.plugin.sql.dml;

import io.github.epi155.emsql.plugin.ClassContext;
import io.github.epi155.emsql.plugin.ComAreaStd;
import io.github.epi155.emsql.plugin.IndentPrintWriter;
import io.github.epi155.emsql.plugin.Tools;
import io.github.epi155.emsql.plugin.sql.JdbcStatement;
import io.github.epi155.emsql.plugin.sql.SqlAction;
import io.github.epi155.emsql.plugin.sql.SqlEnum;
import lombok.Getter;
import lombok.Setter;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.Map;

public class SqlUpdateBatch extends SqlAction implements ApiUpdate {
    private final DelegateUpdate delegateUpdate;
    @Getter
    @Setter
    private ComAreaStd input;
    @Setter
    private int batchSize = 1024;

    SqlUpdateBatch() {
        super();
        this.delegateUpdate = new DelegateUpdate(this);
    }

    @Override
    public JdbcStatement sql(Map<String, SqlEnum> fields) throws MojoExecutionException {
        return delegateUpdate.proceed(fields);
    }

    @Override
    public void writeMethod(IndentPrintWriter ipw, String name, JdbcStatement jdbc, String kPrg, ClassContext cc) {
        int nSize = jdbc.getNameSize();
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
        declareReturnNew(ipw, cc, "SqlUpdateBatch", jdbc, batchSize);
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
