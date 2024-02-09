package io.github.epi155.emsql.plugin.sql.dml;

import io.github.epi155.emsql.plugin.ClassContext;
import io.github.epi155.emsql.plugin.ComAreaStd;
import io.github.epi155.emsql.plugin.IndentPrintWriter;
import io.github.epi155.emsql.plugin.Tools;
import io.github.epi155.emsql.plugin.sql.JdbcStatement;
import io.github.epi155.emsql.plugin.sql.SqlAction;
import io.github.epi155.emsql.plugin.sql.SqlKind;
import lombok.Getter;
import lombok.Setter;
import org.apache.maven.plugin.MojoExecutionException;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class SqlDeleteBatch extends SqlAction implements ApiDelete {
    private final DelegateDelete delegateDelete;
    @Getter
    @Setter
    private ComAreaStd input;
    @Setter
    private int batchSize = 1024;

    SqlDeleteBatch() {
        super();
        this.delegateDelete = new DelegateDelete(this);
    }

    @Override
    public JdbcStatement sql(Map<String, SqlKind> fields) throws MojoExecutionException {
        return delegateDelete.proceed(fields);
    }

    @Override
    public void writeMethod(IndentPrintWriter ipw, String name, @NotNull JdbcStatement jdbc, String kPrg, ClassContext cc) {
        int nSize = jdbc.getNameSize();
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
        declareReturnNew(ipw, cc, "SqlDeleteBatch", jdbc, batchSize);
        ipw.more();
        ipw.printf("@Override%n");
        ipw.printf("public void lazyDelete(%n");
        declareInputBatch(ipw, jdbc);
        ipw.closeParenthesisLn();
        ipw.printf("        throws SQLException {%n");
        ipw.more();
        setInput(ipw, jdbc, cc);
        ipw.printf("addBatch();%n");
        ipw.ends();
        ipw.less();
        ipw.printf("};%n");
        ipw.ends();
    }


}
